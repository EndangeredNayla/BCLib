package org.betterx.bclib.mixin.client;

import org.betterx.bclib.api.v2.ModIntegrationAPI;
import org.betterx.bclib.client.models.CustomModelBakery;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Final
    @Shadow
    private Map<ResourceLocation, UnbakedModel> unbakedCache;

    @Inject(method = "<init>*", at = @At("TAIL"))
    private void bclib_findEmissiveModels(
            ResourceManager resourceManager,
            BlockColors blockColors,
            ProfilerFiller profiler,
            int mipmap,
            CallbackInfo info
    ) {
        //CustomModelBakery.setModelsLoaded(false);
        if (ModIntegrationAPI.hasCanvas()) {
            CustomModelBakery.loadEmissiveModels(unbakedCache);
        }
    }
}
