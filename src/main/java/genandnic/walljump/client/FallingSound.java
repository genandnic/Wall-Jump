package genandnic.walljump.client;

import net.minecraft.client.audio.TickableSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FallingSound extends TickableSound {

    private final ClientPlayerEntity player;

    public FallingSound(ClientPlayerEntity player) {
        super(SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.PLAYERS);
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = Float.MIN_VALUE;
    }

    public void tick() {

        float length = (float) player.getMotion().lengthSquared();
        if (length >= 1.0 && player.isAlive()) {

            volume = MathHelper.clamp((length - 1.0F) / 4.0F, 0.0F, 2.0F);
            if (volume > 0.8F) {
                pitch = 1.0F + (volume - 0.8F);
            } else {
                pitch = 1.0F;
            }

        } else {
            donePlaying = true;
        }

    }

}