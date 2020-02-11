package genandnic.walljump.mixin.client;

import com.mojang.authlib.GameProfile;
import genandnic.walljump.WallJump;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

//        if(this.fallDistance > 1.5 && !this.isFallFlying()) {
//
//            if(WallJump.CONFIGURATION.minFallDistance() > 127) {
//                this.fallDistance = 0.0F;
//                // TODO:  pl.connection.sendPacket(new CPlayerPacket(true));
//            }
//        }
    }
}
