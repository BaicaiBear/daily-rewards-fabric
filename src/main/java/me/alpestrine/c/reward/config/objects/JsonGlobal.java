package me.alpestrine.c.reward.config.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class JsonGlobal implements JSONAble {
    private int ticksPerUpdate;
    private int millisecondsInDay;
    private HashSet<UUID> screenEntities;
    public JsonGlobal() {
        millisecondsInDay = 1000 * 60 * 60 * 24;
        ticksPerUpdate = 100;
        screenEntities = new HashSet<>();
    }

    public int getTicksPerUpdate() {
        return ticksPerUpdate;
    }

    public int getMillisecondsInDay() {
        return millisecondsInDay;
    }

    public HashSet<UUID> getScreenEntity() {
        return screenEntities;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jo = new JsonObject();

        jo.addProperty("millisecondsInDay", millisecondsInDay);
        jo.addProperty("updateTickTime", ticksPerUpdate);
        JsonArray ja = new JsonArray();
        for (UUID uuid : screenEntities) {
            ja.add(uuid.toString());
        }
        jo.add("screenentity", ja);

        return jo;
    }

    @Override @SuppressWarnings("unchecked cast")
    public <T extends JSONAble> T fromJson(JsonObject jo) {

        millisecondsInDay = jo.get("millisecondsInDay").getAsInt();
        ticksPerUpdate = jo.get("updateTickTime").getAsInt();
        screenEntities = new HashSet<>();
        JsonArray ja = jo.getAsJsonArray("screenentity");
        for (JsonElement je : ja) {
            screenEntities.add(UUID.fromString(je.getAsString()));
        }

        return (T) this;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        JsonGlobal that = (JsonGlobal) object;
        return ticksPerUpdate == that.ticksPerUpdate && millisecondsInDay == that.millisecondsInDay && Objects.equals(screenEntities, that.screenEntities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticksPerUpdate, millisecondsInDay, screenEntities);
    }
}
