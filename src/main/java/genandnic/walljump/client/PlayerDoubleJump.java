package genandnic.walljump.client;

import genandnic.walljump.WallJumpConfig;
import genandnic.walljump.packet.PacketFallDistance;
import genandnic.walljump.proxy.CommonProxy;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.Map;

public class PlayerDoubleJump {

    public static boolean useDoubleJump = WallJumpConfig.useDoubleJump;

    private static boolean jumpKey = false;
    private static int jumpCount = 0;

    public static void tryDoubleJump(EntityPlayerSP pl) {

        AxisAlignedBB box = new AxisAlignedBB(pl.posX, pl.posY + (pl.eyeHeight * .8), pl.posZ, pl.posX, pl.posY + pl.height, pl.posZ);

        if (pl.onGround || pl.capabilities.allowFlying || pl.world.containsAnyLiquid(box) || pl.isRiding() || PlayerWallJump.ticksWallClinged > 0) {

            jumpCount = PlayerDoubleJump.getMultiJumps(pl);

        } else if (pl.movementInput.jump || pl.isElytraFlying()) {

            if (!jumpKey && jumpCount > 0 && pl.motionY < 0.1 && PlayerWallJump.ticksWallClinged < 1 && pl.getFoodStats().getFoodLevel() > 0) {

                pl.jump();
                jumpCount--;

                pl.fallDistance = 0.0F;
                CommonProxy.NETWORK.sendToServer(new PacketFallDistance(pl.fallDistance));

            }

            jumpKey = true;

        } else {

            jumpKey = false;

        }

    }

    private static int getMultiJumps(EntityPlayer pl) {

        int jumpCount = 0;
        if (useDoubleJump) jumpCount += 1;

        ItemStack stack = pl.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        if (!stack.isEmpty()) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            if (enchantments.containsKey(CommonProxy.DOUBLEJUMP_ENCHANT))
                jumpCount += enchantments.get(CommonProxy.DOUBLEJUMP_ENCHANT);
        }

        return jumpCount;
    }

}
