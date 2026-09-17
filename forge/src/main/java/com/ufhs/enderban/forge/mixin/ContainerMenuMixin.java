package com.ufhs.enderban.forge.mixin;

import com.ufhs.enderban.forge.EnderBanForge;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractContainerMenu.class, remap = false)
public abstract class ContainerMenuMixin {

	@Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
	private void enderban$clicked(int slotIndex, int button, ClickType clickType, Player player, CallbackInfo ci) {
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}

		AbstractContainerMenu self = (AbstractContainerMenu) (Object) this;
		if (self.slots.isEmpty()) {
			return;
		}

		Container topInventory = self.slots.get(0).container;
		if (!(topInventory instanceof PlayerEnderChestContainer)) {
			return;
		}

		boolean isTopSlot = slotIndex >= 0 && slotIndex < topInventory.getContainerSize();

		switch (clickType) {
			case PICKUP -> {
				if (isTopSlot && EnderBanForge.isBanned(self.getCarried())) {
					ci.cancel();
					EnderBanForge.notifyForbidden(serverPlayer);
				}
			}
			case SWAP -> {
				if (isTopSlot) {
					ItemStack hotbarItem = serverPlayer.getInventory().getItem(button);
					if (EnderBanForge.isBanned(hotbarItem)) {
						ci.cancel();
						EnderBanForge.notifyForbidden(serverPlayer);
					}
				}
			}
			case QUICK_MOVE -> {
				if (!isTopSlot && slotIndex < self.slots.size()) {
					Slot slot = self.getSlot(slotIndex);
					if (EnderBanForge.isBanned(slot.getItem())) {
						ci.cancel();
						EnderBanForge.notifyForbidden(serverPlayer);
					}
				}
			}
			case QUICK_CRAFT -> {
				boolean addingToSlots = (button & 3) == 1;
				if (isTopSlot && addingToSlots && EnderBanForge.isBanned(self.getCarried())) {
					ci.cancel();
					EnderBanForge.notifyForbidden(serverPlayer);
				}
			}
			default -> {
			}
		}
	}
}
