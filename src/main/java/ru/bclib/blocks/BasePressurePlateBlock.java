package ru.bclib.blocks;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;
import ru.bclib.client.models.BasePatterns;
import ru.bclib.client.models.BlockModelProvider;
import ru.bclib.client.models.ModelsHelper;
import ru.bclib.client.models.PatternsHelper;
import ru.bclib.util.BlocksHelper;

public class BasePressurePlateBlock extends PressurePlateBlock implements BlockModelProvider {
	private final Block parent;
	
	public BasePressurePlateBlock(Sensitivity rule, Block source) {
		super(rule, BlocksHelper.copySettingsOf(source).noCollission().noOcclusion().strength(0.5F));
		this.parent = source;
	}

	@Override
	@SuppressWarnings("deprecation")
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		return Collections.singletonList(new ItemStack(this));
	}

	@Override
	@Environment(EnvType.CLIENT)
	public BlockModel getItemModel(ResourceLocation resourceLocation) {
		return getBlockModel(resourceLocation, defaultBlockState());
	}

	@Override
	@Environment(EnvType.CLIENT)
	public @Nullable BlockModel getBlockModel(ResourceLocation resourceLocation, BlockState blockState) {
		ResourceLocation parentId = Registry.BLOCK.getKey(parent);
		Optional<String> pattern;
		if (blockState.getValue(POWERED)) {
			pattern = PatternsHelper.createJson(BasePatterns.BLOCK_PLATE_DOWN, parentId);
		} else {
			pattern = PatternsHelper.createJson(BasePatterns.BLOCK_PLATE_UP, parentId);
		}
		return ModelsHelper.fromPattern(pattern);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public UnbakedModel getModelVariant(ResourceLocation stateId, BlockState blockState, Map<ResourceLocation, UnbakedModel> modelCache) {
		String state = blockState.getValue(POWERED) ? "_down" : "_up";
		ResourceLocation modelId = new ResourceLocation(stateId.getNamespace(),
				"block/" + stateId.getPath() + state);
		registerBlockModel(stateId, modelId, blockState, modelCache);
		return ModelsHelper.createBlockSimple(modelId);
	}
}
