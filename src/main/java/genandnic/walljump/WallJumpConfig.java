package genandnic.walljump;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class WallJumpConfig {

    public static class Common {

        public final ForgeConfigSpec.BooleanValue allowReClinging;
        public final ForgeConfigSpec.BooleanValue autoRotation;
        public final ForgeConfigSpec.DoubleValue elytraSpeedBoost;
        public final ForgeConfigSpec.BooleanValue enableEnchantments;
        public final ForgeConfigSpec.DoubleValue exhaustionWallJump;
        public final ForgeConfigSpec.DoubleValue minFallDistance;
        public final ForgeConfigSpec.BooleanValue playFallSound;
        public final ForgeConfigSpec.DoubleValue sprintSpeedBoost;
        public final ForgeConfigSpec.BooleanValue stepAssist;
        public final ForgeConfigSpec.BooleanValue useDoubleJump;
        public final ForgeConfigSpec.BooleanValue useWallJump;
        public final ForgeConfigSpec.DoubleValue wallJumpHeight;
        public final ForgeConfigSpec.IntValue wallSlideDelay;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Common configuration settings").push("common");

            this.allowReClinging = builder
                    .comment("Allows you to climb up without alternating walls")
                    .define("allowReClinging", false);
            this.autoRotation = builder
                    .comment("Automatically turn the player when wall clinging")
                    .define("autoRotation", false);
            this.elytraSpeedBoost = builder
                    .comment("Elytra speed boost; set to 0.0 to disable")
                    .defineInRange("elytraSpeedBoost", 0.0, 0.0, 3.0);
            this.enableEnchantments = builder
                    .comment("Enable Wall-Jump enchantments in the enchanting table")
                    .define("enableEnchantments",true);
            this.exhaustionWallJump = builder
                    .comment("Exhaustion gained per wall jump")
                    .defineInRange("exhaustionWallJump", 0.8, 0.0, 3.0);
            this.minFallDistance = builder
                    .comment("Minimum distance for fall damage; set to 3.0 to disable")
                    .defineInRange("minFallDistance", 7.5, 3.0, 256);
            this.playFallSound = builder
                    .comment("Play a rush of wind as you fall to your doom")
                    .define("playFallSound", true);
            this.sprintSpeedBoost = builder
                    .comment("Sprint speed boost; set to 0.0 to disable")
                    .defineInRange("sprintSpeedBoost", 0.0, 0.0, 3.0);
            this.stepAssist = builder
                    .comment("Walk up steps even while airborne, also jump over fences")
                    .define("stepAssist", true);
            this.useDoubleJump = builder
                    .comment("Allows you to jump in mid-air")
                    .define("useDoubleJump", false);
            this.useWallJump = builder
                    .comment("Allows you to wall cling and wall jump")
                    .define("useWallJump", true);
            this.wallJumpHeight = builder
                    .defineInRange("wallJumpHeight", 0.55, 0.0, 1.0);
            this.wallSlideDelay = builder
                    .comment("Ticks wall clinged before starting wall slide")
                    .defineInRange("wallSlideDelay", 15, 0, Integer.MAX_VALUE);

            builder.pop();
        }
    }

    static final ForgeConfigSpec commonSpec;
    public static final WallJumpConfig.Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(WallJumpConfig.Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {

    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading event) {

    }

    @SubscribeEvent
    public static void onReload(final ModConfig.ConfigReloading event) {

    }

}
