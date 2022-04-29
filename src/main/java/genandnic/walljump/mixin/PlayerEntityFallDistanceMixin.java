package genandnic.walljump.mixin;

import genandnic.walljump.WallJump;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityFallDistanceMixin {
    @Shadow
    public abstract void playSound(SoundEvent sound, float volume, float pitch);

    @ModifyArg(method = "handleFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;handleFallDamage(FFLnet/minecraft/entity/damage/DamageSource;)Z"), index = 0)
    private float adjustFallDistance(float value) {
        if (value > 3 && value <= WallJump.CONFIGURATION.minFallDistance()) {
            this.playSound(SoundEvents.ENTITY_GENERIC_SMALL_FALL, 0.5F, 1.0F);
            return 3.0F;
        }

        return value;
    }
}