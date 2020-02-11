package genandnic.walljump;

import genandnic.walljump.enchantment.DoubleJumpEnchant;
import genandnic.walljump.enchantment.SpeedBoostEnchant;
import genandnic.walljump.enchantment.WallJumpEnchant;
import genandnic.walljump.proxy.ClientProxy;
import genandnic.walljump.proxy.CommonProxy;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(WallJump.MOD_ID)
@Mod.EventBusSubscriber(modid = WallJump.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WallJump {

    public static final String MOD_ID = "walljump";
    public static final CommonProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public WallJump() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);

    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        PROXY.setupCommon();
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        PROXY.setupClient();
    }

    public static Enchantment WALLJUMP_ENCHANT = new WallJumpEnchant();
    public static Enchantment DOUBLEJUMP_ENCHANT = new DoubleJumpEnchant();
    public static Enchantment SPEEDBOOST_ENCHANT = new SpeedBoostEnchant();

    @SubscribeEvent
    public static void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        event.getRegistry().register(WALLJUMP_ENCHANT);
        event.getRegistry().register(DOUBLEJUMP_ENCHANT);
        event.getRegistry().register(SPEEDBOOST_ENCHANT);
    }

}
