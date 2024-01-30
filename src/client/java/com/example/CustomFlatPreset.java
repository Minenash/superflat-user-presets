package com.example;

import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.RegistryKey;
import net.minecraft.structure.StructureSet;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;

import java.util.Set;

public class CustomFlatPreset {

    public String name;
    public ItemConvertible icon;
    public RegistryKey<Biome> biome;
    public Set<RegistryKey<StructureSet>> structureSetKeys;
    public boolean hasFeatures;
    public boolean hasLakes;
    public FlatChunkGeneratorLayer layers;


}
