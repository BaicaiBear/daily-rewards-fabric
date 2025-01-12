package me.alpestrine.c.reward.screen.screens.reward;

import me.alpestrine.c.reward.config.DailyConfigHandler;
import me.alpestrine.c.reward.config.objects.JsonDailyReward;
import me.alpestrine.c.reward.config.objects.JsonPlayerData;
import me.alpestrine.c.reward.screen.button.ItemBuilder;
import me.alpestrine.c.reward.server.MainServer;
import me.alpestrine.c.reward.util.IMath;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Map;

public class DailyScreen extends AbstractRewardScreen {
    public static final Item dailyItem = Items.BELL;

    public static final DailyConfigHandler dailyHandler = new DailyConfigHandler();

    @Override
    public void addButtons(ServerPlayerEntity viewer) {
        JsonPlayerData data = dataHandler.getForUUID(viewer.getUuid());
        int currentStreak = data.currentStreak;
        double d = 1D + (double) (System.currentTimeMillis() - (data.lastRewardTime + MainServer.millisecondsInDay)) / (MainServer.millisecondsInDay);
        setButton(4, ItemBuilder.start(dailyItem).name(String.format("Current daily streak: %s day".concat(currentStreak + d > 1 ? "s" : ""), Math.max(IMath.round(currentStreak + d, 3), 1))).button());

        int currentSlot = minSlot - 1;
        int currentPage = 1;
        ArrayList<Map.Entry<Integer, JsonDailyReward>> rewards = new ArrayList<>(getRewards().entrySet());
        rewards.sort(Map.Entry.comparingByKey());
        for (Map.Entry<Integer, JsonDailyReward> entry : rewards) {
            JsonDailyReward jpr = entry.getValue();

            int reqTrek = entry.getKey();

            boolean isClaimed = data.claimedDaily.contains(jpr.getId());
            boolean isClaimable = currentStreak >= reqTrek;

            int sl;
            if (currentSlot >= maxSlot) {
                currentSlot = minSlot;
                currentPage++;
                sl = currentSlot;
            } else {
                sl = (currentSlot += 1);
            }

            setButton(currentPage, sl, ItemBuilder.start(getClaimItem(isClaimed, isClaimable))
                    .name(String.format("Day %s", reqTrek))
                    .tooltip(getToolTip(jpr.getRewardItems()))
                    .button(event -> onClick(isClaimed, isClaimable, event.player, data, jpr, Type.Daily)));
        }
    }

    @Override
    public String getName() {
        return "Daily";
    }

    @Override
    protected Map<Integer, JsonDailyReward> getRewards() {
        return dailyHandler.getItems();
    }
}
