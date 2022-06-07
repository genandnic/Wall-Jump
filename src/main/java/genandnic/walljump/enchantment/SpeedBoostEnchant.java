package genandnic.walljump.enchantment;

import genandnic.walljump.Config;
import genandnic.walljump.WallJump;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SpeedBoostEnchant extends Enchantment {

    public SpeedBoostEnchant() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
        this.setRegistryName(WallJump.MOD_ID, "speedboost");
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinCost(int level) {
        return level * 15;
    }

    @Override
    public int getMaxCost(int level) {
        return level * 60;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return super.canEnchant(stack) || (stack.getItem() instanceof ElytraItem && Config.COMMON.enableElytraSpeedEnchantment.get());
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {

        if(!Config.COMMON.enableEnchantments.get())
            return false;

        return stack.canApplyAtEnchantingTable(this);
    }

}
