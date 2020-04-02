package eu.thesystems.cloud.cloudnet3.cluster.node;

import com.google.common.base.Preconditions;
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
import eu.thesystems.cloud.cloudnet3.cluster.channel.RedirectingNetworkChannel;
import eu.thesystems.cloud.cloudnet3.cluster.packet.PacketClusterInRedirectPacket;
import eu.thesystems.cloud.cloudnet3.cluster.packet.PacketClusterOutRedirectPacket;
import eu.thesystems.cloud.cloudnet3.node.CloudNet3Node;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NodeClusterPacketProvider implements ClusterPacketProvider {

    private CloudNet3Node node;
    private CloudNet cloudNet;
    private ClusterPacketReceiver localPacketSender;

    public NodeClusterPacketProvider(CloudNet3Node node) {
        this.node = node;
        this.cloudNet = node.getCloudNet();
        this.localPacketSender = new ClusterPacketReceiver(DriverEnvironment.CLOUDNET, node.getOwnComponentName());
    }

    @NotNull
    @Override
    public ClusterPacketReceiver getLocalPacketSender() {
        return this.localPacketSender;
    }


    @EventListener
    public void handleNodeConnected(NetworkChannelAuthClusterNodeSuccessEvent event) {
        event.getChannel().getPacketRegistry().addListener(PacketClusterOutRedirectPacket.CHANNEL, new PacketClusterInRedirectPacket(this.node));
    }

    @EventListener
    public void handleServiceConnected(NetworkChannelAuthCloudServiceSuccessEvent event) {
        event.getChannel().getPacketRegistry().addListener(PacketClusterOutRedirectPacket.CHANNEL, new PacketClusterInRedirectPacket(this.node));
    }

    @NotNull
    @Override
    public PacketSendResult sendPacket(ClusterPacketReceiver packetSender, ClusterPacketReceiver receiver, IPacket packet) {
        if (packetSender == null) {
            packetSender = this.getLocalPacketSender();
        }

        if (receiver.getTargetType() == DriverEnvironment.CLOUDNET) {
            ChannelResult<IClusterNodeServer> receiverServer = this.getNodeServer(receiver.getTargetIdentifier());

            if (receiverServer.getAlternative() == null) {
                return receiverServer.getResult();
            }

            if (receiver.getTargetIdentifier().equals(this.cloudNet.getCurrentNetworkClusterNodeInfoSnapshot().getNode().getUniqueId())) {
                INetworkChannel channel = this.getSenderChannel(packetSender);

                this.cloudNet.getNetworkServer().getPacketRegistry().handlePacket(channel, packet);

                return PacketSendResult.SUCCESS;
            }

            receiverServer.getAlternative().saveSendPacket(packet);
            return PacketSendResult.SUCCESS;
        } else if (receiver.getTargetType() == DriverEnvironment.WRAPPER) {
            ChannelResult<IClusterNodeServer> result = this.sendPacketToWrapper(packetSender, receiver, packet);
            if (result.getAlternative() != null) {
                result.getAlternative().saveSendPacket(new PacketClusterOutRedirectPacket(packetSender, receiver, packet));
                return PacketSendResult.SUCCESS;
            }

            return result.getResult();
        }
        return PacketSendResult.INVALID_TARGET_TYPE;
    }

    @NotNull
    @Override
    public PacketSendResult[] sendMultiplePackets(ClusterPacketReceiver packetSender, ClusterPacketReceiver[] receivers, IPacket packet) {
        if (receivers.length == 0) {
            return new PacketSendResult[0];
        }
        if (receivers.length == 1) {
            return new PacketSendResult[]{this.sendPacket(packetSender, receivers[0], packet)};
        }

        if (packetSender == null) {
            packetSender = this.getLocalPacketSender();
        }

        PacketSendResult[] results = new PacketSendResult[receivers.length];

        Map<String, Collection<IndexedPacketReceiver>> receiversByNode = new HashMap<>();

        for (int i = 0; i < receivers.length; i++) {
            ClusterPacketReceiver receiver = receivers[i];

            if (receiver.getTargetType() == DriverEnvironment.CLOUDNET) {
                receiversByNode.computeIfAbsent(receiver.getTargetIdentifier(), s -> new ArrayList<>())
                        .add(new IndexedPacketReceiver(i, receiver));
            } else if (receiver.getTargetType() == DriverEnvironment.WRAPPER) {
                ChannelResult<IClusterNodeServer> result = this.sendPacketToWrapper(packetSender, receiver, packet);
                if (result.getAlternative() != null) {
                    receiversByNode.computeIfAbsent(result.getAlternative().getNodeInfo().getUniqueId(), s -> new ArrayList<>())
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
                    packetSender,
                    entry.getValue().stream().map(IndexedPacketReceiver::getReceiver).toArray(ClusterPacketReceiver[]::new),
                    packet
            ));
            for (IndexedPacketReceiver receiver : entry.getValue()) {
                results[receiver.getIndex()] = PacketSendResult.SUCCESS;
            }
        }

        return results;
    }

    @NotNull
    private INetworkChannel getSenderChannel(ClusterPacketReceiver packetSender) {
        if (packetSender.getTargetType() == DriverEnvironment.CLOUDNET) {
            ChannelResult<IClusterNodeServer> senderServer = this.getNodeServer(packetSender.getTargetIdentifier());
            Preconditions.checkNotNull(senderServer.getAlternative(), "Sender Node " + packetSender.getTargetIdentifier() + " not found: " + senderServer.getResult());
            return senderServer.getAlternative().getChannel();
        } else if (packetSender.getTargetType() == DriverEnvironment.WRAPPER) {
            ICloudService service = this.cloudNet.getCloudServiceManager().getCloudService(cloudService -> cloudService.getServiceId().getName().equalsIgnoreCase(packetSender.getTargetIdentifier()));
            if (service != null) {
                Preconditions.checkNotNull(service.getNetworkChannel(), "Sender Service " + packetSender.getTargetIdentifier() + " is not connected");
                return service.getNetworkChannel();
            } else {
                ServiceInfoSnapshot serviceInfoSnapshot = this.cloudNet.getCloudServiceProvider(packetSender.getTargetIdentifier()).getServiceInfoSnapshot();
                Preconditions.checkNotNull(serviceInfoSnapshot, "Sender Service " + packetSender.getTargetIdentifier() + " not found");

                IClusterNodeServer server = this.cloudNet.getClusterNodeServerProvider().getNodeServer(serviceInfoSnapshot.getServiceId().getNodeUniqueId());
                Preconditions.checkNotNull(server, "Sender Service " + packetSender.getTargetIdentifier() + " runs on a Node which doesn't exist");
                Preconditions.checkNotNull(server.getChannel(), "Sender Service " + packetSender.getTargetIdentifier() + " runs on Node which is not connected");

                return new RedirectingNetworkChannel(packetSender, this.cloudNet, server.getChannel(), this.getReceiver(serviceInfoSnapshot));
            }
        }
        throw new IllegalArgumentException("Sender must be of the type WRAPPER or CLOUDNET");
    }

    @NotNull
    private ChannelResult<IClusterNodeServer> getNodeServer(String name) {
        IClusterNodeServer server = this.cloudNet.getClusterNodeServerProvider().getNodeServer(name);
        if (server == null) {
            return new ChannelResult<>(PacketSendResult.RECEIVER_NOT_FOUND, null);
        }
        if (server.getChannel() == null) {
            return new ChannelResult<>(PacketSendResult.RECEIVER_OFFLINE, null);
        }
        return new ChannelResult<>(PacketSendResult.SUCCESS, server);
    }

    @NotNull
    private ChannelResult<IClusterNodeServer> sendPacketToWrapper(ClusterPacketReceiver sender, ClusterPacketReceiver receiver, IPacket packet) {
        ICloudService service = this.cloudNet.getCloudServiceManager().getCloudService(cloudService -> cloudService.getServiceId().getName().equalsIgnoreCase(receiver.getTargetIdentifier()));
        if (service != null) {
            if (service.getNetworkChannel() == null) {
                return new ChannelResult<>(PacketSendResult.RECEIVER_OFFLINE, null);
            }

            if (sender.getTargetType() == DriverEnvironment.CLOUDNET &&
                    sender.getTargetIdentifier().equals(this.cloudNet.getCurrentNetworkClusterNodeInfoSnapshot().getNode().getUniqueId())) {
                service.getNetworkChannel().sendPacket(packet);
            } else {
                service.getNetworkChannel().sendPacket(new PacketClusterOutRedirectPacket(sender, receiver, packet)); // send a redirect packet instead of the packet directly to tell the wrapper that the packet is not from this node
            }
            return new ChannelResult<>(PacketSendResult.SUCCESS, null);
        }

        ServiceInfoSnapshot serviceInfoSnapshot = this.cloudNet.getCloudServiceProvider(receiver.getTargetIdentifier()).getServiceInfoSnapshot();
        if (serviceInfoSnapshot == null) {
            return new ChannelResult<>(PacketSendResult.RECEIVER_NOT_FOUND, null);
        }

        IClusterNodeServer server = this.cloudNet.getClusterNodeServerProvider().getNodeServer(serviceInfoSnapshot.getServiceId().getNodeUniqueId());
        if (server == null) {
            return new ChannelResult<>(PacketSendResult.RECEIVER_NOT_FOUND, null);
        }

        return new ChannelResult<>(PacketSendResult.SUCCESS, server);
    }

}
