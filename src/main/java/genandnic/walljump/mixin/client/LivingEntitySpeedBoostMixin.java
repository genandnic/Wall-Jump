package genandnic.walljump.mixin.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntitySpeedBoostMixin extends Entity {

    public LivingEntitySpeedBoostMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "getJumpVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getJumpVelocityMultiplier()F"))
    private float getJumpVelocityMultiplierSpeedBoost(LivingEntity obj) {
        float val = this.getJumpVelocityMultiplier();

        if(((LivingEntity)(Object) this) instanceof ClientPlayerEntity) {

            StatusEffectInstance jumpBoostEffect = ((ClientPlayerEntity)(Object) this).getStatusEffect(StatusEffects.JUMP_BOOST);

            int jumpBoostLevel = 0;
            if(jumpBoostEffect != null)
                jumpBoostLevel = jumpBoostEffect.getAmplifier() + 1;

            return (float) (val * (this.isSprinting() ? 1.3 : 1.0) * (jumpBoostLevel * 0.5F + 1));
        }

        return val;
    }
}
