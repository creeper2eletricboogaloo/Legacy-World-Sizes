package wily.legacy_world_sizes.mixin.base.client.compat.legacy4j;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.legacy.client.screen.LegacyConfigWidgets;
import wily.legacy.client.screen.Panel;
import wily.legacy.client.screen.PanelVListScreen;
import wily.legacy.client.screen.WorldMoreOptionsScreen;
import wily.legacy_world_sizes.config.LWSWorldOptions;

@Mixin(WorldMoreOptionsScreen.class)
public class WorldMoreOptionsScreenMixin extends PanelVListScreen {
    @Unique
    private boolean legacyWorldSizes$addedOptions;

    public WorldMoreOptionsScreenMixin(Panel.Constructor<PanelVListScreen> panelConstructor, Component component) {
        super(panelConstructor, component);
    }

    @Inject(method = "renderableVListInit", at = @At("HEAD"), remap = false)
    private void init(CallbackInfo ci) {
        if (!(parent instanceof CreateWorldScreen) || legacyWorldSizes$addedOptions) return;
        legacyWorldSizes$addedOptions = true;
        int index = Math.min(3, renderableVList.renderables.size());
        if (isLegacySettingsMenusEnabled()) {
            if (renderableVList.renderables.size() > 5) {
                renderableVList.renderables.remove(5);
            }
            renderableVList.renderables.add(3, LegacyConfigWidgets.createWidget(LWSWorldOptions.balancedSeed, 0, 0, 200, LWSWorldOptions.balancedSeed::setDefault));
            renderableVList.renderables.add(6, LegacyConfigWidgets.createWidget(LWSWorldOptions.legacyBiomeScale, 0, 0, 200, LWSWorldOptions.legacyBiomeScale::setDefault));
            renderableVList.renderables.add(7, LegacyConfigWidgets.createWidget(LWSWorldOptions.legacyWorldSize, 0, 0, 200, LWSWorldOptions.legacyWorldSize::setDefault));
            return;
        }
        renderableVList.renderables.add(index++, LegacyConfigWidgets.createWidget(LWSWorldOptions.balancedSeed, 0, 0, 200, LWSWorldOptions.balancedSeed::setDefault));
        renderableVList.renderables.add(index++, LegacyConfigWidgets.createWidget(LWSWorldOptions.legacyBiomeScale, 0, 0, 200, LWSWorldOptions.legacyBiomeScale::setDefault));
        renderableVList.renderables.add(index, LegacyConfigWidgets.createWidget(LWSWorldOptions.legacyWorldSize, 0, 0, 200, LWSWorldOptions.legacyWorldSize::setDefault));
    }

    @Unique
    private boolean isLegacySettingsMenusEnabled() {
        try {
            Object option = Class.forName("wily.legacy.client.LegacyOptions").getField("legacySettingsMenus").get(null);
            return option instanceof java.util.function.Supplier<?> supplier && Boolean.TRUE.equals(supplier.get());
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }
}
