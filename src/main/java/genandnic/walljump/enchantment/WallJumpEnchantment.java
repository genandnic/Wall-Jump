package genandnic.walljump.enchantment;

import genandnic.walljump.WallJump;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class WallJumpEnchantment extends Enchantment {

    public WallJumpEnchantment(Enchantment.Rarity weight, EnchantmentTarget target, EquipmentSlot[] slots) {
        super(weight, target, slots);
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getMinPower(int level) {
        return 20;
    }

    @Override
    public int getMaxPower(int level) {
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
