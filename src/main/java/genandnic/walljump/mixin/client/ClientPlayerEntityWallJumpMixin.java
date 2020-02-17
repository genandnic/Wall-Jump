package genandnic.walljump.mixin.client;

import com.mojang.authlib.GameProfile;
import genandnic.walljump.TagListOperation;
import genandnic.walljump.WallJump;
import genandnic.walljump.WallJumpConfig;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityWallJumpMixin extends AbstractClientPlayerEntity {

    @Shadow public abstract boolean isRiding();

    @Shadow public abstract float getYaw(float tickDelta);

    @Shadow public Input input;

    public int ticksWallClinged;
    private int ticksKeyDown;
    private double clingX;
    private double clingZ;
    private double lastJumpY = Double.MAX_VALUE;
    private Set<Direction> walls = new HashSet<>();
    private Set<Direction> staleWalls = new HashSet<>();


    public ClientPlayerEntityWallJumpMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }


    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void wallJumpTickMovement(CallbackInfo ci) {
        this.doWallJump();
    }


    private void doWallJump() {

        if(!this.canWallJump()) return;

        if(this.onGround
                || this.abilities.flying
                || !this.world.getFluidState(this.getBlockPos()).isEmpty()
                || this.isRiding()
        ) {
            this.ticksWallClinged = 0;
            this.clingX = Double.NaN;
            this.clingZ = Double.NaN;
            this.lastJumpY = Double.MAX_VALUE;
            this.staleWalls.clear();

            return;
        }

        this.updateWalls();
        this.ticksKeyDown = input.sneaking ? this.ticksKeyDown + 1 : 0;

        if(this.ticksWallClinged < 1) {

            if (this.ticksKeyDown > 0
                    && this.ticksKeyDown < 4
                    && !this.walls.isEmpty()
                    && this.canWallCling()
            ) {

                this.limbDistance = 2.5F;
                this.lastLimbDistance = 2.5F;

                if (WallJump.CONFIGURATION.autoRotation()) {
                    this.yaw = this.getClingDirection().getOpposite().asRotation();
                    this.prevYaw = this.yaw;
                }

                this.ticksWallClinged = 1;
                this.clingX = this.getX();
                this.clingZ = this.getZ();

                this.playHitSound(this.getWallPos());
                this.spawnWallParticle(this.getWallPos());
            }

            return;
        }

        if(!input.sneaking
                || this.onGround
                || !this.world.getFluidState(this.getBlockPos()).isEmpty()
                || this.walls.isEmpty()
                || this.getHungerManager().getFoodLevel() < 1
        ) {

            this.ticksWallClinged = 0;

            if((this.forwardSpeed != 0 || this.sidewaysSpeed != 0)
                    && !this.onGround
                    && !this.walls.isEmpty()
            ) {

                this.fallDistance = 0.0F;

                PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                passedData.writeBoolean(true);
                ClientSidePacketRegistry.INSTANCE.sendToServer(WallJump.WALL_JUMP_PACKET_ID, passedData);

                this.wallJump((float) WallJump.CONFIGURATION.wallJumpHeight());
                this.staleWalls = new HashSet<>(this.walls);
            }

            return;
        }

        if(WallJump.CONFIGURATION.autoRotation()) {
            this.yaw = this.getClingDirection().getOpposite().asRotation();
            this.prevYaw = this.yaw;
        }

        this.setPos(this.clingX, this.getY(), this.clingZ);

        double motionY = this.getVelocity().getY();

        if(motionY > 0.0) {

            motionY = 0.0;

        } else if(motionY < -0.6) {

            motionY = motionY + 0.2;
            this.spawnWallParticle(this.getWallPos());

        } else if(this.ticksWallClinged++ > WallJump.CONFIGURATION.wallSlideDelay()) {

            motionY = -0.1;
            this.spawnWallParticle(this.getWallPos());

        } else {

            motionY = 0.0;
        }

        if(this.fallDistance > 2) {

            this.fallDistance = 0;

            PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
            passedData.writeFloat((float) (motionY * motionY * 8));
            ClientSidePacketRegistry.INSTANCE.sendToServer(WallJump.FALL_DISTANCE_PACKET_ID, passedData);
        }

        this.setVelocity(0.0, motionY, 0.0);
    }


    private boolean canWallJump() {

        if(WallJump.CONFIGURATION.useWallJump()) return true;

        ItemStack stack = this.getEquippedStack(EquipmentSlot.FEET);
        if(!stack.isEmpty()) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            return enchantments.containsKey(WallJump.WALLJUMP_ENCHANTMENT);
        }

        return false;
    }


    private boolean canWallCling() {
        if(this.isClimbing() || this.getVelocity().getY() > 0.1 || this.getHungerManager().getFoodLevel() < 1)
            return false;

        if(!this.world.doesNotCollide(this.getBoundingBox().offset(0, -0.8, 0)))
            return false;

        Block wallBlock = this.getWallBlock();

        if (WallJump.CONFIGURATION.clingTagListOperation() == TagListOperation.BLACKLIST) {
            if(WallJump.CONFIGURATION.clingTags().stream().anyMatch(o -> BlockTags.getContainer().get(new Identifier(o)).contains(wallBlock)))
                return false;

        } else if (WallJump.CONFIGURATION.clingTagListOperation() == TagListOperation.WHITELIST) {
            if(WallJump.CONFIGURATION.clingTags().stream().noneMatch(o -> BlockTags.getContainer().get(new Identifier(o)).contains(wallBlock)))
                return false;

        }

        if(this.staleWalls.isEmpty() || !this.staleWalls.containsAll(this.walls) || this.getY() < this.lastJumpY - 1)
            return true;

        if (WallJump.CONFIGURATION.reClingTagListOperation() == TagListOperation.BLACKLIST) {
            if(WallJump.CONFIGURATION.reClingTags().stream().anyMatch(o -> BlockTags.getContainer().get(new Identifier(o)).contains(wallBlock)))
                return false;

        } else if (WallJump.CONFIGURATION.reClingTagListOperation() == TagListOperation.WHITELIST) {
            if(WallJump.CONFIGURATION.reClingTags().stream().noneMatch(o -> BlockTags.getContainer().get(new Identifier(o)).contains(wallBlock)))
                return false;

        }

        return true;
    }


    private void updateWalls() {

        Box box = new Box(
                this.getX() - 0.001,
                this.getY(),
                this.getZ() - 0.001,
                this.getX() + 0.001,
                this.getY() + this.getStandingEyeHeight(),
                this.getZ() + 0.001
        );

        double dist = (this.getWidth() / 2) + (this.ticksWallClinged > 0 ? 0.1 : 0.06);

        Box[] axes = {
                box.stretch(0, 0, dist),
                box.stretch(-dist, 0, 0),
                box.stretch(0, 0, -dist),
                box.stretch(dist, 0, 0)
        };

        int i = 0;
        Direction direction;
        this.walls = new HashSet<>();

        for (Box axis : axes) {
            direction = Direction.fromHorizontal(i++);

            if(!this.world.doesNotCollide(axis)) {
               this.walls.add(direction);
               this.horizontalCollision = true;
            }
        }
    }


    private Direction getClingDirection() {

        return this.walls.isEmpty() ? Direction.UP : this.walls.iterator().next();
    }


    private BlockPos getWallPos() {

        BlockPos pos = this.getBlockPos();
        BlockPos posUp = pos.offset(Direction.UP);
        BlockPos clingPos = pos.offset(this.getClingDirection());
        BlockPos clingPosUp = clingPos.offset(Direction.UP);

        if(this.world.getBlockState(pos).getMaterial().isSolid()) {
            LOGGER.info("current pos");
            return pos;

        } else if(this.world.getBlockState(posUp).getMaterial().isSolid()) {
            LOGGER.info("current pos up");
            return posUp;

        } else {
            LOGGER.info("cling pos or cling pos up");
            return this.world.getBlockState(clingPos).getMaterial().isSolid() ? clingPos : clingPosUp;

        }
    }


    private Block getWallBlock() {

        return this.world.getBlockState(this.getWallPos()).getBlock();
    }

    private void wallJump(float up) {

        float strafe = Math.signum(this.sidewaysSpeed) * up * up;
        float forward = Math.signum(this.forwardSpeed) * up * up;

        float f = 1.0F / MathHelper.sqrt(strafe * strafe + up * up + forward * forward);
        strafe = strafe * f;
        forward = forward * f;

        float f1 = MathHelper.sin(this.getHeadYaw() * 0.017453292F) * 0.45F;
        float f2 = MathHelper.cos(this.getHeadYaw() * 0.017453292F) * 0.45F;

        int jumpBoostLevel = 0;
        StatusEffectInstance jumpBoostEffect = this.getStatusEffect(StatusEffects.JUMP_BOOST);
        if(jumpBoostEffect != null) jumpBoostLevel = jumpBoostEffect.getAmplifier() + 1;

        Vec3d motion = this.getVelocity();
        this.setVelocity(
                motion.getX() + (strafe * f2 - forward * f1),
                up + (jumpBoostLevel * 0.125),
                motion.getZ() + (forward * f2 + strafe * f1)
        );

        this.lastJumpY = this.getY();
        this.playBreakSound(this.getWallPos());
        this.spawnWallParticle(this.getWallPos());
    }


    private void playHitSound(BlockPos blockPos) {

        BlockState blockState = this.world.getBlockState(blockPos);
        BlockSoundGroup soundType = blockState.getBlock().getSoundGroup(blockState);
        this.playSound(soundType.getHitSound(), soundType.getVolume() * 0.25F, soundType.getPitch());
    }


    private void playBreakSound(BlockPos blockPos) {

        BlockState blockState = this.world.getBlockState(blockPos);
        BlockSoundGroup soundType = blockState.getBlock().getSoundGroup(blockState);
        this.playSound(soundType.getFallSound(), soundType.getVolume() * 0.5F, soundType.getPitch());
    }


    private void spawnWallParticle(BlockPos blockPos) {

        BlockState blockState = this.world.getBlockState(blockPos);
        if(blockState.getRenderType() != BlockRenderType.INVISIBLE) {

            Vec3d pos = this.getPosVector();
            Vec3i motion = this.getClingDirection().getVector();
            this.world.addParticle(
                    new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState),
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    motion.getX() * -1.0D,
                    -1.0D,
                    motion.getZ() * -1.0D
            );
        }
    }
}
