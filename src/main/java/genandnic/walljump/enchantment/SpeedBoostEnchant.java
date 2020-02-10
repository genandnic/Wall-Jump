package genandnic.walljump.enchantment;

import genandnic.walljump.WallJump;
import genandnic.walljump.WallJumpConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;

public class SpeedBoostEnchant extends Enchantment {

    public SpeedBoostEnchant() {
        super(Rarity.RARE, EnumEnchantmentType.ARMOR_FEET, new EntityEquipmentSlot[]{EntityEquipmentSlot.FEET});
        this.setName("speedboost");
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
        return super.canApply(stack) || stack.getItem() instanceof ItemElytra;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {

        if (!WallJumpConfig.enableEnchantments)
            return false;

        return stack.getItem().canApplyAtEnchantingTable(stack, this);
    }

}