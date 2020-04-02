package eu.thesystems.cloud.cloudnet3.cluster.node;

import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.cluster.IClusterNodeServer;
import de.dytanic.cloudnet.driver.DriverEnvironment;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.network.INetworkChannel;
import de.dytanic.cloudnet.driver.network.protocol.IPacket;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.event.cluster.NetworkChannelAuthClusterNodeSuccessEvent;
import de.dytanic.cloudnet.event.network.NetworkChannelAuthCloudServiceSuccessEvent;
import de.dytanic.cloudnet.service.ICloudService;
import eu.thesystems.cloud.cloudnet3.cluster.*;
import eu.thesystems.cloud.cloudnet3.cluster.node.local.LocalNetworkChannel;
import eu.thesystems.cloud.cloudnet3.cluster.packet.PacketClusterInRedirectPacket;
import eu.thesystems.cloud.cloudnet3.cluster.packet.PacketClusterOutRedirectPacket;
import eu.thesystems.cloud.cloudnet3.node.CloudNet3Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NodeClusterPacketProvider implements ClusterPacketProvider {

    private CloudNet3Node node;
    private CloudNet cloudNet;
    private INetworkChannel localNetworkChannel;

    public NodeClusterPacketProvider(CloudNet3Node node) {
        this.node = node;
        this.cloudNet = node.getCloudNet();
        this.localNetworkChannel = new LocalNetworkChannel(cloudNet.getNetworkServer().getPacketRegistry());
    }

    @Override
    public INetworkChannel getLocalNetworkChannel() {
        return this.localNetworkChannel;
    }


    @EventListener
    public void handleNodeConnected(NetworkChannelAuthClusterNodeSuccessEvent event) {
        event.getChannel().getPacketRegistry().addListener(PacketClusterOutRedirectPacket.CHANNEL, new PacketClusterInRedirectPacket(this.node));
    }

    @EventListener
    public void handleServiceConnected(NetworkChannelAuthCloudServiceSuccessEvent event) {
        event.getChannel().getPacketRegistry().addListener(PacketClusterOutRedirectPacket.CHANNEL, new PacketClusterInRedirectPacket(this.node));
    }

    @Override
    public PacketSendResult sendPacket(INetworkChannel packetSender, ClusterPacketReceiver receiver, IPacket packet) {
        if (receiver.getTargetType() == DriverEnvironment.CLOUDNET) {
            if (receiver.getTargetIdentifier().equals(this.cloudNet.getCurrentNetworkClusterNodeInfoSnapshot().getNode().getUniqueId())) {
                this.cloudNet.getNetworkServer().getPacketRegistry().handlePacket(packetSender, packet);
                return PacketSendResult.SUCCESS;
            }

            IClusterNodeServer nodeServer = this.cloudNet.getClusterNodeServerProvider().getNodeServer(receiver.getTargetIdentifier());
            if (nodeServer == null) {
                return PacketSendResult.RECEIVER_NOT_FOUND;
            }

            nodeServer.saveSendPacket(packet);
            return PacketSendResult.SUCCESS;
        } else if (receiver.getTargetType() == DriverEnvironment.WRAPPER) {
            WrapperSendResult result = this.sendPacketToWrapper(receiver, packet);
            if (result.getServer() != null) {
                result.getServer().saveSendPacket(new PacketClusterOutRedirectPacket(receiver, packet));
                return PacketSendResult.SUCCESS;
            }

            return result.getResult();
        }
        return PacketSendResult.INVALID_TARGET_TYPE;
    }

    @Override
    public PacketSendResult[] sendMultiplePackets(INetworkChannel packetSender, ClusterPacketReceiver[] receivers, IPacket packet) {
        if (receivers.length == 0) {
            return new PacketSendResult[0];
        }
        if (receivers.length == 1) {
            return new PacketSendResult[]{this.sendPacket(packetSender, receivers[0], packet)};
        }

        PacketSendResult[] results = new PacketSendResult[receivers.length];

        Map<String, Collection<IndexedPacketReceiver>> receiversByNode = new HashMap<>();

        for (int i = 0; i < receivers.length; i++) {
            ClusterPacketReceiver receiver = receivers[i];

            if (receiver.getTargetType() == DriverEnvironment.CLOUDNET) {
                receiversByNode.computeIfAbsent(receiver.getTargetIdentifier(), s -> new ArrayList<>())
                        .add(new IndexedPacketReceiver(i, receiver));
            } else if (receiver.getTargetType() == DriverEnvironment.WRAPPER) {
                WrapperSendResult result = this.sendPacketToWrapper(receiver, packet);
                if (result.getServer() != null) {
                    receiversByNode.computeIfAbsent(result.getServer().getNodeInfo().getUniqueId(), s -> new ArrayList<>())
                            .add(new IndexedPacketReceiver(i, receiver));
                    continue;
                }

                results[i] = result.getResult();
            } else {
                results[i] = PacketSendResult.RECEIVER_NOT_FOUND;
            }
        }

        for (Map.Entry<String, Collection<IndexedPacketReceiver>> entry : receiversByNode.entrySet()) {
            IClusterNodeServer server = this.cloudNet.getClusterNodeServerProvider().getNodeServer(entry.getKey());
            if (server == null) {
                for (IndexedPacketReceiver receiver : entry.getValue()) {
                    results[receiver.getIndex()] = PacketSendResult.RECEIVER_NOT_FOUND;
                }
                continue;
            }

            server.saveSendPacket(new PacketClusterOutRedirectPacket(
                    entry.getValue().stream().map(IndexedPacketReceiver::getReceiver).toArray(ClusterPacketReceiver[]::new),
                    packet
            ));
            for (IndexedPacketReceiver receiver : entry.getValue()) {
                results[receiver.getIndex()] = PacketSendResult.SUCCESS;
            }
        }

        return results;
    }

    private WrapperSendResult sendPacketToWrapper(ClusterPacketReceiver receiver, IPacket packet) {
        ICloudService service = this.cloudNet.getCloudServiceManager().getCloudService(cloudService -> cloudService.getServiceId().getName().equalsIgnoreCase(receiver.getTargetIdentifier()));
        if (service != null) {
            if (service.getNetworkChannel() == null) {
                return new WrapperSendResult(PacketSendResult.RECEIVER_OFFLINE, null);
            }

            service.getNetworkChannel().sendPacket(packet);
            return new WrapperSendResult(PacketSendResult.SUCCESS, null);
        }

        ServiceInfoSnapshot serviceInfoSnapshot = this.cloudNet.getCloudServiceProvider(receiver.getTargetIdentifier()).getServiceInfoSnapshot();
        if (serviceInfoSnapshot == null) {
            return new WrapperSendResult(PacketSendResult.RECEIVER_NOT_FOUND, null);
        }

        IClusterNodeServer server = this.cloudNet.getClusterNodeServerProvider().getNodeServer(serviceInfoSnapshot.getServiceId().getNodeUniqueId());
        if (server == null) {
            return new WrapperSendResult(PacketSendResult.RECEIVER_NOT_FOUND, null);
        }

        return new WrapperSendResult(null, server);
    }

}
