package net.rewardz.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RenderInit {
    public static final boolean isPatchouliButtonLoaded = FabricLoader.getInstance().isModLoaded("patchoulibutton");
    public static final Identifier REWARD_ICONS = Identifier.of("rewards", "textures/gui/rewards.png");
}
