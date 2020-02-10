package genandnic.walljump.enchantment;

import genandnic.walljump.WallJump;
import genandnic.walljump.WallJumpConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class DoubleJumpEnchant extends Enchantment {

    public DoubleJumpEnchant() {
        super(Rarity.RARE, EnumEnchantmentType.ARMOR_FEET, new EntityEquipmentSlot[]{EntityEquipmentSlot.FEET});
        this.setName("doublejump");
        this.setRegistryName(WallJump.MOD_ID, "doublejump");
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
    public int getMinEnchantability(int level) {
        return level * 20;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return level * 60;
    }

    @Override
    protected boolean canApplyTogether(Enchantment enchantment) {
        if (enchantment instanceof EnchantmentProtection) {
            EnchantmentProtection protection = (EnchantmentProtection) enchantment;
            return protection.protectionType != EnchantmentProtection.Type.FALL;
        }
        return this != enchantment;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {

        if (!WallJumpConfig.enableEnchantments)
            return false;

        return stack.getItem().canApplyAtEnchantingTable(stack, this);
    }
}

