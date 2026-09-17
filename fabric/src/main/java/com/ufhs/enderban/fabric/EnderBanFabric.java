package com.ufhs.enderban.fabric;

import net.fabricmc.api.ModInitializer;

// Fork skeleton: the Paper plugin's logic lives in
// com.ufhs.enderban.{EnderBanPlugin,lang.LangManager,listener.InventoryListener}.
// Porting notes for whoever picks this up:
//  - Config/lang loading (YAML, banned-materials list) has no Bukkit dependency
//    and can be ported almost as-is; swap FileConfiguration/YamlConfiguration
//    for a small hand-rolled YAML reader or a Fabric-friendly library.
//  - There is no InventoryClickEvent equivalent. The closest hooks are
//    fabric-api's ScreenEvents (BEFORE_HANDLED_QUICK_TRANSFER/etc.) on the
//    server-side GenericContainerScreenHandler, or a mixin into
//    EnderChestInventory/ScreenHandler#onSlotClick to cancel the transfer.
public class EnderBanFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		// TODO: load config + lang, mirror EnderBanPlugin#loadConfigToMemory
		// TODO: register the Ender Chest interaction hook described above
	}
}
