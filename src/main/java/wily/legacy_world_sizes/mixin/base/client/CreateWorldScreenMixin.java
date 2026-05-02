package wily.legacy_world_sizes.mixin.base.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.legacy_world_sizes.config.LWSWorldOptions;
import wily.legacy_world_sizes.util.LWSComponents;
import wily.legacy_world_sizes.util.LegacyChunkBounds;
import wily.legacy_world_sizes.util.LegacyLevelLimit;

import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {
    @Unique
    boolean foundBalancedSeed = false;

    protected CreateWorldScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        LWSWorldOptions.restoreChangedDefaults();
    }

    @Shadow @Final private WorldCreationUiState uiState;

    @Shadow protected abstract void onCreate();

    @Inject(method = "onCreate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;createLevelSettings(Z)Lnet/minecraft/world/level/LevelSettings;"), cancellable = true)
    private void onCreate(CallbackInfo ci, @Local LayeredRegistryAccess<RegistryLayer> registryAccess) {
        if (!foundBalancedSeed && uiState.getSeed().isBlank() && LWSWorldOptions.balancedSeed.get()) {
            LWSWorldOptions.setupLegacyWorldSize(registryAccess.compositeAccess());
            LegacyLevelLimit limit = LWSWorldOptions.legacyLevelLimits.get().get(Level.OVERWORLD);
            if (limit != null) {
                ProgressScreen screen = new ProgressScreen(false) {
                    int lastProgress = 0;
                    @Override
                    public void tick() {
                        super.tick();
                        progressStagePercentage(lastProgress = lastProgress % 100 + 5);
                    }
                };
                screen.progressStart(LWSComponents.INITIALIZING_SERVER);
                screen.progressStage(LWSComponents.FINDING_SEED);
                minecraft.setScreen(screen);
                LegacyChunkBounds bounds = limit.bounds().get(0);
                ci.cancel();
                CompletableFuture.runAsync(() -> {
                    uiState.setSettings(uiState.getSettings().withOptions(options -> options.withSeed(OptionalLong.of(bounds.findBalancedSeed(registryAccess.compositeAccess(), 100)))));
                    minecraft.execute(() -> {
                        //Making sure if any problem happens with the world creation, it won't be stuck in the progress screen
                        minecraft.setScreen(CreateWorldScreenMixin.this);
                        foundBalancedSeed = true;
                        onCreate();
                    });
                }, Util.backgroundExecutor());
            }
        }
    }
}
