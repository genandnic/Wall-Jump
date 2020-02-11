package genandnic.walljump.client;

import genandnic.walljump.WallJump;
import genandnic.walljump.WallJumpConfig;
import genandnic.walljump.network.PacketHandler;
import genandnic.walljump.network.message.MessageFallDistance;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class DoubleJumpLogic {

    private static int jumpCount = 0;
    private static boolean jumpKey = false;

    public static void doDoubleJump(ClientPlayerEntity pl) {

        Vec3d pos = pl.getPositionVec();
        Vec3d motion = pl.getMotion();

        AxisAlignedBB box = new AxisAlignedBB(pos.x, pos.y + (pl.getEyeHeight() * .8), pos.z, pos.x, pos.y + pl.getHeight(), pos.z);

        if (pl.onGround || pl.world.containsAnyLiquid(box) || WallJumpLogic.ticksWallClinged > 0 || pl.isPassenger() || pl.abilities.allowFlying) {

            jumpCount = DoubleJumpLogic.getMultiJumps(pl);

        } else if (pl.movementInput.jump || pl.isElytraFlying()) {

            if (!jumpKey && jumpCount > 0 && motion.y < 0.1 && WallJumpLogic.ticksWallClinged < 1 && pl.getFoodStats().getFoodLevel() > 0) {

                pl.jump();
                jumpCount--;

                pl.fallDistance = 0.0F;
                PacketHandler.instance.sendToServer(new MessageFallDistance(pl.fallDistance));

            }

            jumpKey = true;

        } else {

            jumpKey = false;

        }

    }

    private static int getMultiJumps(PlayerEntity pl) {

        int jumpCount = 0;
        if (WallJumpConfig.COMMON.useDoubleJump.get()) jumpCount += 1;

        ItemStack stack = pl.getItemStackFromSlot(EquipmentSlotType.FEET);
        if (!stack.isEmpty()) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            if (enchantments.containsKey(WallJump.DOUBLEJUMP_ENCHANT))
                jumpCount += enchantments.get(WallJump.DOUBLEJUMP_ENCHANT);
        }

        return jumpCount;
    }

}
