package me.alpestrine.c.reward.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.alpestrine.c.reward.config.objects.JsonPlayerData;

import java.util.*;

public class PlayerDataHandler implements BasicConfigReader<UUID, JsonPlayerData> {
    private Map<UUID, JsonPlayerData> stacks = Collections.synchronizedMap(new HashMap<>());

    @Override
    public JsonArray getJson() {
        JsonObject toWrite = new JsonObject();
        ArrayList<Map.Entry<UUID, JsonPlayerData>> copy = new ArrayList<>(stacks.entrySet());
        for (Map.Entry<UUID, JsonPlayerData> entry : copy) {
            toWrite.add(entry.getKey().toString(), entry.getValue().toJson());
        }
        JsonArray holder = new JsonArray();
        holder.add(toWrite);
        return holder;
    }

    @Override
    public Map<UUID, JsonPlayerData> getItems() {
        return Map.copyOf(stacks);
    }

    public JsonPlayerData getForUUID(UUID uuid) {
        return stacks.computeIfAbsent(uuid, u -> new JsonPlayerData());
    }

    @Override
    public void setJson(JsonArray value) {
        JsonObject realValue = value.size() == 1 ? value.get(0).getAsJsonObject() : new JsonObject();
        Map<UUID, JsonPlayerData> stacks = new HashMap<>();
        for (Map.Entry<String, JsonElement> je : realValue.entrySet()) {
            JsonPlayerData jdr = new JsonPlayerData().fromJson(je.getValue().getAsJsonObject());
            stacks.put(UUID.fromString(je.getKey()), jdr);
        }
        this.stacks = Collections.synchronizedMap(stacks);
    }

    @Override
    public String getName() {
        return "playerdata";
    }
}
