package genandnic.walljump.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketFallDistance implements IMessage {

    public PacketFallDistance() {

    }

    private float fallDistance;

    public PacketFallDistance(float fallDistance) {
        this.fallDistance = fallDistance;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(fallDistance);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        fallDistance = buf.readFloat();
    }

    public static class PacketFallDistanceHandler implements IMessageHandler<PacketFallDistance, IMessage> {

        @Override
        public IMessage onMessage(PacketFallDistance message, MessageContext context) {

            EntityPlayerMP serverPlayer = context.getServerHandler().player;

            serverPlayer.getServerWorld().addScheduledTask(() -> {
                serverPlayer.fallDistance = message.fallDistance;
            });

            return null;
        }

    }
}
