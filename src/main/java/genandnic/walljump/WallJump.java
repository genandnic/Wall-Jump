package genandnic.walljump;

import genandnic.walljump.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = WallJump.MOD_ID, name = WallJump.NAME, version = WallJump.VERSION, acceptedMinecraftVersions = "[1.12,1.12.2]", acceptableRemoteVersions = "[1.3.2,)")
public class WallJump {

    public static final String MOD_ID = "walljump";
    public static final String NAME = "Wall-Jump!";
    public static final String VERSION = "1.3.2";

    @SidedProxy(clientSide = "genandnic.walljump.proxy.ClientProxy", serverSide = "genandnic.walljump.proxy.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        proxy.preInit(event);

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        proxy.init(event);

    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

        proxy.postInit(event);

    }

}
