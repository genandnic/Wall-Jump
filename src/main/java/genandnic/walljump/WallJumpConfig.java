package genandnic.walljump;
import org.aeonbits.owner.Config;

public interface WallJumpConfig extends Config {

    @DefaultValue("false")
    boolean allowReClinging();

    @DefaultValue("false")
    boolean autoRotation();

    @DefaultValue("0.0")
    double elytraSpeedBoost();

    @DefaultValue("true")
    boolean enableEnchantments();

    @DefaultValue("0.8")
    double exhaustionWallJump();

    @DefaultValue("7.5")
    double minFallDistance();

    @DefaultValue("true")
    boolean playFallSound();

    @DefaultValue("0.0")
    double sprintSpeedBoost();

    @DefaultValue("true")
    boolean stepAssist();

    @DefaultValue("false")
    boolean useDoubleJump();

    @DefaultValue("true")
    boolean useWallJump();

    @DefaultValue("0.55")
    double wallJumpHeight();

    @DefaultValue("15")
    int wallSlideDelay();
}

