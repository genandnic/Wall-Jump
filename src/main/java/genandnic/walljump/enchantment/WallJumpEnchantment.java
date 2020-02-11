package genandnic.walljump.enchantment;

import genandnic.walljump.WallJump;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class WallJumpEnchantment extends Enchantment {

    public WallJumpEnchantment(Weight weight, EnchantmentTarget target, EquipmentSlot[] slots) {
        super(weight, target, slots);
    }

    @Override
    public int getMinimumLevel() {
        return 1;
    }

    @Override
    public int getMaximumLevel() {
        return 1;
    }

    @Override
    public int getMinimumPower(int level) {
        return 20;
    }

    @Override
    public int getMaximumPower(int level) {
        return 60;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {

        if(WallJump.CONFIGURATION.useWallJump() || !WallJump.CONFIGURATION.enableEnchantments()) {
            return false;
        }

        return stack.isEnchantable();
    }
}
