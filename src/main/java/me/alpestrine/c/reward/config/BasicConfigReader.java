package me.alpestrine.c.reward.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import me.alpestrine.c.reward.config.objects.JsonStack;
import me.alpestrine.c.reward.server.MainServer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public interface BasicConfigReader<N, T> {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Object o = new Object();

    default boolean reload() throws IOException {
        File file = FabricLoader.getInstance().getConfigDir().resolve("rewards").resolve(getName().concat(".json")).toFile();
        if (!file.exists()) {
            setJson(new JsonArray());
            String str = writeCurrentValue();
            if (str != null) {
                throw new IOException(str);
            }
        }
        setJson(gson.fromJson(Files.readString(file.toPath()), JsonArray.class));
        boolean isEmpty = getJson().isEmpty();
        if (isEmpty) {
            writeValue(getDefault());
            setJson(gson.fromJson(Files.readString(file.toPath()), JsonArray.class));
        }
        return !isEmpty;
    }

    default String writeCurrentValue() {
        return writeValue(gson.toJson(getJson()));
    }

    default String writeValue(String value) {
        synchronized (o) {
            File file = FabricLoader.getInstance().getConfigDir().resolve("rewards").resolve(getName().concat(".json")).toFile();
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
                Files.writeString(file.toPath(), value);
                return null;
            } catch (IOException e) {
                return e.getLocalizedMessage();
            }
        }
    }

    default String readStringFromAsset(String asset) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(asset)) {
            return new String(Objects.requireNonNull(is).readAllBytes());
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    JsonArray getJson();

    default String getDefault() {
        return gson.toJson(new JsonArray());
    }

    Map<N, T> getItems();

    void setJson(JsonArray value);

    String getName();

    default void onInit() {
        try {
            if (!reload()) {
                System.err.println("File didn't exist for: " + getName());
            }
        } catch (Exception e) {
            System.err.println("Failed to load config on init for: " + getName());
            e.printStackTrace(System.err);
        }
    }

    default JsonStack getRandomItem() {
        DefaultedRegistry<Item> items = Registries.ITEM;
        MinecraftServer server = MainServer.server;
        Map<RegistryEntry<Enchantment>, Integer> map = null;
        if (server != null) {
            Registry<Enchantment> enchantmentRegistry = server.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
            for (int enches = 0; enches < ThreadLocalRandom.current().nextInt(0, 4); enches++) {
                if (map == null) {
                    map = new HashMap<>();
                }
                map.put(
                        enchantmentRegistry.getEntry(enchantmentRegistry.get(ThreadLocalRandom.current().nextInt(0, enchantmentRegistry.size()))),
                        ThreadLocalRandom.current().nextInt(0, 10)
                );
            }
        }
        int index = ThreadLocalRandom.current().nextInt(0, items.size());
        return new JsonStack(items.get(index), "Hi".concat(String.valueOf(index)), ThreadLocalRandom.current().nextInt(1, 5), ThreadLocalRandom.current().nextInt(0, 5), map);
    }

    default double secondsToHours(double seconds) {
        return seconds / 60 / 60;
    }
}
