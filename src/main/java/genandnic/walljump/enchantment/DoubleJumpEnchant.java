package genandnic.walljump.enchantment;

import genandnic.walljump.WallJump;
import genandnic.walljump.WallJumpConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class DoubleJumpEnchant extends Enchantment {

    public DoubleJumpEnchant() {
        super(Rarity.RARE, EnchantmentType.ARMOR_FEET, new EquipmentSlotType[]{EquipmentSlotType.FEET});
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
        if (enchantment instanceof ProtectionEnchantment) {
            ProtectionEnchantment protection = (ProtectionEnchantment) enchantment;
            return protection.protectionType != ProtectionEnchantment.Type.FALL;
        }
        return this != enchantment;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {

        if(!WallJumpConfig.COMMON.enableEnchantments.get())
            return false;

        return stack.canApplyAtEnchantingTable(this);
    }

}
