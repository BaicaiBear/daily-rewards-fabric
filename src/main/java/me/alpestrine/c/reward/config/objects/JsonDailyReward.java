package me.alpestrine.c.reward.config.objects;

import com.google.gson.JsonObject;
import me.alpestrine.c.reward.util.IMath;

public class JsonDailyReward extends JsonBaseReward<Integer> {

    @Override
    public Integer getTime() {
        return IMath.round(timeThing, 4);
    }

    @Override
    public void writeTimeThing(JsonObject object) {
        object.addProperty("day", getTime());
    }

    @Override
    public Integer readTimeThing(JsonObject object) {
        return object.get("day").getAsInt();
    }
}
