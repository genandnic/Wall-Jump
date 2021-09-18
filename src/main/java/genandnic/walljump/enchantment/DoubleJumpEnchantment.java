package genandnic.walljump.enchantment;

import genandnic.walljump.WallJump;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class DoubleJumpEnchantment extends Enchantment {

    public DoubleJumpEnchantment(Enchantment.Rarity weight, EnchantmentTarget target, EquipmentSlot[] slots) {
        super(weight, target, slots);
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public int getMinPower(int level) {
        return level * 20;
    }

    @Override
    public int getMaxPower(int level) {
        return level * 60;
    }

    @Override
    public boolean canAccept(Enchantment enchantment) {
        if(enchantment instanceof ProtectionEnchantment protection) {
            return protection.protectionType != ProtectionEnchantment.Type.FALL;
        }

        return this != enchantment;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {

        if(!WallJump.CONFIGURATION.enableEnchantments()) {
            return false;
        }

        return stack.isEnchantable();
    }
}
