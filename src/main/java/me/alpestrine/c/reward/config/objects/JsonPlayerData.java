package me.alpestrine.c.reward.config.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.TreeSet;

public class JsonPlayerData implements JSONAble {
    public int playtimeSeconds;
    public boolean hasJointToday;
    public long lastJoin;
    public long lastJoinDayTime;
    public TreeSet<String> claimedDaily;
    public TreeSet<String> claimedPlaytime;

    public JsonPlayerData() {
        playtimeSeconds = 0;
        hasJointToday = false;
        lastJoin = 0;
        lastJoinDayTime = 0;
        claimedDaily = new TreeSet<>(CharSequence::compare);
        claimedPlaytime = new TreeSet<>(CharSequence::compare);
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("playtimeSeconds", playtimeSeconds);
        obj.addProperty("hasJointToday", hasJointToday);
        obj.addProperty("lastJoin", lastJoin);
        obj.addProperty("lastJoinDayTime", lastJoinDayTime);
        JsonArray dailyJa = new JsonArray();
        for (String str : claimedDaily) {
            dailyJa.add(str);
        }
        obj.add("claimedDaily", dailyJa);
        JsonArray playtimeJa = new JsonArray();
        for (String str : claimedPlaytime) {
            playtimeJa.add(str);
        }
        obj.add("claimedPlaytime", playtimeJa);
        return obj;
    }

    @SuppressWarnings("unchecked cast") @Override
    public JsonPlayerData fromJson(JsonObject object) {
        playtimeSeconds = object.get("playtimeSeconds").getAsInt();
        hasJointToday = object.get("hasJointToday").getAsBoolean();
        lastJoin = object.get("lastJoin").getAsLong();
        lastJoinDayTime = object.get("lastJoinDayTime").getAsLong();
        JsonArray dailyJa = object.getAsJsonArray("claimedDaily");
        claimedDaily = new TreeSet<>(CharSequence::compare);
        for (JsonElement je : dailyJa) {
            claimedDaily.add(je.getAsString());
        }
        JsonArray playtimeJa = object.getAsJsonArray("claimedPlaytime");
        claimedPlaytime = new TreeSet<>(CharSequence::compare);
        for (JsonElement je : playtimeJa) {
            claimedPlaytime.add(je.getAsString());
        }
        return this;
    }
}
