package genandnic.walljump;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class WallJumpClient implements ClientModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("WallJumpClient");

	public static FallingSound FALLING_SOUND;

	@Override
	public void onInitializeClient() {

		FALLING_SOUND = new FallingSound(MinecraftClient.getInstance().player);

		LOGGER.info("[Wall Jump Client] initialized!");
	}
}
