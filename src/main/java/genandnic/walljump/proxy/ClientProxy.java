package genandnic.walljump.proxy;

import genandnic.walljump.Config;
import genandnic.walljump.client.DoubleJumpLogic;
import genandnic.walljump.client.FallingSound;
import genandnic.walljump.client.SpeedBoostLogic;
import genandnic.walljump.client.WallJumpLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class ClientProxy extends CommonProxy {

    public static KeyMapping KEY_WALLJUMP = new KeyMapping("walljump.key.walljump", GLFW.GLFW_KEY_LEFT_SHIFT, "key.categories.movement");
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static FallingSound FALLING_SOUND;

    @Override
    public void setupClient() {

        ClientRegistry.registerKeyBinding(KEY_WALLJUMP);
        MinecraftForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        LocalPlayer pl = minecraft.player;

        if (event.phase != TickEvent.Phase.END || pl == null) return;

        WallJumpLogic.doWallJump(pl);
        DoubleJumpLogic.doDoubleJump(pl);
        SpeedBoostLogic.doSpeedBoost(pl);

        if (pl.horizontalCollision && Config.COMMON.stepAssist.get() && pl.getDeltaMovement().y > -0.2 && pl.getDeltaMovement().y < 0.01) {
            if (!ClientProxy.collidesWithBlock(pl.getLevel(), pl.getBoundingBox().inflate(0.01, -pl.maxUpStep + 0.02, 0.01))) {
                pl.setOnGround(true);
            }
        }

        if (pl.sprintTime > 0 && pl.getDeltaMovement().length() > 0.08)
            pl.horizontalCollision = false;

        if (pl.fallDistance > 1.5 && !pl.isFallFlying()) {

            if (Config.COMMON.playFallSound.get() && (FALLING_SOUND == null || FALLING_SOUND.isStopped())) {
                FALLING_SOUND = new FallingSound(pl);
                minecraft.getSoundManager().play(FALLING_SOUND);
            }

        }

    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent event) {

        if (event.getEntity() == minecraft.player && Config.COMMON.playFallSound.get()) {
            FALLING_SOUND = new FallingSound(minecraft.player);
            minecraft.getSoundManager().play(FALLING_SOUND);
        }

    }

    public static boolean collidesWithBlock(Level level, AABB box) {
        return !level.noCollision(box);
    }

}
