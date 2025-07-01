package net.rewardz.network;

import me.alpestrine.c.reward.screen.screens.SelectionScreen;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class RewardsServerPacket {

    public static void init() {
        PayloadTypeRegistry.playC2S().register(RewardScreenPacket.PACKET_ID, RewardScreenPacket.PACKET_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(RewardScreenPacket.PACKET_ID, (payload, context) -> context.player().server.execute(() -> context.player().openHandledScreen(new SelectionScreen())));
    }
}