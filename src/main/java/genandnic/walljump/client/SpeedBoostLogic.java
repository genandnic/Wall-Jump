package genandnic.walljump.client;

import genandnic.walljump.Config;
import genandnic.walljump.WallJump;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class SpeedBoostLogic {

    public static void doSpeedBoost(LocalPlayer pl) {

        int jumpBoostLevel = 0;
        MobEffectInstance jumpBoostEffect = pl.getEffect(MobEffect.byId(8));
        if (jumpBoostEffect != null) jumpBoostLevel = jumpBoostEffect.getAmplifier() + 1;
        pl.flyingSpeed = (float) (pl.getSpeed() * (pl.isSprinting() ? 1 : 1.3) / 5) * (jumpBoostLevel * 0.5f + 1);

        Vec3 pos = pl.position();
        Vec3 look = pl.getLookAngle();
        Vec3 motion = pl.getDeltaMovement();

        if (pl.isFallFlying()) {

            if (pl.isCrouching()) {

                if (pl.getXRot() < 30f)
                    pl.setDeltaMovement(motion.subtract(motion.multiply(0.05, 0.05, 0.05)));

            } else if (pl.isSprinting()) {

                float elytraSpeedBoost = Config.COMMON.elytraSpeedBoost.get().floatValue() + (getEquipmentBoost(pl, EquipmentSlot.CHEST) * 0.5f);
                Vec3 boost = new Vec3(look.x, look.y, look.z).normalize().scale(elytraSpeedBoost);
                if (motion.length() <= boost.length()) {
                    pl.setDeltaMovement(motion.add(boost.multiply(0.05, 0.05, 0.05)));
                }

                if (boost.length() > 0.5)
                    pl.level.addParticle(ParticleTypes.FIREWORK, pos.x, pos.y, pos.z, 0, 0, 0);

            }

        } else if (pl.isSprinting()) {

            float sprintSpeedBoost = (Config.COMMON.sprintSpeedBoost.get().floatValue() + (getEquipmentBoost(pl, EquipmentSlot.FEET) * 0.25f));
            if (!pl.isOnGround()) sprintSpeedBoost /= 3.125;

            Vec3 boost = new Vec3(look.x, 0.0, look.z).scale(sprintSpeedBoost * 0.125F);
            pl.setDeltaMovement(motion.add(boost));

        }

    }

    private static int getEquipmentBoost(LocalPlayer pl, EquipmentSlot slot) {

        ItemStack stack = pl.getItemBySlot(slot);
        if (!stack.isEmpty()) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            if (enchantments.containsKey(WallJump.SPEEDBOOST_ENCHANT))
                return enchantments.get(WallJump.SPEEDBOOST_ENCHANT);
        }

        return 0;
    }


}
