package com.ufhs.enderban;

import com.ufhs.enderban.lang.LangManager;
import com.ufhs.enderban.listener.InventoryListener;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class EnderBanPlugin extends JavaPlugin {
   private final Set<Material> banned = new HashSet<>();
   private LangManager langManager;

   public void onEnable() {
      this.saveDefaultConfig();
      this.langManager = new LangManager(this);
      this.loadConfigToMemory();
      Bukkit.getPluginManager().registerEvents(new InventoryListener(this, this.banned), this);
      this.getLogger().info("EnderBan enabled (server: " + Bukkit.getName() + " " + Bukkit.getBukkitVersion() + ")");
   }

   public void onDisable() {
      this.getLogger().info("EnderBan disabled");
   }

   public LangManager lang() {
      return this.langManager;
   }

   public void loadConfigToMemory() {
      this.banned.clear();
      FileConfiguration config = this.getConfig();

      for (String raw : config.getStringList("banned-materials")) {
         String name = raw.trim();
         if (!name.isEmpty()) {
            Material material = Material.matchMaterial(name);
            if (material == null) {
               this.getLogger().warning("config.yml: unknown material '" + name + "'");
            } else {
               this.banned.add(material);
            }
         }
      }

      this.langManager.reload();
      this.getLogger().info("EnderBan: loaded " + this.banned.size() + " banned material(s), language: " + this.langManager.getCurrentLang());
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!command.getName().equalsIgnoreCase("ebreload")) {
         return false;
      } else if (!sender.hasPermission("enderban.reload")) {
         sender.sendMessage(this.langManager.get("no-permission"));
         return true;
      } else {
         this.reloadConfig();
         this.loadConfigToMemory();
         sender.sendMessage(this.langManager.get("reload-success"));
         return true;
      }
   }
}
