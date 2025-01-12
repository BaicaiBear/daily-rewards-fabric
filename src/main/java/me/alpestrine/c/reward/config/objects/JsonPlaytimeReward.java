package me.alpestrine.c.reward.config.objects;

import com.google.gson.JsonObject;
import me.alpestrine.c.reward.util.IMath;

import java.util.ArrayList;
import java.util.Arrays;

public class JsonPlaytimeReward extends JsonBaseReward<Double> {

    @Override
    public Double getTime() {
        return IMath.round(timeThing, 4);
    }

    @Override
    public void writeTimeThing(JsonObject object) {
        object.addProperty("hours", getTime());
    }

    @Override
    public Double readTimeThing(JsonObject object) {
        return object.get("hours").getAsDouble();
    }

}
