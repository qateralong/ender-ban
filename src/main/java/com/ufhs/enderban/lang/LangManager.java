package com.ufhs.enderban.lang;

import com.ufhs.enderban.EnderBanPlugin;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LangManager {
   private static final List<String> SUPPORTED = List.of("en", "ru", "es");
   private static final String DEFAULT_LANG = "en";
   private final EnderBanPlugin plugin;
   private final Map<String, String> messages = new HashMap<>();
   private String currentLang = "en";

   public LangManager(EnderBanPlugin plugin) {
      this.plugin = plugin;
      this.saveBundledLangFiles();
   }

   private void saveBundledLangFiles() {
      File langDir = new File(this.plugin.getDataFolder(), "lang");
      if (!langDir.exists()) {
         langDir.mkdirs();
      }

      for (String code : SUPPORTED) {
         File target = new File(langDir, "messages_" + code + ".yml");
         if (!target.exists()) {
            this.plugin.saveResource("lang/messages_" + code + ".yml", false);
         }
      }
   }

   public void reload() {
      this.messages.clear();
      String configured = this.plugin.getConfig().getString("lang", "en");
      String normalized = configured == null ? "en" : configured.trim().toLowerCase();
      if (!SUPPORTED.contains(normalized)) {
         this.plugin.getLogger().warning("config.yml: unknown lang '" + configured + "', falling back to 'en'");
         normalized = "en";
      }

      this.currentLang = normalized;
      this.loadInto(this.messages, "en");
      if (!normalized.equals("en")) {
         this.loadInto(this.messages, normalized);
      }
   }

   private void loadInto(Map<String, String> target, String code) {
      File file = new File(new File(this.plugin.getDataFolder(), "lang"), "messages_" + code + ".yml");
      FileConfiguration yaml;
      if (file.exists()) {
         yaml = YamlConfiguration.loadConfiguration(file);
      } else {
         try {
            label65: {
               try (InputStream in = this.plugin.getResource("lang/messages_" + code + ".yml")) {
                  if (in != null) {
                     yaml = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
                     break label65;
                  }
               }

               return;
            }
         } catch (Exception var10) {
            return;
         }
      }

      for (String key : yaml.getKeys(false)) {
         target.put(key, yaml.getString(key, ""));
      }
   }

   public String getCurrentLang() {
      return this.currentLang;
   }

   public String get(String key) {
      String raw = this.messages.getOrDefault(key, key);
      return ChatColor.translateAlternateColorCodes('&', raw);
   }
}
