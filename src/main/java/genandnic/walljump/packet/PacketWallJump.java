package genandnic.walljump.packet;

import genandnic.walljump.WallJumpConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketWallJump implements IMessage {

    public PacketWallJump() {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    public static class PacketWallJumpHandler implements IMessageHandler<PacketWallJump, IMessage> {

        @Override
        public IMessage onMessage(PacketWallJump message, MessageContext context) {

            EntityPlayerMP serverPlayer = context.getServerHandler().player;
            serverPlayer.getServerWorld().addScheduledTask(() -> {

                serverPlayer.fallDistance = 0.0F;
                serverPlayer.addExhaustion((float) WallJumpConfig.exhaustionWallJump);

            });

            return null;
        }

    }

}
