package genandnic.walljump;

import genandnic.walljump.enchantment.DoubleJumpEnchantment;
import genandnic.walljump.enchantment.WallJumpEnchantment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.aeonbits.owner.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class WallJump implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("WallJump");

	public static Enchantment WALLJUMP_ENCHANTMENT;
	public static Enchantment DOUBLEJUMP_ENCHANTMENT;

	public static WallJumpConfig CONFIGURATION = ConfigFactory.create(WallJumpConfig.class);

	public static final Identifier FALL_DISTANCE_PACKET_ID = new Identifier("walljump", "falldistance");
	public static final Identifier WALL_JUMP_PACKET_ID = new Identifier("walljump", "walljump");

	@Override
	public void onInitialize() {
		WALLJUMP_ENCHANTMENT = Registry.register(
				Registry.ENCHANTMENT,
				new Identifier("walljump", "walljump"),
				new WallJumpEnchantment(
						Enchantment.Weight.UNCOMMON,
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
						Enchantment.Weight.RARE,
						EnchantmentTarget.ARMOR_FEET,
						new EquipmentSlot[] {
								EquipmentSlot.FEET
						}
				)
		);

		ServerSidePacketRegistry.INSTANCE.register(FALL_DISTANCE_PACKET_ID, ((packetContext, packetByteBuf) -> {
			float fallDistance = packetByteBuf.readFloat();
			packetContext.getTaskQueue().execute(() -> {
				packetContext.getPlayer().fallDistance = fallDistance;
			});
		}));

		ServerSidePacketRegistry.INSTANCE.register(WALL_JUMP_PACKET_ID, ((packetContext, packetByteBuf) -> {
			boolean didWallJump = packetByteBuf.readBoolean();

			packetContext.getTaskQueue().execute(() -> {
				if(didWallJump)
					packetContext.getPlayer().addExhaustion((float) WallJump.CONFIGURATION.exhaustionWallJump());
			});
		}));

		LOGGER.info("[Wall Jump] initialized!");
	}
}
