package com.example;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import org.apache.commons.lang3.text.WordUtils;

public class Utils {

    public static LabelComponent text(Object info) {
        return Components.label(Text.literal(info.toString()));
    }

    public static String idToWords(String id) {
        return WordUtils.capitalize(id.replace('_', ' '));
    }

    public static FlowLayout spaceBetween(Component left, Component right) {
        return Containers.horizontalFlow(Sizing.fill(), Sizing.content())
                .child(Containers.horizontalFlow(Sizing.fill(50), Sizing.content()).child(left))
                .child(Containers.horizontalFlow(Sizing.fill(50), Sizing.content()).child(right).horizontalAlignment(HorizontalAlignment.RIGHT));
    }

    public static String getConfigString(FlatChunkGeneratorConfig config, boolean vanilla) {
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < config.getLayers().size(); ++i) {
            if (i > 0)
                stringBuilder.append(",");
            stringBuilder.append(config.getLayers().get(i));
        }

        stringBuilder.append(";");
        stringBuilder.append(config.getBiome().getKey().map(RegistryKey::getValue)
                .orElseThrow(() -> new IllegalStateException("Biome not registered")));
        return stringBuilder.toString();
    }

}
