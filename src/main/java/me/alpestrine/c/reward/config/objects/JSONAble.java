package me.alpestrine.c.reward.config.objects;

import com.google.gson.JsonObject;

public interface JSONAble {
    JsonObject toJson();

    <T extends JSONAble> T fromJson(JsonObject object);
}
