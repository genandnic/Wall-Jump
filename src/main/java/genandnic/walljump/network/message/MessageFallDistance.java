package genandnic.walljump.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageFallDistance implements IMessage<MessageFallDistance> {

    private float fallDistance;

    public MessageFallDistance() {
    }

    public MessageFallDistance(float fallDistance) {
        this.fallDistance = fallDistance;
    }

    public MessageFallDistance decode(FriendlyByteBuf buffer) {
        return new MessageFallDistance(buffer.readFloat());
    }

    public void encode(MessageFallDistance message, FriendlyByteBuf buffer) {
        buffer.writeFloat(message.fallDistance);
    }

    public void handle(MessageFallDistance message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() ->
                supplier.get().getSender().fallDistance = message.fallDistance);
        supplier.get().setPacketHandled(true);
    }
}
