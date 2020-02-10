package genandnic.walljump.proxy;

import genandnic.walljump.WallJump;
import genandnic.walljump.WallJumpConfig;
import genandnic.walljump.enchantment.DoubleJumpEnchant;
import genandnic.walljump.enchantment.SpeedBoostEnchant;
import genandnic.walljump.enchantment.WallJumpEnchant;
import genandnic.walljump.packet.PacketFallDistance;
import genandnic.walljump.packet.PacketForceConfig;
import genandnic.walljump.packet.PacketWallJump;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber
public class CommonProxy {

    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(WallJump.MOD_ID);

    public void preInit(FMLPreInitializationEvent event) {

    }

    public void init(FMLInitializationEvent event) {

        MinecraftForge.EVENT_BUS.register(this);

        int id = 0;
        NETWORK.registerMessage(PacketForceConfig.PacketForceConfigHandler.class, PacketForceConfig.class, id++, Side.CLIENT);
        NETWORK.registerMessage(PacketWallJump.PacketWallJumpHandler.class, PacketWallJump.class, id++, Side.SERVER);
        NETWORK.registerMessage(PacketFallDistance.PacketFallDistanceHandler.class, PacketFallDistance.class, id++, Side.SERVER);

    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    @SubscribeEvent
    public void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {

        CommonProxy.NETWORK.sendTo(new PacketForceConfig(), (EntityPlayerMP) event.player);

    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {

        float distance = event.getDistance();
        if (distance > 3 && distance <= WallJumpConfig.minFallDistance) {

            event.setDistance(3.0F);
            event.getEntity().playSound(SoundEvents.ENTITY_GENERIC_SMALL_FALL, 0.5F, 1.0F);

        }

    }

    public static final Enchantment WALLJUMP_ENCHANT = new WallJumpEnchant();
    public static final Enchantment DOUBLEJUMP_ENCHANT = new DoubleJumpEnchant();
    public static final Enchantment SPEEDBOOST_ENCHANT = new SpeedBoostEnchant();

    @SubscribeEvent
    public static void registerEnchants(RegistryEvent.Register<Enchantment> event) {

        event.getRegistry().registerAll(WALLJUMP_ENCHANT, DOUBLEJUMP_ENCHANT, SPEEDBOOST_ENCHANT);

    }


}