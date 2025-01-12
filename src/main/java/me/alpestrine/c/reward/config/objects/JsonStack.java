package me.alpestrine.c.reward.config.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.alpestrine.c.reward.server.MainServer;
import me.alpestrine.c.reward.util.RomanNumber;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonStack implements JSONAble {

    private Item item;
    private String name;
    private int amount;
    private int damage;
    private Map<RegistryEntry<Enchantment>, Integer> enchantments;

    public JsonStack() {}

    public JsonStack(Item item, String name, int amount, int damage, Map<RegistryEntry<Enchantment>, Integer> enchantments) {
        this();
        this.item = item;
        this.name = name;
        this.amount = amount;
        this.damage = damage;
        this.enchantments = enchantments;
        fromJson(toJson()); // cleanse
    }

    public ItemStack toItemStack() {
        ItemStack is = new ItemStack(item, amount);
        is.setDamage(damage);
        if (name != null) {
            is.set(DataComponentTypes.ITEM_NAME, Text.of(name));
        }
        if (enchantments != null) {
            for (Map.Entry<RegistryEntry<Enchantment>, Integer> entry : enchantments.entrySet()) {
                is.addEnchantment(entry.getKey(), entry.getValue());
            }
        }
        return is;
    }

    public List<String> toToolTip() {
        ArrayList<String> tt = new ArrayList<>();
        tt.add(String.format("x%s %s", amount, item.getName().getString()).concat(name != null ? String.format(" \"%s\"", name) : ""));
        if (enchantments != null) {
            for (Map.Entry<RegistryEntry<Enchantment>, Integer> entry : enchantments.entrySet()) {
                String enchStr = entry.getKey().value().description().getString();
                int level = entry.getValue();
                String rom = "";
                if (level != 1) {
                    rom = RomanNumber.toRoman(level);
                }
                tt.add(enchStr.concat(" ").concat(rom));
            }
        }
        return tt;
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        String itemId = Registries.ITEM.getId(item).toString();
        obj.addProperty("item", itemId);
        if (amount != 1) {
            obj.addProperty("amount", amount);
        }
        if (damage != 0) {
            obj.addProperty("damage", damage);
        }
        if (name != null) {
            obj.addProperty("name", name);
        }
        if (enchantments != null) {
            JsonObject enchants = new JsonObject();
            for (Map.Entry<RegistryEntry<Enchantment>, Integer> entry : enchantments.entrySet()) {
                RegistryEntry<Enchantment> ench = entry.getKey();
                enchants.addProperty(ench.getKey().orElseThrow(() -> new RuntimeException("Failed to get enchantment key for: " + ench.value().toString())).getValue().toString(), entry.getValue());
            }
            obj.add("enchants", enchants);
        }
        return obj;
    }

    @Override @SuppressWarnings("unchecked cast")
    public JsonStack fromJson(JsonObject obj) {
        Identifier id = Identifier.of(obj.get("item").getAsString());
        this.item = Registries.ITEM.getOrEmpty(id).orElseThrow(() -> new RuntimeException("Failed to get item for id: " + id));
        this.amount = obj.has("amount") ? obj.get("amount").getAsInt() : 1;
        this.damage = obj.has("damage") ? obj.get("damage").getAsInt() : 0;
        this.name = obj.has("name") ? obj.get("name").getAsString() : null;
        Map<RegistryEntry<Enchantment>, Integer> enchantments = null;
        MinecraftServer server = MainServer.server;
        if (obj.get("enchants") instanceof JsonObject enchants && server != null) {// loaded cfg before server was ready, we will load again after it is
            enchantments = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : enchants.entrySet()) {
                Identifier enchId = Identifier.of(entry.getKey());
                RegistryEntry.Reference<Enchantment> enchEntry = server.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(enchId).orElseThrow(() -> new RuntimeException("Failed to get enchantment for id: " + enchId));
                enchantments.put(enchEntry, Math.max(entry.getValue().getAsInt(), 1));
            }
        }
        this.enchantments = enchantments;
        return this;
    }

}
