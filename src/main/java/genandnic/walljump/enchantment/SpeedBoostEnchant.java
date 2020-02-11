package genandnic.walljump.enchantment;

import genandnic.walljump.Config;
import genandnic.walljump.WallJump;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;

public class SpeedBoostEnchant extends Enchantment {

    public SpeedBoostEnchant() {
        super(Rarity.RARE, EnchantmentType.ARMOR_FEET, new EquipmentSlotType[]{EquipmentSlotType.FEET});
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
    public int getMinEnchantability(int level) {
        return level * 15;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return level * 60;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return super.canApply(stack) || stack.getItem() instanceof ElytraItem;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {

        if(!Config.COMMON.enableEnchantments.get())
            return false;

        return stack.canApplyAtEnchantingTable(this);
    }

}
