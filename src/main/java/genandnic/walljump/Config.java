package genandnic.walljump;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber
public class Config {

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
                    .comment("Elytra speed boost multiplier")
                    .defineInRange("elytraSpeedBoost", 1.0, 0.0, 5.0);
            this.enableEnchantments = builder
                    .comment("Enable Wall-Jump enchantments in the enchanting table")
                    .define("enableEnchantments",true);
            this.enableElytraSpeedEnchantment = builder
                    .comment("Enable the Speed Boost enchantment for elytra")
                    .define("enableElytraSpeedEnchantment",true);
            this.exhaustionWallJump = builder
                    .comment("Exhaustion gained per wall jump")
                    .defineInRange("exhaustionWallJump", 0.8, 0.0, 5.0);
            this.minFallDistance = builder
                    .comment("Minimum distance for fall damage; set to 3.0 to disable")
                    .defineInRange("minFallDistance", 7.5, 3.0, 256);
            this.playFallSound = builder
                    .comment("Play a rush of wind as you fall to your doom")
                    .define("playFallSound", true);
            this.sprintSpeedBoost = builder
                    .comment("Sprint speed boost; set to 0.0 to disable")
                    .defineInRange("sprintSpeedBoost", 0.0, 0.0, 5.0);
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

    public static final Config.Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config.Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();

    }

}
