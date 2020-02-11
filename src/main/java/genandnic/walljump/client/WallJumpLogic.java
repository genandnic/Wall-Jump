package genandnic.walljump.client;

import genandnic.walljump.WallJump;
import genandnic.walljump.WallJumpConfig;
import genandnic.walljump.network.PacketHandler;
import genandnic.walljump.network.message.MessageFallDistance;
import genandnic.walljump.network.message.MessageWallJump;
import genandnic.walljump.proxy.ClientProxy;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class WallJumpLogic {

    public static int ticksWallClinged;
    private static int ticksKeyDown;
    private static double clingX, clingZ;
    private static double lastJumpY = Double.MAX_VALUE;

    public static void doWallJump(ClientPlayerEntity pl) {

        if(!WallJumpLogic.canWallJump(pl))
            return;

        if (pl.onGround || pl.abilities.isFlying || pl.isInWater()) {

            ticksWallClinged = 0;
            clingX = Double.NaN;
            clingZ = Double.NaN;
            lastJumpY = Double.MAX_VALUE;
            staleWalls.clear();

            return;
        }

        WallJumpLogic.updateWalls(pl);
        ticksKeyDown = ClientProxy.KEY_WALLJUMP.isKeyDown() ? ticksKeyDown + 1 : 0;

        if (ticksWallClinged < 1) {

            if (ticksKeyDown > 0 && ticksKeyDown < 4 && !walls.isEmpty() && canWallCling(pl)) {

                pl.limbSwingAmount = 2.5F;
                if (WallJumpConfig.COMMON.autoRotation.get()) pl.rotationYaw = getClingDirection().getOpposite().getHorizontalAngle();

                ticksWallClinged = 1;
                clingX = pl.getPositionVec().x;
                clingZ = pl.getPositionVec().z;

                playHitSound(pl, getWallPos(pl));
                spawnWallParticle(pl, getWallPos(pl));
            }

            return;
        }

        if (!ClientProxy.KEY_WALLJUMP.isKeyDown() || pl.onGround || pl.isInWater() || walls.isEmpty() || pl.getFoodStats().getFoodLevel() < 1) {

            ticksWallClinged = 0;

            if ((pl.moveForward != 0 || pl.moveStrafing != 0) && !pl.onGround && !walls.isEmpty()) {

                pl.fallDistance = 0.0F;
                PacketHandler.instance.sendToServer(new MessageWallJump());

                wallJump(pl, WallJumpConfig.COMMON.wallJumpHeight.get().floatValue());
                staleWalls = new HashSet<>(walls);

            }

            return;
        }

        if (WallJumpConfig.COMMON.autoRotation.get()) pl.rotationYaw = getClingDirection().getOpposite().getHorizontalAngle();
        pl.setPosition(clingX, pl.getPositionVec().y, clingZ);

        Double motionY = pl.getMotion().y;
        if (motionY > 0.0) {
            motionY = 0.0;
        } else if (motionY < -0.6) {
            motionY = motionY + 0.2;
            spawnWallParticle(pl, getWallPos(pl));
        } else if (ticksWallClinged++ > WallJumpConfig.COMMON.wallSlideDelay.get()) {
            motionY = -0.1;
            spawnWallParticle(pl, getWallPos(pl));
        } else {
            motionY = 0.0;
        }

        if (pl.fallDistance > 2) {
            pl.fallDistance = 0;
            PacketHandler.instance.sendToServer(new MessageFallDistance((float) (motionY * motionY * 8)));
        }

        pl.setMotion(0.0, motionY, 0.0);
    }

    private static boolean canWallJump(PlayerEntity pl) {

        if (WallJumpConfig.COMMON.useWallJump.get()) return true;

        ItemStack stack = pl.getItemStackFromSlot(EquipmentSlotType.FEET);
        if (!stack.isEmpty()) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            return enchantments.containsKey(WallJump.WALLJUMP_ENCHANT);
        }

        return false;
    }

    private static boolean canWallCling(ClientPlayerEntity pl) {

        if (pl.isOnLadder() || pl.getMotion().y > 0.1 || pl.getFoodStats().getFoodLevel() < 1)
            return false;

        if (ClientProxy.collidesWithBlock(pl.world,pl.getBoundingBox().offset(0, -0.8, 0))) return false;

        if (WallJumpConfig.COMMON.allowReClinging.get() || pl.getPositionVec().y < lastJumpY - 1) return true;

        if (staleWalls.containsAll(walls)) return false;

        return true;
    }

    private static Set<Direction> walls = new HashSet<Direction>();
    private static Set<Direction> staleWalls = new HashSet<Direction>();

    private static void updateWalls(ClientPlayerEntity pl) {

        Vec3d pos = pl.getPositionVec();
        AxisAlignedBB box = new AxisAlignedBB(pos.x - 0.001, pos.y, pos.z - 0.001, pos.x + 0.001, pos.y + pl.getEyeHeight(), pos.z + 0.001);

        double dist = (pl.getWidth() / 2) + (ticksWallClinged > 0 ? 0.1 : 0.06);
        AxisAlignedBB[] axes = {box.expand(0, 0, dist), box.expand(-dist, 0, 0), box.expand(0, 0, -dist), box.expand(dist, 0, 0)};

        int i = 0;
        Direction direction = Direction.UP;
        WallJumpLogic.walls = new HashSet<Direction>();
        for (AxisAlignedBB axis : axes) {
            direction = Direction.byHorizontalIndex(i++);
            if (ClientProxy.collidesWithBlock(pl.world, axis)) {
                walls.add(direction);
                pl.collidedHorizontally = true;
            }
        }

    }

    private static Direction getClingDirection() {
        return walls.isEmpty() ? Direction.UP : walls.iterator().next();
    }

    private static BlockPos getWallPos(ClientPlayerEntity player) {

        BlockPos pos = player.getPosition().offset(getClingDirection());
        return player.world.getBlockState(pos).getMaterial().isSolid() ? pos : pos.offset(Direction.UP);

    }

    private static void wallJump(ClientPlayerEntity pl, float up) {

        float strafe = Math.signum(pl.moveStrafing) * up * up;
        float forward = Math.signum(pl.moveForward) * up * up;

        float f = 1.0F / MathHelper.sqrt(strafe * strafe + up * up + forward * forward);
        strafe = strafe * f;
        forward = forward * f;

        float f1 = MathHelper.sin(pl.rotationYaw * 0.017453292F) * 0.45f;
        float f2 = MathHelper.cos(pl.rotationYaw * 0.017453292F) * 0.45f;

        int jumpBoostLevel = 0;
        EffectInstance jumpBoostEffect = pl.getActivePotionEffect(Effect.get(8));
        if (jumpBoostEffect != null) jumpBoostLevel = jumpBoostEffect.getAmplifier() + 1;

        Vec3d motion = pl.getMotion();
        pl.setMotion(motion.x + (strafe * f2 - forward * f1), up + (jumpBoostLevel * .125), motion.z + (forward * f2 + strafe * f1));

        lastJumpY = pl.getPositionVec().y;
        playBreakSound(pl, getWallPos(pl));
        spawnWallParticle(pl, getWallPos(pl));

    }

    private static void playHitSound(Entity entity, BlockPos pos) {

        BlockState state = entity.world.getBlockState(pos);
        SoundType soundtype = state.getBlock().getSoundType(state, entity.world, pos, entity);
        entity.playSound(soundtype.getHitSound(), soundtype.getVolume() * 0.25F, soundtype.getPitch());

    }

    private static void playBreakSound(Entity entity, BlockPos pos) {

        BlockState state = entity.world.getBlockState(pos);
        SoundType soundtype = state.getBlock().getSoundType(state, entity.world, pos, entity);
        entity.playSound(soundtype.getFallSound(), soundtype.getVolume() * 0.5F, soundtype.getPitch());

    }

    private static void spawnWallParticle(Entity entity, BlockPos blockPos) {

        BlockState state = entity.world.getBlockState(blockPos);
        if (state.getRenderType() != BlockRenderType.INVISIBLE) {

            Vec3d pos = entity.getPositionVec();
            Vec3i motion = getClingDirection().getDirectionVec();
            entity.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, state).setPos(blockPos), pos.x, pos.y,
                    pos.z, motion.getX() * -1.0D, -1.0D, motion.getZ() * -1.0D);

        }

    }

}
