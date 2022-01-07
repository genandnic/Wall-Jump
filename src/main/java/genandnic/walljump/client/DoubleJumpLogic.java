package genandnic.walljump.client;

import genandnic.walljump.Config;
import genandnic.walljump.WallJump;
import genandnic.walljump.network.PacketHandler;
import genandnic.walljump.network.message.MessageFallDistance;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class DoubleJumpLogic {

    private static int jumpCount = 0;
    private static boolean jumpKey = false;

    public static void doDoubleJump(LocalPlayer pl) {

        Vec3 pos = pl.position();
        Vec3 motion = pl.getDeltaMovement();

        AABB box = new AABB(pos.x, pos.y + (pl.getEyeHeight() * .8), pos.z, pos.x, pos.y + pl.getBbHeight(), pos.z);

        if (pl.isOnGround() || pl.level.containsAnyLiquid(box) || WallJumpLogic.ticksWallClinged > 0 || pl.isPassenger() || pl.getAbilities().mayfly) {

            jumpCount = getMultiJumps(pl);

        } else if (pl.input.jumping) {

            if (!jumpKey && jumpCount > 0 && motion.y < 0.333 && WallJumpLogic.ticksWallClinged < 1 && pl.getFoodData().getFoodLevel() > 0) {

                pl.jumpFromGround();
                jumpCount--;

                pl.fallDistance = 0.0F;
                PacketHandler.INSTANCE.sendToServer(new MessageFallDistance(pl.fallDistance));

            }

            jumpKey = true;

        } else {

            jumpKey = false;

        }

    }

    private static int getMultiJumps(LocalPlayer pl) {

        int jumpCount = 0;
        if (Config.COMMON.useDoubleJump.get()) jumpCount += 1;

        ItemStack stack = pl.getItemBySlot(EquipmentSlot.FEET);
        if (!stack.isEmpty()) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            if (enchantments.containsKey(WallJump.DOUBLEJUMP_ENCHANT))
                jumpCount += enchantments.get(WallJump.DOUBLEJUMP_ENCHANT);
        }

        return jumpCount;
    }

}
