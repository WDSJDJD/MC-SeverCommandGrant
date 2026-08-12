package com.example.commandgrant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionManager {

    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("commandgrant.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Map<UUID, Set<String>> PERMISSIONS = new ConcurrentHashMap<>();

    public static void load() {
        if (!Files.exists(FILE)) {
            return;
        }

        try (Reader reader = Files.newBufferedReader(FILE)) {
            Type type = new TypeToken<Map<String, Set<String>>>(){}.getType();
            Map<String, Set<String>> raw = GSON.fromJson(reader, type);

            PERMISSIONS.clear();

            if (raw != null) {
                raw.forEach((uuidString, commands) -> {
                    try {
                        UUID uuid = UUID.fromString(uuidString);
                        Set<String> set = commands == null
                                ? new HashSet<>()
                                : new HashSet<>(commands);
                        PERMISSIONS.put(uuid, Collections.synchronizedSet(set));
                    } catch (IllegalArgumentException ignored) {
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());

            Map<String, Set<String>> raw = new HashMap<>();
            PERMISSIONS.forEach((uuid, commands) ->
                    raw.put(uuid.toString(), new HashSet<>(commands)));

            try (Writer writer = Files.newBufferedWriter(FILE)) {
                GSON.toJson(raw, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasPermission(UUID uuid, String command) {
        Set<String> commands = PERMISSIONS.get(uuid);
        return commands != null && commands.contains(command.toLowerCase(Locale.ROOT));
    }

    public static void grant(UUID uuid, String command) {
        PERMISSIONS.computeIfAbsent(uuid, k -> Collections.synchronizedSet(new HashSet<>()))
                .add(command.toLowerCase(Locale.ROOT));
        save();
    }

    public static void revoke(UUID uuid, String command) {
        Set<String> commands = PERMISSIONS.get(uuid);
        if (commands != null) {
            commands.remove(command.toLowerCase(Locale.ROOT));
            if (commands.isEmpty()) {
                PERMISSIONS.remove(uuid);
            }
            save();
        }
    }

    public static Set<String> list(UUID uuid) {
        Set<String> commands = PERMISSIONS.get(uuid);
        return commands == null ? Collections.emptySet() : new HashSet<>(commands);
    }
}