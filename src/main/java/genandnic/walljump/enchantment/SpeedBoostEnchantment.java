package genandnic.walljump.enchantment;

import genandnic.walljump.WallJump;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;

public class SpeedBoostEnchantment extends Enchantment {
    public SpeedBoostEnchantment(Weight weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    @Override
    public int getMinimumLevel() {
        return 1;
    }

    @Override
    public int getMaximumLevel() {
        return 3;
    }

    @Override
    public int getMinimumPower(int level) {
        return level * 15;
    }

    @Override
    public int getMaximumPower(int level) {
        return level * 60;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {

        if(!WallJump.CONFIGURATION.enableEnchantments()) {
            return false;
        }

        return stack.isEnchantable() || stack.getItem() instanceof ElytraItem;
    }
}
