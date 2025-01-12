package me.alpestrine.c.reward.config.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.alpestrine.c.reward.util.IMath;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class JsonBaseReward<N extends Number> implements JSONAble {
    protected N timeThing;
    protected List<JsonStack> rewardItems;
    protected String id;

    public abstract N getTime();

    public List<JsonStack> getRewardItems() {
        return rewardItems;
    }

    public String getId() {
        return id;
    }

    public abstract void writeTimeThing(JsonObject object);

    public abstract N readTimeThing(JsonObject object);

    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        writeTimeThing(obj);
        obj.addProperty("id", id != null ? id : String.valueOf(getTime()));
        JsonArray rewards = new JsonArray();
        for (JsonStack stack : rewardItems) {
            rewards.add(stack.toJson());
        }
        obj.add("items", rewards);
        return obj;
    }

    @SuppressWarnings("unchecked cast") @Override
    public <T extends JSONAble> T fromJson(JsonObject object) {
        timeThing = readTimeThing(object);
        id = object.has("id") ? object.get("id").getAsString() : String.valueOf(getTime());
        JsonArray ja = object.getAsJsonArray("items");
        ArrayList<JsonStack> stacks = new ArrayList<>();
        for (JsonElement je : ja) {
            stacks.add(new JsonStack().fromJson(je.getAsJsonObject()));
        }
        rewardItems = List.copyOf(stacks);
        return (T) this;
    }


    public <T> T addJsonStack(Class<T> clazz, JsonStack... stack) {
        if (rewardItems == null) {
            rewardItems = new ArrayList<>();
        }
        rewardItems.addAll(Arrays.asList(stack));
        return clazz.cast(this);
    }

    public <T> T setTimeThing(Class<T> clazz, N timeThing) {
        this.timeThing = timeThing;
        return clazz.cast(this);
    }
}
