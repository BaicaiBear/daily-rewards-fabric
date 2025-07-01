package me.alpestrine.c.reward.server;

import me.alpestrine.c.reward.MainMod;
import me.alpestrine.c.reward.config.GlobalConfigHandler;
import me.alpestrine.c.reward.config.objects.JsonPlayerData;
import me.alpestrine.c.reward.screen.CustomScreenHandler;
import me.alpestrine.c.reward.screen.button.InventoryEvent;
import me.alpestrine.c.reward.screen.screens.AbstractACScreen;
import me.alpestrine.c.reward.screen.screens.SelectionScreen;
import me.alpestrine.c.reward.server.TickExecutor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class MainServer {
    public static final Executor configHandlerThread = Executors.newSingleThreadExecutor();
    public static final GlobalConfigHandler globalConfigHandler = new GlobalConfigHandler();
    public static int millisecondsInDay;
    public static int ticksPerUpdate;
    public static HashSet<UUID> screenEntities = new HashSet<>();
    private static int lastSave;
    public static MinecraftServer server;
    public static void onInit(Object object) {
        server = (MinecraftServer) object;
        SelectionScreen.dailyHandler.onInit();
        SelectionScreen.playtimeHandler.onInit();
        SelectionScreen.dataHandler.onInit();
        globalConfigHandler.onInit();
    }

    public static int execute(Runnable task) {
        configHandlerThread.execute(task);
        return 0;
    }

    public static void handleClick(ScreenHandler sh, int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        try {
            if (!(player instanceof ServerPlayerEntity spe)) {
                return;
            }
            if (sh instanceof CustomScreenHandler) {
                ci.cancel();
            }
            Inventory inventory = slotIndex == ScreenHandler.EMPTY_SPACE_SLOT_INDEX ? player.getInventory() : sh.slots.get(slotIndex).inventory;
            InventoryEvent ie = new InventoryEvent(slotIndex, button, actionType, inventory, spe);
            if (inventory instanceof AbstractACScreen s) {
                s.onClick(ie);
            }
        } catch (Exception e) {
            System.err.println("Error handling click mixin");
            e.printStackTrace(System.err);
        }
    }

    public static void onTick() {
        int tect = TickExecutor.currentTick.get() + 1;
        TickExecutor.tickTasks.getOrDefault(tect, new ArrayList<>()).forEach(Runnable::run);
        TickExecutor.currentTick.set(tect);
        if (tect >= lastSave + ticksPerUpdate) {
            boolean toUpdate = false;
            for (ServerPlayerEntity spe : MainServer.server.getPlayerManager().getPlayerList()) {
                JsonPlayerData join = SelectionScreen.dataHandler.getForUUID(spe.getUuid());
                if (!Instant.ofEpochMilli(join.lastJoinDayTime)
                                .atZone(ZoneId.of("Asia/Shanghai"))
                                .toLocalDate()
                                .equals(ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toLocalDate())) {
                    join.hasJointToday = false;
                    join.lastJoinDayTime = System.currentTimeMillis();
                }
                join.playtimeSeconds += ticksPerUpdate / 20;
                join.lastJoin = System.currentTimeMillis();
                MainMod.refreshIfRewardScreen(spe);
                toUpdate = true;
            }

            lastSave = tect;
            if (toUpdate) {
                configHandlerThread.execute(SelectionScreen.dataHandler::writeCurrentValue);
            }
        }
    }
}
