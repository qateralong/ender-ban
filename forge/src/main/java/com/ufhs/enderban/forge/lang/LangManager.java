package com.ufhs.enderban.forge.lang;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;

public class LangManager {
	private static final List<String> SUPPORTED = List.of("en", "ru", "es");
	private static final String DEFAULT_LANG = "en";
	private static final Gson GSON = new Gson();

	private final Path langDir;
	private final Map<String, String> messages = new HashMap<>();
	private String currentLang = DEFAULT_LANG;

	public LangManager(Path langDir) {
		this.langDir = langDir;
		this.saveBundledLangFiles();
	}

	private void saveBundledLangFiles() {
		try {
			Files.createDirectories(this.langDir);
		} catch (IOException ignored) {
		}

		for (String code : SUPPORTED) {
			Path target = this.langDir.resolve("messages_" + code + ".json");
			if (!Files.exists(target)) {
				this.copyBundled(code, target);
			}
		}
	}

	private void copyBundled(String code, Path target) {
		try (InputStream in = LangManager.class.getResourceAsStream("/enderban/lang/messages_" + code + ".json")) {
			if (in != null) {
				Files.copy(in, target);
			}
		} catch (IOException ignored) {
		}
	}

	public void reload(String configuredLang) {
		this.messages.clear();
		String normalized = configuredLang == null ? DEFAULT_LANG : configuredLang.trim().toLowerCase();
		if (!SUPPORTED.contains(normalized)) {
			normalized = DEFAULT_LANG;
		}

		this.currentLang = normalized;
		this.loadInto(this.messages, DEFAULT_LANG);
		if (!normalized.equals(DEFAULT_LANG)) {
			this.loadInto(this.messages, normalized);
		}
	}

	private void loadInto(Map<String, String> target, String code) {
		Path file = this.langDir.resolve("messages_" + code + ".json");
		Map<String, String> parsed = null;

		if (Files.exists(file)) {
			try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
				parsed = GSON.fromJson(reader, new TypeToken<Map<String, String>>() {}.getType());
			} catch (IOException ignored) {
			}
		} else {
			try (InputStream in = LangManager.class.getResourceAsStream("/enderban/lang/messages_" + code + ".json")) {
				if (in != null) {
					parsed = GSON.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), new TypeToken<Map<String, String>>() {}.getType());
				}
			} catch (IOException ignored) {
			}
		}

		if (parsed != null) {
			target.putAll(parsed);
		}
	}

	public String getCurrentLang() {
		return this.currentLang;
	}

	public Component get(String key) {
		String raw = this.messages.getOrDefault(key, key);
		return Component.literal(raw.replaceAll("(?i)&([0-9a-fk-or])", "§$1"));
	}
}
