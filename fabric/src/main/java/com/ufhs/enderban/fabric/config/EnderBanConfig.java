package com.ufhs.enderban.fabric.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class EnderBanConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private String lang = "en";

	@SerializedName("banned-materials")
	private List<String> bannedMaterials = List.of("minecraft:air");

	public String getLang() {
		return this.lang;
	}

	public List<String> getBannedMaterials() {
		return this.bannedMaterials;
	}

	public static EnderBanConfig loadOrCreate(Path path) {
		if (Files.exists(path)) {
			try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
				EnderBanConfig config = GSON.fromJson(reader, EnderBanConfig.class);
				if (config != null) {
					return config;
				}
			} catch (IOException ignored) {
			}
		}

		EnderBanConfig defaults = new EnderBanConfig();
		defaults.save(path);
		return defaults;
	}

	public void save(Path path) {
		try {
			Files.createDirectories(path.getParent());
			try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
				GSON.toJson(this, writer);
			}
		} catch (IOException ignored) {
		}
	}
}
