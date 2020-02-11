package genandnic.walljump.client;

import genandnic.walljump.WallJump;
import genandnic.walljump.WallJumpConfig;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class SpeedBoostLogic {

    public static void doSpeedBoost(ClientPlayerEntity pl) {

        int jumpBoostLevel = 0;
        EffectInstance jumpBoostEffect = pl.getActivePotionEffect(Effect.get(8));
        if (jumpBoostEffect != null) jumpBoostLevel = jumpBoostEffect.getAmplifier() + 1;
        pl.jumpMovementFactor = (float) (pl.getAIMoveSpeed() * (pl.isSprinting() ? 1 : 1.3) / 5) * (jumpBoostLevel * 0.5f + 1);

        Vec3d pos = pl.getPositionVec();
        Vec3d look = pl.getLookVec();
        Vec3d motion = pl.getMotion();

        if (pl.isElytraFlying()) {

            if (pl.isSneaking()) {

                if (pl.rotationPitch < 30f)
                    pl.setMotion(motion.subtract(motion.mul(0.05, 0.05, 0.05)));

            } else if (pl.isSprinting()) {

                float elytraSpeedBoost = WallJumpConfig.COMMON.elytraSpeedBoost.get().floatValue() + (getEquipmentBoost(pl,EquipmentSlotType.CHEST) * 0.75f);
                Vec3d boost = new Vec3d(look.x, look.y + 0.5, look.z).normalize().scale(elytraSpeedBoost);
                if (motion.length() <= boost.length()) {
                    pl.setMotion(motion.add(boost.mul(0.05, 0.05, 0.05)));
                }

                if (boost.length() > 0.5)
                    pl.world.addParticle(ParticleTypes.FIREWORK, pos.x, pos.y, pos.z, 0, 0, 0);

            }

        } else if (pl.isSprinting()) {

            float sprintSpeedBoost = (WallJumpConfig.COMMON.sprintSpeedBoost.get().floatValue() + (getEquipmentBoost(pl, EquipmentSlotType.FEET) * 0.375f));
            if (!pl.onGround) sprintSpeedBoost /= 3.125;

            Vec3d boost = new Vec3d(look.x, 0.0, look.z).scale(sprintSpeedBoost * 0.125F);
            pl.setMotion(motion.add(boost));

        }

    }

    private static int getEquipmentBoost(ClientPlayerEntity pl, EquipmentSlotType slot) {

        ItemStack stack = pl.getItemStackFromSlot(slot);
        if (!stack.isEmpty()) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            if (enchantments.containsKey(WallJump.SPEEDBOOST_ENCHANT))
                return enchantments.get(WallJump.SPEEDBOOST_ENCHANT);
        }

        return 0;
    }


}
