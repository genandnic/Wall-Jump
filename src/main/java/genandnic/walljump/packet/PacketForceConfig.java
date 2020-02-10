package genandnic.walljump.packet;

import genandnic.walljump.WallJumpConfig;
import genandnic.walljump.client.PlayerDoubleJump;
import genandnic.walljump.client.PlayerSpeedBoost;
import genandnic.walljump.client.PlayerWallJump;
import genandnic.walljump.proxy.ClientProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketForceConfig implements IMessage {

    public PacketForceConfig() {

    }

    public double minFallDistance = WallJumpConfig.minFallDistance;
    public boolean useWallJump = WallJumpConfig.useWallJump;
    public boolean allowReClinging = WallJumpConfig.allowReClinging;
    public double wallJumpBoost = WallJumpConfig.wallJumpBoost;
    public int wallSlideDelay = WallJumpConfig.wallSlideDelay;
    public boolean useDoubleJump = WallJumpConfig.useDoubleJump;
    public double sprintSpeedBoost = WallJumpConfig.sprintSpeedBoost;
    public double elytraSpeedBoost = WallJumpConfig.elytraSpeedBoost;

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(minFallDistance);
        buf.writeBoolean(useWallJump);
        buf.writeBoolean(allowReClinging);
        buf.writeDouble(wallJumpBoost);
        buf.writeInt(wallSlideDelay);
        buf.writeBoolean(useDoubleJump);
        buf.writeDouble(sprintSpeedBoost);
        buf.writeDouble(elytraSpeedBoost);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        minFallDistance = buf.readDouble();
        useWallJump = buf.readBoolean();
        allowReClinging = buf.readBoolean();
        wallJumpBoost = buf.readDouble();
        wallSlideDelay = buf.readInt();
        useDoubleJump = buf.readBoolean();
        sprintSpeedBoost = buf.readDouble();
        elytraSpeedBoost = buf.readDouble();
    }

    public static class PacketForceConfigHandler implements IMessageHandler<PacketForceConfig, IMessage> {

        @Override
        public IMessage onMessage(PacketForceConfig message, MessageContext context) {

            if (!Minecraft.getMinecraft().isIntegratedServerRunning()) {
                WallJumpConfig.isRemote = true;
                ClientProxy.minFallDistance = message.minFallDistance;
                PlayerWallJump.useWallJump = message.useWallJump;
                PlayerWallJump.allowReClinging = message.allowReClinging;
                PlayerWallJump.wallJumpBoost = (float) message.wallJumpBoost;
                PlayerWallJump.wallSlideDelay = message.wallSlideDelay;
                PlayerDoubleJump.useDoubleJump = message.useDoubleJump;
                PlayerSpeedBoost.sprintSpeedBoost = (float) message.sprintSpeedBoost;
                PlayerSpeedBoost.elytraSpeedBoost = (float) message.elytraSpeedBoost;
            }

            return null;
        }

    }
}
