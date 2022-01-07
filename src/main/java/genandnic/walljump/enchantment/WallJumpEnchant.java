package genandnic.walljump.enchantment;

import genandnic.walljump.Config;
import genandnic.walljump.WallJump;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class WallJumpEnchant extends Enchantment {

    public WallJumpEnchant() {
        super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
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
    public int getMinCost(int level) {
        return 20;
    }

    @Override
    public int getMaxCost(int level) {
        return 60;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {

        if(Config.COMMON.useWallJump.get() || !Config.COMMON.enableEnchantments.get())
            return false;

        return stack.canApplyAtEnchantingTable(this);
    }

}
