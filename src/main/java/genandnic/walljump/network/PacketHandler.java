package genandnic.walljump.network;

import genandnic.walljump.WallJump;
import genandnic.walljump.network.message.IMessage;
import genandnic.walljump.network.message.MessageFallDistance;
import genandnic.walljump.network.message.MessageWallJump;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static int nextId = 0;
    public static SimpleChannel instance;

    public static void init()
    {
        instance = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(WallJump.MOD_ID, "network"))
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .simpleChannel();

        register(MessageWallJump.class, new MessageWallJump());
        register(MessageFallDistance.class, new MessageFallDistance());
    }

    private static <T> void register(Class<T> clazz, IMessage<T> message)
    {
        instance.registerMessage(nextId++, clazz, message::encode, message::decode, message::handle);
    }
}
