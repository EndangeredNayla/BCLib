package org.betterx.bclib.api.v3.levelgen.features;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BCLConfigureFeature<F extends Feature<FC>, FC extends FeatureConfiguration> {
    private static final Map<Holder<ConfiguredFeature<?, ?>>, BCLConfigureFeature<?, ?>> KNOWN = new HashMap<>();

    public final ResourceLocation id;
    public final Holder<ConfiguredFeature<FC, F>> configuredFeature;
    public final boolean registered;

    BCLConfigureFeature(ResourceLocation id, Holder<ConfiguredFeature<FC, F>> configuredFeature, boolean registered) {
        this.id = id;
        this.configuredFeature = configuredFeature;
        this.registered = registered;
    }

    public F getFeature() {
        return configuredFeature.value().feature();
    }

    public FC getConfiguration() {
        return configuredFeature.value().config();
    }


    public BCLPlacedFeatureBuilder<F, FC> place() {
        return place(this.id);
    }

    public BCLPlacedFeatureBuilder<F, FC> place(ResourceLocation id) {
        return BCLPlacedFeatureBuilder.place(id, this);
    }

    static <F extends Feature<FC>, FC extends FeatureConfiguration> BCLConfigureFeature<F, FC> create(Holder<ConfiguredFeature<FC, F>> registeredFeature) {
        return (BCLConfigureFeature<F, FC>) KNOWN.computeIfAbsent(
                (Holder<ConfiguredFeature<?, ?>>) (Object) registeredFeature,
                holder -> new BCLConfigureFeature<>(holder.unwrapKey().orElseThrow()
                                                          .location(), registeredFeature, false)
        );
    }

    public boolean placeInWorld(ServerLevel level, BlockPos pos, RandomSource random) {
        return placeInWorld(getFeature(), getConfiguration(), level, pos, random);
    }

    private static boolean placeUnboundInWorld(
            Feature<?> feature,
            FeatureConfiguration config,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        if (config instanceof RandomPatchConfiguration rnd) {
            var configured = rnd.feature().value().feature().value();
            feature = configured.feature();
            config = configured.config();
        }

        if (feature instanceof UserGrowableFeature growable) {
            return growable.grow(level, pos, random, config);
        }

        FeaturePlaceContext context = new FeaturePlaceContext(
                Optional.empty(),
                level,
                level.getChunkSource().getGenerator(),
                random,
                pos,
                config
        );
        return feature.place(context);
    }

    public static boolean placeInWorld(
            Feature<NoneFeatureConfiguration> feature,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        return placeUnboundInWorld(feature, FeatureConfiguration.NONE, level, pos, random);
    }

    public static <FC extends FeatureConfiguration> boolean placeInWorld(
            Feature<FC> feature,
            FC config,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        return placeUnboundInWorld(feature, config, level, pos, random);
    }
}
