package genandnic.walljump.proxy;

import genandnic.walljump.WallJump;
import genandnic.walljump.WallJumpConfig;
import genandnic.walljump.client.FallingSound;
import genandnic.walljump.client.PlayerDoubleJump;
import genandnic.walljump.client.PlayerSpeedBoost;
import genandnic.walljump.client.PlayerWallJump;
import genandnic.walljump.packet.PacketForceConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

import static genandnic.walljump.WallJumpConfig.playFallSound;

public class ClientProxy extends CommonProxy {

    public static final KeyBinding KEY_WALLJUMP = new KeyBinding("walljump.key.walljump", Keyboard.KEY_LSHIFT, "key.categories.movement");

    @Override
    public void preInit(FMLPreInitializationEvent event) {

        super.preInit(event);
        ClientRegistry.registerKeyBinding(KEY_WALLJUMP);

    }

    @Override
    public void init(FMLInitializationEvent event) {

        super.init(event);
        MinecraftForge.EVENT_BUS.register(this);

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

        super.postInit(event);

    }

    private static Minecraft minecraft = Minecraft.getMinecraft();
    private static FallingSound fallingSound = new FallingSound(minecraft.player);

    public static double minFallDistance = WallJumpConfig.minFallDistance;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        EntityPlayerSP pl = minecraft.player;
        if (event.phase != TickEvent.Phase.END || pl == null) return;

        PlayerWallJump.tryWallJump(pl);
        PlayerDoubleJump.tryDoubleJump(pl);
        PlayerSpeedBoost.trySpeedBoost(pl);

        if (pl.collidedHorizontally && WallJumpConfig.stepAssist && Math.abs(pl.motionY) < 0.05) {
            if (!ClientProxy.collidesWithBlock(pl.world, pl.getEntityBoundingBox().grow(0.001, -pl.stepHeight, 0.001)))
                pl.onGround = true;
        }

        if (pl.sprintingTicksLeft > 0 && new Vec3d(pl.motionX, pl.motionY, pl.motionZ).lengthVector() > 0.08)
            pl.collidedHorizontally = false;

        if (pl.fallDistance > 2 && !pl.isElytraFlying()) {

            if (minFallDistance > pl.world.getHeight()) {
                pl.fallDistance = 0.0f;
                pl.connection.sendPacket(new CPacketPlayer(true));
            }

            if (playFallSound && fallingSound.isDonePlaying()) {
                fallingSound = new FallingSound(minecraft.player);
                minecraft.getSoundHandler().playSound(fallingSound);
            }

        }

    }


    @SubscribeEvent
    public void onWorldLoad(EntityJoinWorldEvent event) {

        if (event.getEntity() == minecraft.player && true) {
            fallingSound = new FallingSound(minecraft.player);
            minecraft.getSoundHandler().playSound(fallingSound);
        }

    }

    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {

        WallJumpConfig.loadLocalConfig();

    }

    @SubscribeEvent
    public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {

        if (event.getModID().equals(WallJump.MOD_ID)) {

            ConfigManager.sync(WallJump.MOD_ID, Config.Type.INSTANCE);
            if (!WallJumpConfig.isRemote) WallJumpConfig.loadLocalConfig();

            if (minecraft.isIntegratedServerRunning())
                CommonProxy.NETWORK.sendToAll(new PacketForceConfig());

        }

    }

    public static boolean collidesWithBlock(World world, AxisAlignedBB box) {
        return world.collidesWithAnyBlock(box);
    }

}
