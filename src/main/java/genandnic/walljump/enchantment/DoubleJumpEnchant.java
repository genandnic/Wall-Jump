package genandnic.walljump.enchantment;

import genandnic.walljump.Config;
import genandnic.walljump.WallJump;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;

public class DoubleJumpEnchant extends Enchantment {

    public DoubleJumpEnchant() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
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
    public int getMinCost(int level) {
        return level * 20;
    }

    @Override
    public int getMaxCost(int level) {
        return level * 60;
    }

    @Override
    protected boolean checkCompatibility(Enchantment enchantment) {
        if (enchantment instanceof ProtectionEnchantment protection) {
            return protection.type != ProtectionEnchantment.Type.FALL;
        }
        return this != enchantment;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {

        if(!Config.COMMON.enableEnchantments.get())
            return false;

        return stack.canApplyAtEnchantingTable(this);
    }



}
