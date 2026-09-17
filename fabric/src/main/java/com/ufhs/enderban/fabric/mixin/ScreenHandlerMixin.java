package com.ufhs.enderban.fabric.mixin;

import com.ufhs.enderban.fabric.EnderBanFabric;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {

	@Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
	private void enderban$onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
		if (!(player instanceof ServerPlayerEntity serverPlayer)) {
			return;
		}

		ScreenHandler self = (ScreenHandler) (Object) this;
		if (self.slots.isEmpty()) {
			return;
		}

		Inventory topInventory = self.slots.get(0).inventory;
		if (!(topInventory instanceof EnderChestInventory)) {
			return;
		}

		boolean isTopSlot = slotIndex >= 0 && slotIndex < topInventory.size();

		switch (actionType) {
			case PICKUP -> {
				if (isTopSlot && EnderBanFabric.isBanned(self.getCursorStack())) {
					ci.cancel();
					EnderBanFabric.notifyForbidden(serverPlayer);
				}
			}
			case SWAP -> {
				if (isTopSlot) {
					ItemStack hotbarItem = serverPlayer.getInventory().getStack(button);
					if (EnderBanFabric.isBanned(hotbarItem)) {
						ci.cancel();
						EnderBanFabric.notifyForbidden(serverPlayer);
					}
				}
			}
			case QUICK_MOVE -> {
				if (!isTopSlot && slotIndex < self.slots.size()) {
					Slot slot = self.getSlot(slotIndex);
					if (EnderBanFabric.isBanned(slot.getStack())) {
						ci.cancel();
						EnderBanFabric.notifyForbidden(serverPlayer);
					}
				}
			}
			case QUICK_CRAFT -> {
				boolean addingToSlots = (button & 3) == 1;
				if (isTopSlot && addingToSlots && EnderBanFabric.isBanned(self.getCursorStack())) {
					ci.cancel();
					EnderBanFabric.notifyForbidden(serverPlayer);
				}
			}
			default -> {
			}
		}
	}
}
