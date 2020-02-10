package genandnic.walljump.client;

import genandnic.walljump.WallJumpConfig;
import genandnic.walljump.packet.PacketFallDistance;
import genandnic.walljump.packet.PacketWallJump;
import genandnic.walljump.proxy.ClientProxy;
import genandnic.walljump.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerWallJump {

    private static Minecraft minecraft = Minecraft.getMinecraft();

    public static boolean useWallJump = WallJumpConfig.useWallJump;
    public static boolean allowReClinging = WallJumpConfig.allowReClinging;
    public static float wallJumpBoost = (float) WallJumpConfig.wallJumpBoost;
    public static int wallSlideDelay = WallJumpConfig.wallSlideDelay;

    public static int ticksWallClinged;
    private static int ticksKeyDown;
    private static double clingX, clingZ;
    private static double lastJumpY = Double.MAX_VALUE;

    public static void tryWallJump(EntityPlayerSP pl) {

        if (!PlayerWallJump.canWallJump(pl))
            return;

        if (pl.onGround || pl.capabilities.isFlying || pl.isInWater()) {

            ticksWallClinged = 0;
            clingX = Double.NaN;
            clingZ = Double.NaN;
            lastJumpY = Double.MAX_VALUE;
            staleWalls.clear();

            return;
        }

        PlayerWallJump.updateWalls(pl);
        ticksKeyDown = ClientProxy.KEY_WALLJUMP.isKeyDown() ? ticksKeyDown + 1 : 0;

        if (ticksWallClinged < 1) {

            if (ticksKeyDown > 0 && ticksKeyDown < 4 && !walls.isEmpty() && canWallCling(pl)) {

                pl.limbSwingAmount = 2.5F;
                if (WallJumpConfig.autoRotation)
                    pl.rotationYaw = getClingDirection().getOpposite().getHorizontalAngle();

                ticksWallClinged = 1;
                clingX = pl.posX;
                clingZ = pl.posZ;

                playHitSound(pl, getWallPos(pl));
                spawnWallParticle(pl, getWallPos(pl));
            }

            return;
        }

        if (!ClientProxy.KEY_WALLJUMP.isKeyDown() || walls.isEmpty() || pl.getFoodStats().getFoodLevel() < 1) {

            if ((pl.moveForward != 0 || pl.moveStrafing != 0) && wallJumpBoost > 0 && !pl.onGround && !walls.isEmpty()) {

                pl.fallDistance = 0.0F;
                CommonProxy.NETWORK.sendToServer(new PacketWallJump());

                wallJump(pl, wallJumpBoost);
                staleWalls = new HashSet<>(walls);

            }

            lastJumpY = pl.posY;
            ticksWallClinged = 0;

            return;
        }

        if (WallJumpConfig.autoRotation) pl.rotationYaw = getClingDirection().getOpposite().getHorizontalAngle();
        pl.setPosition(clingX, pl.posY, clingZ);
        pl.movementInput.sneak = true;

        if (pl.motionY > 0.0) {
            pl.motionY = 0.0;
        } else if (pl.motionY < -0.6) {
            pl.motionY = pl.motionY + 0.2;
            spawnWallParticle(pl, getWallPos(pl));
        } else if (ticksWallClinged++ > wallSlideDelay) {
            pl.motionY = -0.1;
            spawnWallParticle(pl, getWallPos(pl));
        } else {
            pl.motionY = 0.0;
        }

        if (pl.fallDistance > 2) {
            pl.fallDistance = 0;
            CommonProxy.NETWORK.sendToServer(new PacketFallDistance((float) (pl.motionY * pl.motionY * 8)));
        }

        pl.setVelocity(0, pl.motionY, 0);
    }


    private static boolean canWallJump(EntityPlayer pl) {

        if (useWallJump) return true;

        ItemStack stack = pl.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        if (!stack.isEmpty()) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            return enchantments.containsKey(CommonProxy.WALLJUMP_ENCHANT);
        }

        return false;
    }

    private static boolean canWallCling(EntityPlayerSP pl) {

        if (pl.isOnLadder() || pl.motionY > 0.1 || pl.getFoodStats().getFoodLevel() < 1)
            return false;

        if (ClientProxy.collidesWithBlock(pl.world, pl.getEntityBoundingBox().offset(0, -0.8, 0))) return false;

        if (Arrays.asList(WallJumpConfig.blacklistedBlocks).contains(pl.world.getBlockState(getWallPos(pl)).getBlock().getRegistryName().toString()) ^ WallJumpConfig.invertBlockBlacklist)
            return false;

        if (allowReClinging || pl.posY < lastJumpY - 1) return true;

        if (staleWalls.containsAll(walls)) return false;

        return true;
    }

    private static Set<EnumFacing> walls = new HashSet<EnumFacing>();
    private static Set<EnumFacing> staleWalls = new HashSet<EnumFacing>();

    private static void updateWalls(EntityPlayerSP pl) {

        AxisAlignedBB box = new AxisAlignedBB(pl.posX - 0.001, pl.posY, pl.posZ - 0.001, pl.posX + 0.001, pl.posY + pl.getEyeHeight(), pl.posZ + 0.001);

        double dist = (pl.width / 2) + (ticksWallClinged > 0 ? 0.2 : 0.06);
        AxisAlignedBB[] axes = {box.expand(0, 0, dist), box.expand(-dist, 0, 0), box.expand(0, 0, -dist), box.expand(dist, 0, 0)};

        int i = 0;
        EnumFacing direction;
        PlayerWallJump.walls = new HashSet<EnumFacing>();
        for (AxisAlignedBB axis : axes) {
            direction = EnumFacing.HORIZONTALS[i++];
            if (ClientProxy.collidesWithBlock(pl.world, axis)) {
                walls.add(direction);
                pl.collidedHorizontally = true;
            }
        }

    }

    private static EnumFacing getClingDirection() {
        return walls.isEmpty() ? EnumFacing.UP : walls.iterator().next();
    }

    private static BlockPos getWallPos(EntityPlayerSP player) {

        BlockPos pos = new BlockPos(player).offset(getClingDirection());
        return player.world.getBlockState(pos).getMaterial().isSolid() ? pos : pos.offset(EnumFacing.UP);

    }

    private static void wallJump(EntityPlayerSP pl, float up) {

        float strafe = Math.signum(pl.moveStrafing) * up * up;
        float forward = Math.signum(pl.moveForward) * up * up;

        float f = 1.0F / MathHelper.sqrt(strafe * strafe + up * up + forward * forward);
        strafe = strafe * f;
        forward = forward * f;

        float f1 = MathHelper.sin(pl.rotationYaw * 0.017453292F) * 0.45f;
        float f2 = MathHelper.cos(pl.rotationYaw * 0.017453292F) * 0.45f;

        int jumpBoostLevel = 0;
        PotionEffect jumpBoostEffect = minecraft.player.getActivePotionEffect(Potion.getPotionById(8));
        if (jumpBoostEffect != null) jumpBoostLevel = jumpBoostEffect.getAmplifier() + 1;

        pl.motionY = up + (jumpBoostLevel * .125);
        pl.motionX += strafe * f2 - forward * f1;
        pl.motionZ += forward * f2 + strafe * f1;

        playBreakSound(pl, getWallPos(pl));
        spawnWallParticle(pl, getWallPos(pl));

    }

    private static void playHitSound(Entity entity, BlockPos pos) {

        IBlockState state = entity.world.getBlockState(pos);
        SoundType soundtype = state.getBlock().getSoundType(state, entity.world, pos, entity);
        entity.playSound(soundtype.getHitSound(), soundtype.getVolume() * 0.25F, soundtype.getPitch());

    }

    private static void playBreakSound(Entity entity, BlockPos pos) {

        IBlockState state = entity.world.getBlockState(pos);
        SoundType soundtype = state.getBlock().getSoundType(state, entity.world, pos, entity);
        entity.playSound(soundtype.getFallSound(), soundtype.getVolume() * 0.5F, soundtype.getPitch());

    }

    private static void spawnWallParticle(Entity entity, BlockPos blockpos) {

        IBlockState iblockstate = entity.world.getBlockState(blockpos);
        if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {

            Vec3i motion = getClingDirection().getDirectionVec();
            entity.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, entity.posX, entity.posY, entity.posZ, motion.getX() * -0.1D, -0.1D, motion.getZ() * -0.1D, Block.getStateId(iblockstate));

        }

    }

}