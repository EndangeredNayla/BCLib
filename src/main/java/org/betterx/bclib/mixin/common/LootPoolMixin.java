package org.betterx.bclib.mixin.common;

import org.betterx.bclib.interfaces.LootPoolAccessor;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import com.google.common.collect.Lists;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(LootPool.class)
public class LootPoolMixin implements LootPoolAccessor {
    @Shadow
    @Final
    public NumberProvider rolls;
    @Shadow
    @Final
    public NumberProvider bonusRolls;

    @Shadow
    @Final
    public List<LootItemCondition> conditions;

    @Shadow
    @Final
    public List<LootItemFunction> functions;

    @Shadow
    @Final
    public List<LootPoolEntryContainer> entries;

    @Override
    public LootPool bcl_mergeEntries(List<LootPoolEntryContainer> newEntries) {
        final List<LootPoolEntryContainer> merged = Lists.newArrayList(entries);
        merged.addAll(newEntries);

        return new LootPool(
                merged,
                this.conditions,
                this.functions,
                this.rolls,
                this.bonusRolls
        );
    }
}
