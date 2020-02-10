package genandnic.walljump.client;

import genandnic.walljump.WallJumpConfig;
import genandnic.walljump.proxy.CommonProxy;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;

import java.util.Map;

public class PlayerSpeedBoost {

    public static float sprintSpeedBoost = (float) WallJumpConfig.sprintSpeedBoost;
    public static float elytraSpeedBoost = (float) WallJumpConfig.elytraSpeedBoost;

    public static void trySpeedBoost(EntityPlayerSP pl) {

        int jumpBoostLevel = 0;
        PotionEffect jumpBoostEffect = pl.getActivePotionEffect(Potion.getPotionById(8));
        if (jumpBoostEffect != null) jumpBoostLevel = jumpBoostEffect.getAmplifier() + 1;
        pl.jumpMovementFactor = (float) (pl.getAIMoveSpeed() * (pl.isSprinting() ? 1 : 1.3) / 5) * (jumpBoostLevel * 0.5f + 1);

        if (pl.isSprinting()) {

            if (pl.isElytraFlying()) {

                float boost = (elytraSpeedBoost + (getEquipmentBoost(pl, EntityEquipmentSlot.CHEST) / 2)) * 0.025F;
                pl.moveRelative(pl.moveStrafing, pl.moveVertical, pl.moveForward, boost);

                if (boost > 0)
                    pl.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, pl.posX, pl.posY, pl.posZ, 0, 0, 0);

            } else {

                float boost = (sprintSpeedBoost + (getEquipmentBoost(pl, EntityEquipmentSlot.FEET) * 0.375f)) * 0.125F;
                if (!pl.onGround) boost /= 3.125;
                pl.moveRelative(pl.moveStrafing, 0.0f, pl.moveForward, boost);

            }

        }

    }

    private static int getEquipmentBoost(EntityPlayer pl, EntityEquipmentSlot slot) {

        ItemStack stack = pl.getItemStackFromSlot(slot);
        if (!stack.isEmpty()) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            if (enchantments.containsKey(CommonProxy.SPEEDBOOST_ENCHANT))
                return enchantments.get(CommonProxy.SPEEDBOOST_ENCHANT);
        }

        return 0;
    }

}
