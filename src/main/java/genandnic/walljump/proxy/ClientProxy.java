package genandnic.walljump.proxy;

import genandnic.walljump.WallJumpConfig;
import genandnic.walljump.client.DoubleJumpLogic;
import genandnic.walljump.client.FallingSound;
import genandnic.walljump.client.SpeedBoostLogic;
import genandnic.walljump.client.WallJumpLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class ClientProxy extends CommonProxy {

    private static Minecraft minecraft = Minecraft.getInstance();
    private static FallingSound fallingSound = new FallingSound(minecraft.player);

    public static final KeyBinding KEY_WALLJUMP = new KeyBinding("walljump.key.walljump", GLFW.GLFW_KEY_LEFT_SHIFT, "key.categories.movement");

    @Override
    public void setupClient() {

        ClientRegistry.registerKeyBinding(KEY_WALLJUMP);
        MinecraftForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        ClientPlayerEntity pl = minecraft.player;
        if (event.phase != TickEvent.Phase.END || pl == null) return;

        WallJumpLogic.doWallJump(pl);
        DoubleJumpLogic.doDoubleJump(pl);
        SpeedBoostLogic.doSpeedBoost(pl);

        if (pl.collidedHorizontally && WallJumpConfig.COMMON.stepAssist.get() && Math.abs(pl.getMotion().y) < 0.05) {
            if (!ClientProxy.collidesWithBlock(pl.world,pl.getBoundingBox().grow(0.001, -pl.stepHeight, 0.001))) pl.onGround = true;
        }

        if (pl.sprintingTicksLeft > 0 && pl.getMotion().length() > 0.08)
            pl.collidedHorizontally = false;

        if (pl.fallDistance > 2 && !pl.isElytraFlying()) {

            if (WallJumpConfig.COMMON.minFallDistance.get() > 255) {
                pl.fallDistance = 0.0f;
                pl.connection.sendPacket(new CPlayerPacket(true));
            }

            if (WallJumpConfig.COMMON.playFallSound.get() && fallingSound.isDonePlaying()) {
                fallingSound = new FallingSound(minecraft.player);
                minecraft.getSoundHandler().play(fallingSound);
            }

        }


    }

    @SubscribeEvent
    public void onWorldLoad(EntityJoinWorldEvent event) {

        if (event.getEntity() == minecraft.player && WallJumpConfig.COMMON.playFallSound.get()) {
            fallingSound = new FallingSound(minecraft.player);
            minecraft.getSoundHandler().play(fallingSound);
        }

    }

    public static boolean collidesWithBlock(World world, AxisAlignedBB box) {
        return world.getCollisionShapes(null, box).count() > 0;
    }

}
