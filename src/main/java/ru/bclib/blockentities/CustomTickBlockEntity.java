package ru.bclib.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface CustomTickBlockEntity {
    void customTick(Level level, BlockPos pos, BlockState state);
}
