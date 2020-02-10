package genandnic.walljump;

import genandnic.walljump.client.PlayerDoubleJump;
import genandnic.walljump.client.PlayerSpeedBoost;
import genandnic.walljump.client.PlayerWallJump;
import genandnic.walljump.proxy.ClientProxy;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;

@net.minecraftforge.common.config.Config(modid = WallJump.MOD_ID)
public class WallJumpConfig {

    @Ignore
    public static boolean isRemote = false;

    @Comment("Allows you to climb up without alternating walls")
    public static boolean allowReClinging = false;

    @Comment("Automatically turn the player when wall clinging")
    public static boolean autoRotation = false;

    @Comment("A list of blocks that cannot be clinged to")
    public static String[] blacklistedBlocks = {"minecraft:block"};

    @RangeDouble(min = 0.0, max = 3.0)
    @Comment("Elytra speed boost; set to 0.0 to disable")
    public static double elytraSpeedBoost = 0.0;

    @Comment("Enable Wall-Jump enchantments in the enchanting table")
    public static boolean enableEnchantments = true;

    @RangeDouble(min = 0.0, max = 3.0)
    @Comment("Exhaustion gained per wall jump")
    public static double exhaustionWallJump = 0.75;

    @Comment("Turns the blacklisted block list into a whitelist")
    public static boolean invertBlockBlacklist = false;

    @RangeDouble(min = 3.0)
    @Comment("Minimum distance for fall damage; set to 3.0 to disable")
    public static double minFallDistance = 7.5;

    @Comment("Play a rush of wind as you fall to your doom")
    public static boolean playFallSound = true;

    @RangeDouble(min = 0.0, max = 3.0)
    @Comment("Sprint speed boost; set to 0.0 to disable")
    public static double sprintSpeedBoost = 0.0;

    @Comment("Walk up steps even while airborne, also jump over fences")
    public static boolean stepAssist = true;

    @Comment("Allows you to jump in mid-air")
    public static boolean useDoubleJump = false;

    @Comment("Allows you to wall cling and wall jump")
    public static boolean useWallJump = true;

    @RangeDouble(min = 0.0F, max = 1.0)
    @Comment("Distance jumped during a wall jump")
    public static double wallJumpBoost = 0.55;

    @RangeInt(min = 0)
    @Comment("Ticks wall clinged before a wall slide")
    public static int wallSlideDelay = 15;

    public static void loadLocalConfig() {

        WallJumpConfig.isRemote = false;
        ClientProxy.minFallDistance = minFallDistance;
        PlayerWallJump.useWallJump = useWallJump;
        PlayerWallJump.allowReClinging = allowReClinging;
        PlayerWallJump.wallJumpBoost = (float) wallJumpBoost;
        PlayerWallJump.wallSlideDelay = wallSlideDelay;
        PlayerDoubleJump.useDoubleJump = useDoubleJump;
        PlayerSpeedBoost.sprintSpeedBoost = (float) sprintSpeedBoost;
        PlayerSpeedBoost.elytraSpeedBoost = (float) elytraSpeedBoost;

    }

}
