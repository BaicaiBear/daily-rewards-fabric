package me.alpestrine.c.reward.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.rewardz.init.ConfigInit;
import net.rewardz.init.RenderInit;
import net.rewardz.network.RewardScreenPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class ClientInventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {
    public ClientInventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void mouseClickedMixin(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> info) {
        if (this.client != null && this.focusedSlot == null
                && this.isPointWithinBounds(ConfigInit.CONFIG.posX + (RenderInit.isPatchouliButtonLoaded ? 23 : 0), ConfigInit.CONFIG.posY, 20, 18, (double) mouseX, (double) mouseY)) {
            ClientPlayNetworking.send(new RewardScreenPacket());
        }
    }

    @Inject(method = "drawBackground", at = @At("TAIL"))
    protected void drawBackgroundMixin(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo info) {
        int rewardButtonX = ConfigInit.CONFIG.posX + (RenderInit.isPatchouliButtonLoaded ? 23 : 0);

        if (this.isPointWithinBounds(rewardButtonX, ConfigInit.CONFIG.posY, 20, 18, (double) mouseX, (double) mouseY)) {
            context.drawTexture(RenderInit.REWARD_ICONS, this.x + rewardButtonX, this.y + ConfigInit.CONFIG.posY, 196, 42, 20, 18);
            renderCheckMark(context);
            context.drawTooltip(this.textRenderer, Text.literal("日常奖励"), mouseX, mouseY);
        } else {
            context.drawTexture(RenderInit.REWARD_ICONS, this.x + rewardButtonX, this.y + ConfigInit.CONFIG.posY, 176, 42, 20, 18);
            renderCheckMark(context);
        }
    }

    private void renderCheckMark(DrawContext context) {
            context.drawTexture(RenderInit.REWARD_ICONS, this.x + 2 + ConfigInit.CONFIG.posX + (RenderInit.isPatchouliButtonLoaded ? 23 : 0), this.y + 2 + ConfigInit.CONFIG.posY, 192, 0, 16, 14);
    }

}
