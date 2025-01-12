package me.alpestrine.c.reward.mixins;

import me.alpestrine.c.reward.screen.CustomScreenHandler;
import me.alpestrine.c.reward.screen.screens.SelectionScreen;
import me.alpestrine.c.reward.server.MainServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class ServerInteractMixin {

    @Inject(method = "interact", at = @At("HEAD"))
    private void hookInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        Entity entity = (Entity) (Object) this;
        if (player instanceof ServerPlayerEntity spe && !(spe.currentScreenHandler instanceof CustomScreenHandler) && MainServer.screenEntities.contains(entity.getUuid())) {
            spe.openHandledScreen(new SelectionScreen());
            spe.swingHand(hand, true);
        }
    }

}
