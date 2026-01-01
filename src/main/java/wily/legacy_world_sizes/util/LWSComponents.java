package wily.legacy_world_sizes.util;

import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class LWSComponents {
    public static final Component OPTIONS_TITLE = Component.translatable("legacy_world_sizes.options");

    //Components shared with Legacy4J
    public static final Component INITIALIZING_SERVER = Component.translatable("legacy.connect.initializing");
    public static final Component FINDING_SEED = Component.translatable("legacy.finding_seed");

    public static Component optionName(String key){
        return Component.translatable("legacy_world_sizes.options."+key);
    }

    public static <T> Function<T, Component> staticTooltip(Component component) {
        return t -> component;
    }
}
