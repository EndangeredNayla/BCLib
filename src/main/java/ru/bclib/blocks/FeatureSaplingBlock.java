package ru.bclib.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import ru.bclib.client.models.BasePatterns;
import ru.bclib.client.models.ModelsHelper;
import ru.bclib.client.models.PatternsHelper;
import ru.bclib.client.render.BCLRenderLayer;
import ru.bclib.interfaces.BlockModelProvider;
import ru.bclib.interfaces.RenderLayerProvider;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

public class FeatureSaplingBlock extends SaplingBlock implements RenderLayerProvider, BlockModelProvider {
	private static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 14, 12);
	private final Function<BlockState, Feature<?>> feature;
	
	public FeatureSaplingBlock(Function<BlockState, Feature<?>> featureSupplier) {
		this(FabricBlockSettings.of(Material.PLANT)
						//TODO: 1.18.2 Check if this is still ok
						//.breakByHand(true)
		   .collidable(false)
		   .instabreak()
		   .sound(SoundType.GRASS)
		   .randomTicks(),
			featureSupplier
		);
	}
	
	public FeatureSaplingBlock(int light, Function<BlockState, Feature<?>> featureSupplier) {
		this(FabricBlockSettings.of(Material.PLANT)
						//TODO: 1.18.2 Check if this is still ok
						//.breakByHand(true)
		   .collidable(false)
		   .luminance(light)
		   .instabreak()
		   .sound(SoundType.GRASS)
		   .randomTicks(),
			featureSupplier
		);
	}
	
	public FeatureSaplingBlock(BlockBehaviour.Properties properties, Function<BlockState, Feature<?>> featureSupplier) {
		super(null, properties);
		this.feature = featureSupplier;
	}
	
	protected Feature<?> getFeature(BlockState state) {
		return feature.apply(state);
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		return Collections.singletonList(new ItemStack(this));
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
		if (!canSurvive(state, world, pos)) return Blocks.AIR.defaultBlockState();
		else return state;
	}
	
	@Override
	public boolean isBonemealSuccess(Level world, Random random, BlockPos pos, BlockState state) {
		return random.nextInt(16) == 0;
	}
	
	@Override
	public void advanceTree(ServerLevel world, BlockPos pos, BlockState blockState, Random random) {
		FeaturePlaceContext context = new FeaturePlaceContext(
			Optional.empty(),
			world,
			world.getChunkSource().getGenerator(),
			random,
			pos,
			null
		);
		getFeature(blockState).place(context);
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
		this.tick(state, world, pos, random);
	}
	
	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
		super.tick(state, world, pos, random);
		if (isBonemealSuccess(world, random, pos, state)) {
			performBonemeal(world, random, pos, state);
		}
	}
	
	@Override
	public BCLRenderLayer getRenderLayer() {
		return BCLRenderLayer.CUTOUT;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public BlockModel getItemModel(ResourceLocation resourceLocation) {
		return ModelsHelper.createBlockItem(resourceLocation);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public @Nullable BlockModel getBlockModel(ResourceLocation resourceLocation, BlockState blockState) {
		Optional<String> pattern = PatternsHelper.createJson(BasePatterns.BLOCK_CROSS, resourceLocation);
		return ModelsHelper.fromPattern(pattern);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
		return SHAPE;
	}
}
