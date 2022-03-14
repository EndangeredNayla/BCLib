package ru.bclib.blocks;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.List;

public abstract class WallMushroomBlock extends BaseWallPlantBlock {
	public WallMushroomBlock(int light) {
		this(
			FabricBlockSettings
				.of(Material.PLANT)
				//TODO: 1.18.2 Check if this is still ok
					//..breakByHand(true)
				.luminance(light)
				.destroyTime(0.2F)
				.sound(SoundType.GRASS)
				.sound(SoundType.WOOD)
				.noCollission()
		);
	}
	
	public WallMushroomBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		return Lists.newArrayList(new ItemStack(this));
	}
	
	@Override
	public boolean isSupport(LevelReader world, BlockPos pos, BlockState blockState, Direction direction) {
		return blockState.getMaterial().isSolid() && blockState.isFaceSturdy(world, pos, direction);
	}
}
