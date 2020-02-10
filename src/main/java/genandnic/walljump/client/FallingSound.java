package genandnic.walljump.client;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FallingSound extends MovingSound {

    private final EntityPlayerSP player;

    public FallingSound(EntityPlayerSP player) {
        super(SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.PLAYERS);
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = Float.MIN_VALUE;
    }

    @Override
    public void update() {

        float length = (float) new Vec3d(player.motionX, player.motionY, player.motionZ).lengthSquared();
        if (length >= 1.0 && !this.player.isDead) {

            xPosF = (float) player.posX;
            yPosF = (float) player.posY;
            zPosF = (float) player.posZ;

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