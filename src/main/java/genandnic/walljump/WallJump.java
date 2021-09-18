package genandnic.walljump;

import genandnic.walljump.enchantment.DoubleJumpEnchantment;
import genandnic.walljump.enchantment.SpeedBoostEnchantment;
import genandnic.walljump.enchantment.WallJumpEnchantment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.aeonbits.owner.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class WallJump implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("WallJump");

	public static Enchantment WALLJUMP_ENCHANTMENT;
	public static Enchantment DOUBLEJUMP_ENCHANTMENT;
	public static Enchantment SPEEDBOOST_ENCHANTMENT;

	public static WallJumpConfig CONFIGURATION;


	public static final Identifier FALL_DISTANCE_PACKET_ID = new Identifier("walljump", "falldistance");
	public static final Identifier WALL_JUMP_PACKET_ID = new Identifier("walljump", "walljump");

	@Override
	public void onInitialize() {

		// Configuration
		File configFile = new File(
				FabricLoader.getInstance().getConfigDirectory(),
				"wall-jump.properties"
		);

		try {
			if (configFile.createNewFile()) {

				LOGGER.info("[Wall Jump] creating default config file");
				CONFIGURATION = ConfigFactory.create(WallJumpConfig.class);
				CONFIGURATION.store(new FileOutputStream(configFile), "automatically generated default config file");

			} else {

				LOGGER.info("[Wall Jump] loading config from file");
				Properties props = new Properties();
				props.load(new FileInputStream(configFile));
				CONFIGURATION = ConfigFactory.create(WallJumpConfig.class, props);

			}

		} catch(IOException e) {
			LOGGER.error("[Wall Jump] failed to load config file !");
			e.printStackTrace();
		}

		// Enchantments
		WALLJUMP_ENCHANTMENT = Registry.register(
				Registry.ENCHANTMENT,
				new Identifier("walljump", "walljump"),
				new WallJumpEnchantment(
						Enchantment.Rarity.UNCOMMON,
						EnchantmentTarget.ARMOR_FEET,
						new EquipmentSlot[] {
								EquipmentSlot.FEET
						}
				)
		);

		DOUBLEJUMP_ENCHANTMENT = Registry.register(
				Registry.ENCHANTMENT,
				new Identifier("walljump", "doublejump"),
				new DoubleJumpEnchantment(
						Enchantment.Rarity.RARE,
						EnchantmentTarget.ARMOR_FEET,
						new EquipmentSlot[] {
								EquipmentSlot.FEET
						}
				)
		);

		SPEEDBOOST_ENCHANTMENT = Registry.register(
				Registry.ENCHANTMENT,
				new Identifier("walljump", "speedboost"),
				new SpeedBoostEnchantment(
						Enchantment.Rarity.RARE,
						EnchantmentTarget.ARMOR_FEET,
						new EquipmentSlot[] {
								EquipmentSlot.FEET
						}
				)
		);

		// Packets
		ServerPlayNetworking.registerGlobalReceiver(FALL_DISTANCE_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			float fallDistance = buf.readFloat();
			server.execute(() -> {
				player.fallDistance = fallDistance;
			});
		});

		ServerPlayNetworking.registerGlobalReceiver(WALL_JUMP_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			boolean didWallJump = buf.readBoolean();

			server.execute(() -> {
				if(didWallJump)
					player.addExhaustion((float) WallJump.CONFIGURATION.exhaustionWallJump());
			});
		});

		LOGGER.info("[Wall Jump] initialized!");
	}
}
