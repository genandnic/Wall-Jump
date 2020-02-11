package genandnic.walljump.network.message;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageFallDistance implements IMessage<MessageFallDistance> {

    private float fallDistance;

    public MessageFallDistance() {
    }

    public MessageFallDistance(float fallDistance) {
        this.fallDistance = fallDistance;
    }

    @Override
    public void encode(MessageFallDistance message, PacketBuffer buffer) {
        buffer.writeFloat(message.fallDistance);
    }

    @Override
    public MessageFallDistance decode(PacketBuffer buffer) {
        return new MessageFallDistance(buffer.readFloat());
    }

    @Override
    public void handle(MessageFallDistance message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() ->
        {
            supplier.get().getSender().fallDistance = message.fallDistance;
        });
        supplier.get().setPacketHandled(true);
    }
}
