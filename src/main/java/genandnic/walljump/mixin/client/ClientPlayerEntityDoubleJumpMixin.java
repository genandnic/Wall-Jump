package genandnic.walljump.mixin.client;

import com.mojang.authlib.GameProfile;
import genandnic.walljump.ClientPlayerEntityWallJumpInterface;
import genandnic.walljump.WallJump;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityDoubleJumpMixin extends AbstractClientPlayerEntity implements ClientPlayerEntityWallJumpInterface {

    @Shadow public abstract boolean isRiding();

    @Shadow public Input input;

    private int jumpCount = 0;
    private boolean jumpKey = false;


    public ClientPlayerEntityDoubleJumpMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }


    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void doubleJumpTickMovement(CallbackInfo ci) {
        this.doDoubleJump();
    }


    private void doDoubleJump() {

        Vec3d pos = this.getPos();
        Vec3d motion = this.getVelocity();

        Box box = new Box(
                pos.getX(),
                pos.getY() + this.getEyeHeight(this.getPose()) * 0.8,
                pos.getZ(),
                pos.getX(),
                pos.getY() + this.getHeight(),
                pos.getZ()
        );

        if(this.onGround
                || this.world.containsFluid(box)
                || this.ticksWallClinged > 0
                || this.isRiding()
                || this.getAbilities().allowFlying
        ) {

            this.jumpCount = this.getMultiJumps();

        } else if(this.input.jumping) {

           if(!this.jumpKey
                   && this.jumpCount > 0
                   && motion.getY() < 0.333
                   && this.ticksWallClinged < 1
                   && this.getHungerManager().getFoodLevel() > 0
           ) {

               this.jump();
               this.jumpCount--;

               this.fallDistance = 0.0F;

               PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
               passedData.writeFloat(this.fallDistance);
               ClientSidePacketRegistry.INSTANCE.sendToServer(WallJump.FALL_DISTANCE_PACKET_ID, passedData);
           }

           this.jumpKey = true;

        } else {

            this.jumpKey = false;

        }
    }

    private int getMultiJumps() {

        int jumpCount = 0;
        if(WallJump.CONFIGURATION.useDoubleJump())
            jumpCount += 1;

        ItemStack stack = this.getEquippedStack(EquipmentSlot.FEET);
        if(!stack.isEmpty()) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
            if(enchantments.containsKey(WallJump.DOUBLEJUMP_ENCHANTMENT))
                jumpCount += enchantments.get(WallJump.DOUBLEJUMP_ENCHANTMENT);
        }

        return jumpCount;
    }
}
