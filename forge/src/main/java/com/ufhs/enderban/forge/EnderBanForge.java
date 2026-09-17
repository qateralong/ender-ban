package com.ufhs.enderban.forge;

import com.mojang.brigadier.context.CommandContext;
import com.ufhs.enderban.forge.config.EnderBanConfig;
import com.ufhs.enderban.forge.lang.LangManager;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(EnderBanForge.MOD_ID)
public class EnderBanForge {
	public static final String MOD_ID = "enderban";
	private static final Set<Item> BANNED = new HashSet<>();
	private static LangManager langManager;

	private final Path configDir;

	public EnderBanForge() {
		this.configDir = FMLPaths.CONFIGDIR.get().resolve("enderban");
		langManager = new LangManager(this.configDir.resolve("lang"));
		this.loadConfig();

		RegisterCommandsEvent.BUS.addListener(this::onRegisterCommands);
	}

	private void onRegisterCommands(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("ebreload")
			.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
			.executes(this::reload));
	}

	private int reload(CommandContext<CommandSourceStack> context) {
		this.loadConfig();
		context.getSource().sendSuccess(() -> langManager.get("reload-success"), false);
		return 1;
	}

	private void loadConfig() {
		EnderBanConfig config = EnderBanConfig.loadOrCreate(this.configDir.resolve("config.json"));
		BANNED.clear();

		for (String raw : config.getBannedMaterials()) {
			String name = raw.trim();
			if (name.isEmpty()) {
				continue;
			}

			Identifier id = Identifier.tryParse(name);
			Item item = id == null ? null : BuiltInRegistries.ITEM.getOptional(id).orElse(null);
			if (item != null) {
				BANNED.add(item);
			}
		}

		langManager.reload(config.getLang());
	}

	public static boolean isBanned(ItemStack stack) {
		return !stack.isEmpty() && BANNED.contains(stack.getItem());
	}

	public static void notifyForbidden(ServerPlayer player) {
		player.sendSystemMessage(langManager.get("forbidden"));
	}
}
