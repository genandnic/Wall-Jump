package genandnic.walljump.network.message;

import genandnic.walljump.WallJumpConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageWallJump implements IMessage<MessageWallJump> {

    @Override
    public void encode(MessageWallJump message, PacketBuffer buffer) { }

    @Override
    public MessageWallJump decode(PacketBuffer buffer) {
        return new MessageWallJump();
    }

    @Override
    public void handle(MessageWallJump message, Supplier<NetworkEvent.Context> supplier) {

        supplier.get().enqueueWork(() -> {
            ServerPlayerEntity player = supplier.get().getSender();
            if (player != null) {
                player.fallDistance = 0.0F;
                player.addExhaustion(WallJumpConfig.COMMON.exhaustionWallJump.get().floatValue());
            }
        });

        supplier.get().setPacketHandled(true);
    }

}
