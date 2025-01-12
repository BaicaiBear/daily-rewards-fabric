package me.alpestrine.c.reward.config;

import com.google.gson.JsonArray;
import me.alpestrine.c.reward.config.objects.JsonGlobal;
import me.alpestrine.c.reward.server.MainServer;

import java.util.Map;

public class GlobalConfigHandler implements BasicConfigReader<Void, Void> {
    private JsonArray value = new JsonArray();

    @Override
    public JsonArray getJson() {
        return value.deepCopy();
    }

    @Override
    public Map<Void, Void> getItems() {
        return Map.of();
    }

    @Override
    public String getDefault() {
        /*JsonArray ja = new JsonArray();
        ja.add(new JsonGlobal().toJson());
        return gson.toJson(ja);*/

        return readStringFromAsset("config/global.json");
    }

    @Override
    public void setJson(JsonArray ja) {
        this.value = ja;
        JsonGlobal global;
        if (ja.isEmpty()) {
            global = new JsonGlobal();
        } else {
            global = new JsonGlobal().fromJson(ja.get(0).getAsJsonObject());
        }
        MainServer.millisecondsInDay = global.getMillisecondsInDay();
        MainServer.ticksPerUpdate = global.getTicksPerUpdate();
        MainServer.screenEntities = global.getScreenEntity();
    }

    @Override
    public String getName() {
        return "global";
    }
}
