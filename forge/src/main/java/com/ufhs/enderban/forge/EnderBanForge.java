package com.ufhs.enderban.forge;

import net.minecraftforge.fml.common.Mod;

// Fork skeleton, see ../../fabric/src/.../EnderBanFabric.java for the same
// notes on porting config/lang loading. Forge also has no InventoryClickEvent
// equivalent - look at PlayerContainerEvent.Open plus a mixin/ASM hook into
// EnderChestInventory, or ContainerEventHandler on the relevant Container.
@Mod(EnderBanForge.MOD_ID)
public class EnderBanForge {
	public static final String MOD_ID = "enderban";

	public EnderBanForge() {
		// TODO: load config + lang, mirror EnderBanPlugin#loadConfigToMemory
		// TODO: register the Ender Chest interaction hook described above
	}
}
