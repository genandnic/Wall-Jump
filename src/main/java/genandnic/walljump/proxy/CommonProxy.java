package genandnic.walljump.proxy;

import genandnic.walljump.WallJumpConfig;
import genandnic.walljump.network.PacketHandler;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonProxy {

    public void setupCommon() {

        PacketHandler.init();
        MinecraftForge.EVENT_BUS.register(this);

    }

    public void setupClient() {

    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {

        float distance = event.getDistance();
        if (distance > 3 && distance <= WallJumpConfig.COMMON.minFallDistance.get()) {

            event.setDistance(3.0F);
            event.getEntity().playSound(SoundEvents.ENTITY_GENERIC_SMALL_FALL, 0.5F, 1.0F);

        }

    }

}
