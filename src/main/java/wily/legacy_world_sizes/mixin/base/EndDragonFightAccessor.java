package wily.legacy_world_sizes.mixin.base;

import net.minecraft.world.level.dimension.end.EnderDragonFight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(EnderDragonFight.class)
public interface EndDragonFightAccessor {
    @Accessor
	List<Integer> getGateways();
}
