package genandnic.walljump.mixin.client;

import com.mojang.authlib.GameProfile;
import genandnic.walljump.FallingSound;
import genandnic.walljump.WallJump;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import genandnic.walljump.WallJumpClient;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMiscellaneousMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMiscellaneousMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void miscellaneousTickMovement(CallbackInfo ci) {

        if(this.horizontalCollision
                && WallJump.CONFIGURATION.stepAssist()
                && this.getVelocity().getY() > -0.2
                && this.getVelocity().getY() < 0.01
        ) {

            if(this.world.doesNotCollide(this.getBoundingBox().expand(0.01, -this.stepHeight + 0.02, 0.01))) {

                this.onGround = true;

            }
        }

        // This can be omitted:
        // TODO: no idea where the sprintingTicksLeft logic is
        // if (pl.sprintingTicksLeft > 0 && pl.getMotion().length() > 0.08)
        //     pl.collidedHorizontally = false;

        if(this.fallDistance > 1.5 && !this.isFallFlying()) {

//             This also can be omitted:
//             if(WallJump.CONFIGURATION.minFallDistance() > 127) {
//                 this.fallDistance = 0.0F;
//                 // TODO: no idea what the CPlayerPacket is
//                 // pl.connection.sendPacket(new CPlayerPacket(true));
//             }

            if(WallJump.CONFIGURATION.playFallSound() && WallJumpClient.FALLING_SOUND.isDone()) {

                WallJumpClient.FALLING_SOUND = new FallingSound((ClientPlayerEntity) (Object) this);
                MinecraftClient.getInstance().getSoundManager().play(WallJumpClient.FALLING_SOUND);

            }
        }
    }
}
