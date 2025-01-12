package me.alpestrine.c.reward.screen.screens;

import me.alpestrine.c.reward.screen.button.ItemBuilder;
import me.alpestrine.c.reward.screen.screens.reward.DailyScreen;
import me.alpestrine.c.reward.screen.screens.reward.PlaytimeScreen;
import net.minecraft.server.network.ServerPlayerEntity;

public class SelectionScreen extends AbstractACScreen {

    @Override
    public void addButtons(ServerPlayerEntity viewer) {
        setButton(11, ItemBuilder.start(DailyScreen.dailyItem).name("Daily rewards").button(event -> event.player.openHandledScreen(new DailyScreen())));
        setButton(15, ItemBuilder.start(PlaytimeScreen.playtimeItem).name("Playtime rewards").button(event -> event.player.openHandledScreen(new PlaytimeScreen())));
    }

    @Override
    public String getName() {
        return "Selection";
    }

    @Override
    public int getPage() {
        return 1;
    }
}
