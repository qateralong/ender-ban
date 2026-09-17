package com.ufhs.enderban.fabric;

import com.mojang.brigadier.context.CommandContext;
import com.ufhs.enderban.fabric.config.EnderBanConfig;
import com.ufhs.enderban.fabric.lang.LangManager;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EnderBanFabric implements ModInitializer {
	private static final Set<Item> BANNED = new HashSet<>();
	private static LangManager langManager;

	private Path configDir;

	@Override
	public void onInitialize() {
		this.configDir = FabricLoader.getInstance().getConfigDir().resolve("enderban");
		langManager = new LangManager(this.configDir.resolve("lang"));
		this.loadConfig();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
			dispatcher.register(CommandManager.literal("ebreload")
				.requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
				.executes(this::reload)));
	}

	private int reload(CommandContext<ServerCommandSource> context) {
		this.loadConfig();
		context.getSource().sendFeedback(() -> langManager.get("reload-success"), false);
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
			Item item = id == null ? null : Registries.ITEM.getOptionalValue(id).orElse(null);
			if (item != null) {
				BANNED.add(item);
			}
		}

		langManager.reload(config.getLang());
	}

	public static boolean isBanned(ItemStack stack) {
		return !stack.isEmpty() && BANNED.contains(stack.getItem());
	}

	public static void notifyForbidden(ServerPlayerEntity player) {
		player.sendMessage(langManager.get("forbidden"));
	}
}
