package wily.legacy_world_sizes.mixin.base;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import wily.legacy_world_sizes.config.LWSWorldOptions;
import wily.legacy_world_sizes.util.LevelHolder;

@Mixin(ChunkGeneratorStructureState.class)
public class ChunkGeneratorStructureStateMixin implements LevelHolder {
    @Unique
    Level lws$level;

    @Override
    public void setLevel(Level level) {
        this.lws$level = level;
        LWSWorldOptions.setupValidPlacements(getLevel(), self());
    }

    @Override
    public Level getLevel() {
        return lws$level;
    }

    @Unique
    private ChunkGeneratorStructureState self() {
        return (ChunkGeneratorStructureState) (Object) this;
    }
}
