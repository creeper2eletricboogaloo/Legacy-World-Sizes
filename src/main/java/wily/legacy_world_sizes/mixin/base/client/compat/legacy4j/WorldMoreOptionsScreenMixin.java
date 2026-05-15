package wily.legacy_world_sizes.mixin.base.client.compat.legacy4j;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.client.FactoryConfigWidgets;
import wily.factoryapi.base.client.SimpleLayoutRenderable;
import wily.factoryapi.base.config.FactoryConfig;
import wily.factoryapi.base.config.FactoryConfigControl;
import wily.legacy.client.CommonColor;
import wily.legacy.client.LegacyOptions;
import wily.legacy.client.screen.*;
import wily.legacy_world_sizes.config.LWSWorldOptions;

import java.util.function.Function;

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
            addLabeledConfigSlider(6, LWSWorldOptions.legacyBiomeScale);
            addLabeledConfigSlider(8, LWSWorldOptions.legacyWorldSize);
            return;
        }
        renderableVList.renderables.add(index++, LegacyConfigWidgets.createWidget(LWSWorldOptions.balancedSeed, 0, 0, 200, LWSWorldOptions.balancedSeed::setDefault));
        renderableVList.renderables.add(index++, SimpleLayoutRenderable.create(0, 9, r -> ((GuiGraphicsExtractor, i, j, f) -> {})));
        addLabeledConfigSlider(index++, LWSWorldOptions.legacyBiomeScale);
        addLabeledConfigSlider(index + 1, LWSWorldOptions.legacyWorldSize);
    }

    @Unique
    private <T> void addLabeledConfigSlider(int index, FactoryConfig<T> config) {
        FactoryConfigControl.FromInt<T> c = (FactoryConfigControl.FromInt<T>) config.control();
        Function<T, Tooltip> tooltipFunction = (v) -> FactoryConfigWidgets.getCachedTooltip(config.getDisplay().tooltip().apply(v));
        renderableVList.renderables.add(index, LegacySliderButton.createFromInt(0, 0, 200, 16, (s) -> config.getDisplay().valueToComponent().apply(s.getObjectValue()), (s) -> tooltipFunction.apply(s.getObjectValue()), config.get(), c.valueGetter(), c.valueSetter(), c.valuesSize(), (s) -> FactoryConfig.saveOptionAndConsume(config, s.getObjectValue(), config::setDefault), config));
        renderableVList.renderables.add(index, new RenderableVList.LayoutText(config.getDisplay().name(), CommonColor.GRAY_TEXT, () -> LegacyOptions.getUIMode().isSD() ? 9 : 13));
    }

    @Unique
    private boolean isLegacySettingsMenusEnabled() {
       return LegacyOptions.legacySettingsMenus.get();
    }
}
