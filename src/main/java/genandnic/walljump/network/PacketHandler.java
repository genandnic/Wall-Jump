package genandnic.walljump.network;

import genandnic.walljump.WallJump;
import genandnic.walljump.network.message.IMessage;
import genandnic.walljump.network.message.MessageFallDistance;
import genandnic.walljump.network.message.MessageWallJump;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class PacketHandler {

    public static final String PROTOCOL_VERSION = "1";

    public static SimpleChannel INSTANCE;
    private static int nextId = 0;

    public static void init()
    {
        INSTANCE = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(WallJump.MOD_ID, "network"))
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .simpleChannel();
        register(MessageFallDistance.class, new MessageFallDistance());
        register(MessageWallJump.class, new MessageWallJump());
    }

    private static <T> void register(Class<T> clazz, IMessage<T> message)
    {
        INSTANCE.registerMessage(nextId++, clazz, message::encode, message::decode, message::handle);
    }

}
