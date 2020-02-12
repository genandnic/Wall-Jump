package genandnic.walljump.mixin.client;

import com.mojang.authlib.GameProfile;
import genandnic.walljump.WallJump;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntitySpeedBoostMixin extends AbstractClientPlayerEntity {

    @Shadow
    public abstract boolean isSneaking();

    public ClientPlayerEntitySpeedBoostMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void miscellaneousTickMovement(CallbackInfo ci) {
        this.doSpeedBoost();
    }

    private void doSpeedBoost() {
        Vec3d pos = this.getPosVector();
        Vec3d look = this.getRotationVector();
        Vec3d motion = this.getVelocity();

        if (this.isFallFlying()) {

            if (this.isSneaking()) {

                if (this.pitch < 30F)
                    this.setVelocity(motion.subtract(motion.multiply(0.05)));

            } else if (this.isSprinting()) {

                float elytraSpeedBoost = (float) WallJump.CONFIGURATION.elytraSpeedBoost() + (getEquipmentBoost(EquipmentSlot.CHEST) * 0.75F);
                Vec3d boost = new Vec3d(look.getX(), look.getY() + 0.5, look.getZ()).normalize().multiply(elytraSpeedBoost);
                if(motion.length() <= boost.length())
                    this.setVelocity(motion.add(boost.multiply(0.05)));

                if(boost.length() > 0.5)
                    this.world.addParticle(ParticleTypes.FIREWORK, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);

            }

        } else if(this.isSprinting()) {

            float sprintSpeedBoost = (float) WallJump.CONFIGURATION.sprintSpeedBoost() + (getEquipmentBoost(EquipmentSlot.FEET) * 0.375F);
            if(!this.onGround)
                sprintSpeedBoost /= 3.125;

            Vec3d boost = new Vec3d(look.getX(), 0.0, look.getZ()).multiply(sprintSpeedBoost * 0.125F);
            this.setVelocity(motion.add(boost));
        }
    }

    private int getEquipmentBoost(EquipmentSlot slot) {

        ItemStack stack = this.getEquippedStack(slot);
        if (!stack.isEmpty()) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            if (enchantments.containsKey(WallJump.SPEEDBOOST_ENCHANTMENT))
                return enchantments.get(WallJump.SPEEDBOOST_ENCHANTMENT);
        }

        return 0;
    }
}
