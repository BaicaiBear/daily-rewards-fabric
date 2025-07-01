package me.alpestrine.c.reward.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import me.alpestrine.c.reward.config.objects.JsonPlaytimeReward;

import java.util.HashMap;
import java.util.Map;

public class PlaytimeConfigHandler implements BasicConfigReader<Double, JsonPlaytimeReward> {
    private JsonArray value = new JsonArray();
    private Map<Double, JsonPlaytimeReward> stacks = new HashMap<>();

    @Override
    public Map<Double, JsonPlaytimeReward> getItems() {
        return Map.copyOf(stacks);
    }

    @Override
    public JsonArray getJson() {
        return value.deepCopy();
    }

    @Override
    public String getDefault() {
        return readStringFromAsset("config/playtime.json");
    }

    @Override
    public void setJson(JsonArray value) {
        this.value = value;

        Map<Double, JsonPlaytimeReward> stacks = new HashMap<>();
        if (!value.isEmpty()) {
            JsonElement firstElement = value.get(0);
            JsonPlaytimeReward jdr = new JsonPlaytimeReward().fromJson(firstElement.getAsJsonObject());
            stacks.put(jdr.getTime(), jdr);
        }
        this.stacks = stacks;
    }

    @Override
    public String getName() {
        return "playtime";
    }
}