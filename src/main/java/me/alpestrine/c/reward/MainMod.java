package me.alpestrine.c.reward;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.alpestrine.c.reward.screen.CustomScreenHandler;
import me.alpestrine.c.reward.screen.screens.AbstractACScreen;
import me.alpestrine.c.reward.screen.screens.SelectionScreen;
import me.alpestrine.c.reward.server.MainServer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.rewardz.network.RewardsServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class MainMod implements ModInitializer {
	public static final String ID = "rewards";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Rewards!");
		CommandRegistrationCallback.EVENT.register(this::register);
		RewardsServerPacket.init();
	}

	private void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
		for (ConfigType ct : ConfigType.values()) {
			dispatcher.register(CommandManager.literal(String.format("rewards-reload-%s-config", ct.name().toLowerCase()))
					.executes(context -> MainServer.execute(() -> execCommand(context, environment, ct))));
		}
	}

	private void execCommand(CommandContext<ServerCommandSource> context, CommandManager.RegistrationEnvironment environment, ConfigType type) {
		ServerCommandSource scs = context.getSource();
		if (scs.hasPermissionLevel(2) || environment.integrated) {
			try {
				scs.sendMessage(Text.of(String.format((switch (type) {
					case Daily -> SelectionScreen.dailyHandler;
					case Playtime -> SelectionScreen.playtimeHandler;
					case PlayerData -> SelectionScreen.dataHandler;
					case Global -> MainServer.globalConfigHandler;
				}).reload() ? "Successfully reloaded %s config!" : "Config file for %s didn't exist or was empty, default config was saved to disk.", type.name())));
				scs.getServer().getPlayerManager().getPlayerList().forEach(MainMod::refreshIfRewardScreen);
			} catch (Exception e) {
				scs.sendMessage(Text.of(String.format("Failed to reload %s config, check logs for error.", type.name())));
				e.printStackTrace(System.err);
			}
		} else {
			scs.sendError(Text.of("No permission!"));
		}
	}

	public static void refreshIfRewardScreen(ServerPlayerEntity spe) {
		if (spe.currentScreenHandler instanceof CustomScreenHandler csh && csh.getInventory() instanceof AbstractACScreen aacs) {
			aacs.refresh(spe);
		}
	}

	public enum ConfigType {Daily, Playtime, Global, PlayerData}
}
