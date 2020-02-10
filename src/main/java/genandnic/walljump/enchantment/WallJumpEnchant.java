package genandnic.walljump.enchantment;

import genandnic.walljump.WallJump;
import genandnic.walljump.WallJumpConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class WallJumpEnchant extends Enchantment {

    public WallJumpEnchant() {
        super(Rarity.UNCOMMON, EnumEnchantmentType.ARMOR_FEET, new EntityEquipmentSlot[]{EntityEquipmentSlot.FEET});
        this.setName("walljump");
        this.setRegistryName(WallJump.MOD_ID, "walljump");
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
    public int getMinEnchantability(int level) {
        return 15;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return 60;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {

        if (WallJumpConfig.useWallJump || !WallJumpConfig.enableEnchantments)
            return false;

        return stack.getItem().canApplyAtEnchantingTable(stack, this);
    }

}