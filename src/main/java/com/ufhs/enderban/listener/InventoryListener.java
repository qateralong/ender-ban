package com.ufhs.enderban.listener;

import com.ufhs.enderban.EnderBanPlugin;
import java.util.HashMap;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {
   private final EnderBanPlugin plugin;
   private final Set<Material> banned;

   public InventoryListener(EnderBanPlugin plugin, Set<Material> banned) {
      this.plugin = plugin;
      this.banned = banned;
   }

   private boolean isBanned(ItemStack item) {
      return item != null && this.banned.contains(item.getType());
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent event) {
      if (event.getView().getTopInventory().getType() == InventoryType.ENDER_CHEST) {
         Player player = (Player)event.getWhoClicked();
         Inventory clicked = event.getClickedInventory();
         boolean clickedIsEnderChest = clicked != null && clicked.getType() == InventoryType.ENDER_CHEST;
         if (clickedIsEnderChest && this.isBanned(event.getCursor())) {
            event.setCancelled(true);
            this.notifyForbidden(player);
         } else {
            if (clickedIsEnderChest && event.getClick() == ClickType.NUMBER_KEY) {
               ItemStack hotbarItem = player.getInventory().getItem(event.getHotbarButton());
               if (this.isBanned(hotbarItem)) {
                  event.setCancelled(true);
                  this.notifyForbidden(player);
                  return;
               }
            }

            if (!clickedIsEnderChest
               && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)
               && this.isBanned(event.getCurrentItem())) {
               event.setCancelled(true);
               this.notifyForbidden(player);
            }
         }
      }
   }

   @EventHandler
   public void onInventoryDrag(InventoryDragEvent event) {
      if (event.getView().getTopInventory().getType() == InventoryType.ENDER_CHEST) {
         boolean bannedInDrag = event.getNewItems().values().stream().anyMatch(this::isBanned);
         if (bannedInDrag) {
            event.setCancelled(true);
            this.notifyForbidden((Player)event.getWhoClicked());
         }
      }
   }

   private void returnOrDrop(Player player, ItemStack item) {
      HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(new ItemStack[]{item.clone()});
      if (!leftover.isEmpty()) {
         leftover.values().forEach(dropped -> {
            player.getWorld().dropItemNaturally(player.getLocation(), dropped);
            player.sendMessage(this.plugin.lang().get("returned-dropped"));
         });
      }
   }

   private void notifyForbidden(Player player) {
      player.sendMessage(this.plugin.lang().get("forbidden"));
   }
}
