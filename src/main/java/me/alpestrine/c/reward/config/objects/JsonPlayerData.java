package me.alpestrine.c.reward.config.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

public class JsonPlayerData implements JSONAble {
    public int playtimeSeconds;
    public int currentStreak;
    public long lastJoin;
    public long lastRewardTime;
    public TreeSet<String> claimedDaily;
    public TreeSet<String> claimedPlaytime;

    public JsonPlayerData() {
        playtimeSeconds = 0;
        currentStreak = 0;
        lastJoin = 0;
        lastRewardTime = 0;
        claimedDaily = new TreeSet<>(CharSequence::compare);
        claimedPlaytime = new TreeSet<>(CharSequence::compare);
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("playtimeSeconds", playtimeSeconds);
        obj.addProperty("currentStreak", currentStreak);
        obj.addProperty("lastJoin", lastJoin);
        obj.addProperty("lastRewardTime", lastRewardTime);
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
        currentStreak = object.get("currentStreak").getAsInt();
        lastJoin = object.get("lastJoin").getAsLong();
        lastRewardTime = object.get("lastRewardTime").getAsLong();
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
