package genandnic.walljump;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;


@Environment(EnvType.CLIENT)
public class FallingSound extends MovingSoundInstance {

    private final ClientPlayerEntity player;

    public FallingSound(ClientPlayerEntity player) {
        super(SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.PLAYERS);
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = Float.MIN_VALUE;
    }

    @Override
    public void tick() {

        float length = (float) player.getVelocity().lengthSquared();
        if(length >= 1.0 && player.isAlive()) {

            this.volume = MathHelper.clamp((length - 1.0F) / 4.0F, 0.0F, 2.0F);

            if(this.volume > 0.8) {
                this.pitch = 1.0F + (this.volume - 0.8F);
            } else {
                this.pitch = 1.0F;
            }

        } else {

            this.done = true;

        }
    }
}

