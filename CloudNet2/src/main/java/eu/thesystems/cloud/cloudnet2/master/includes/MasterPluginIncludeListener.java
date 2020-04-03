package eu.thesystems.cloud.cloudnet2.master.includes;

import de.dytanic.cloudnet.event.IEventListener;
import de.dytanic.cloudnetcore.api.event.network.WrapperChannelInitEvent;

public class MasterPluginIncludeListener implements IEventListener<WrapperChannelInitEvent> {
    @Override
    public void onCall(WrapperChannelInitEvent event) {
        event.getChannel().pipeline().addLast(new MasterServerTemplateModifier());
    }
}
