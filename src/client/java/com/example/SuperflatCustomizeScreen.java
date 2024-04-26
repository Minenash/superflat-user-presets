package com.example;

import com.example.widgets.BiomeEntry;
import com.example.widgets.Expandable;
import com.example.widgets.LayerWidget;
import com.example.widgets.PresetEntry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.CustomizeFlatLevelScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FlatLevelGeneratorPresetTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.Utils.idToWords;
import static com.example.Utils.text;


public class SuperflatCustomizeScreen extends BaseOwoScreen<FlowLayout> {

    private final CreateWorldScreen parent;

    public static final MutableText UNKNOWN_PRESET_TEXT = Text.translatable("flat_world_preset.unknown");

    private final List<Pair<FlatLevelGeneratorPreset,Text>> vanillaPresets = new ArrayList<>();

    private FlatLevelGeneratorPreset selectedPreset;

    public GeneratorOptionsHolder gen;
    public FlatChunkGeneratorConfig config;

    private static final Surface OPTIONS_LIST = (context, component) -> {
        RenderSystem.setShaderColor(0.125F, 0.125F, 0.125F, 1);
        context.drawTexture(Screen.OPTIONS_BACKGROUND_TEXTURE, component.x(), component.y(), 0, 0, component.width(), component.height(), 32, 32);
        RenderSystem.setShaderColor(1, 1, 1, 1);
    };

    public SuperflatCustomizeScreen(CreateWorldScreen parent) {
        this.parent = parent;

        FabricLoader.getInstance().getModContainer("dw").get().getMetadata().getIconPath(16);

        for (var e : FabricLoader.getInstance().getAllMods()) {
            System.out.println(e.getMetadata().getId() + " - " + e.getMetadata().getIconPath(16));
        }


        gen = parent.getWorldCreator().getGeneratorOptionsHolder();
        ChunkGenerator chunkGenerator = gen.selectedDimensions().getChunkGenerator();
        DynamicRegistryManager.Immutable dynamicRegistryManager = gen.getCombinedRegistryManager();
        var registryEntryLookup = dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.BIOME);
        var registryEntryLookup2 = dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.STRUCTURE_SET);
        var registryEntryLookup3 = dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.PLACED_FEATURE);
        config = chunkGenerator instanceof FlatChunkGenerator ? ((FlatChunkGenerator)chunkGenerator).getConfig() : FlatChunkGeneratorConfig.getDefaultConfig(registryEntryLookup, registryEntryLookup2, registryEntryLookup3);

        for (RegistryEntry<FlatLevelGeneratorPreset> registryEntry : gen.getCombinedRegistryManager().get(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET).iterateEntries(FlatLevelGeneratorPresetTags.VISIBLE)) {
            Set set = (registryEntry.value()).settings().getLayers().stream().map(layer -> layer.getBlockState().getBlock()).filter(block -> !block.isEnabled(gen.dataConfiguration().enabledFeatures())).collect(Collectors.toSet());
            if (!set.isEmpty()) {
                System.out.println("Discarding flat world preset {} since it contains experimental blocks {FIX}");
                continue;
            }
            Text text = registryEntry.getKey().map(key -> Text.translatable(key.getValue().toTranslationKey("flat_world_preset"))).orElse(UNKNOWN_PRESET_TEXT);
            vanillaPresets.add(new Pair<>(registryEntry.value(), text));
        }
        selectedPreset = vanillaPresets.get(0).getLeft();

    }

    public void setPreset(FlatLevelGeneratorPreset preset) {
        selectedPreset = preset;
        System.out.println(Utils.getConfigString(selectedPreset.settings(), true));
        presetText.text(Utils.getConfigString(selectedPreset.settings(), true));
        listB.clearChildren();
        buildListB();
    }

    public void setBiome(RegistryKey<Biome> biome) {
        listB.clearChildren();
        buildListB();
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_R || keyCode == GLFW.GLFW_KEY_F5)
            this.client.setScreen(new SuperflatCustomizeScreen(parent));
        else if (keyCode == GLFW.GLFW_KEY_E) {
            this.client.setScreen(new CustomizeFlatLevelScreen(parent, (config) -> {}, config));
        }
        else
            return super.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }

    private FlowLayout root;
    private FlowLayout listB;
    private TextBoxComponent presetText;
    private ButtonComponent vanilla;
    private ButtonComponent custom;


    @Override
    protected void build(FlowLayout root) {
        this.root = root;
        root.surface(Surface.OPTIONS_BACKGROUND);
        root.horizontalAlignment(HorizontalAlignment.CENTER);
        root.child(text("Superflat Customization Screen").margins(Insets.of(8)));

        FlowLayout main = Containers.horizontalFlow(Sizing.fill(), Sizing.fill(78));

        FlowLayout right = Containers.verticalFlow(Sizing.fill(55), Sizing.fill());
        right.margins(Insets.right(8));
        presetText = Components.textBox(Sizing.fill()).text(Utils.getConfigString(selectedPreset.settings(), true));
        presetText.setMaxLength(Integer.MAX_VALUE);
        presetText.margins(Insets.of(0,2,0,0));
        right.child(presetText);

        listB = Containers.verticalFlow(Sizing.fill(), Sizing.content());
        listB.horizontalAlignment(HorizontalAlignment.CENTER);
        buildListB();
        right.child(Containers.verticalScroll(Sizing.fill(), Sizing.fill(),listB).scrollbar(ScrollContainer.Scrollbar.vanillaFlat()).scrollbarThiccness(6).surface(OPTIONS_LIST));
        main.child(right);

        FlowLayout left = Containers.verticalFlow(Sizing.fill(45), Sizing.fill());
        vanilla = Components.button(Text.literal("Vanilla"), b -> {
            b.active(false);
            custom.active(true);
        }).active(false);
        vanilla.sizing(Sizing.fill(50), Sizing.fixed(16)).margins(Insets.of(3,2,8,3));

        custom = Components.button(Text.literal("Custom"), b -> {
            b.active(false);
            vanilla.active(true);
        });
        custom.sizing(Sizing.fill(50), Sizing.fixed(16)).margins(Insets.of(3,2,3,30));

        left.child(Containers.horizontalFlow(Sizing.fill(), Sizing.content()).child(vanilla).child(custom));

        FlowLayout listA = Containers.verticalFlow(Sizing.fill(), Sizing.content());
        for (RegistryKey<Biome> key : gen.getCombinedRegistryManager().get(RegistryKeys.BIOME).getKeys()) {
            listA.child(new BiomeEntry(this, key));
            if (!key.getValue().toString().contains("ocean"))
                System.out.println(key.getValue());
        }

//        for (var preset : vanillaPresets) {
//            PresetEntry entry = new PresetEntry(this, preset);
//            if (selectedPreset == preset.getLeft())
//                entry.select();
//            listA.child(entry);
//        }
        left.child(Containers.verticalScroll(Sizing.fill(), Sizing.fill(),listA).scrollbar(ScrollContainer.Scrollbar.vanillaFlat()).scrollbarThiccness(6).surface(OPTIONS_LIST).margins(Insets.of(4)));
        main.child(0, left);

        root.child(main);

        root.child(Containers.horizontalFlow(Sizing.fill(), Sizing.content())
                .child(Components.button(Text.literal("Done"), b -> {}).sizing(Sizing.fixed(150), Sizing.fixed(20)).margins(Insets.right(5)))
                .child(Components.button(Text.literal("Cancel"), b -> {}).sizing(Sizing.fixed(150), Sizing.fixed(20)).margins(Insets.left(5)))
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .margins(Insets.of(4))
        );

        //        labels.child(text("Presets").horizontalTextAlignment(HorizontalAlignment.CENTER).sizing(Sizing.fill(50), Sizing.content()).margins(Insets.bottom(4)));
//        labels.child(text("Details").horizontalTextAlignment(HorizontalAlignment.CENTER).sizing(Sizing.fill(50), Sizing.content()).margins(Insets.top(2)));


    }

    public void buildListB() {
        listB.child( infoLine("Biome ",
                text(WordUtils.capitalize(I18n.translate("biome." + selectedPreset.settings().getBiome().getKey().get().getValue().toTranslationKey())))));


//selectedPreset.settings().hasFeatures
        var expandable = new Expandable("Biome Features", text(selectedPreset.settings().hasFeatures ? "§a✔" : "§c\uD83D\uDDD9"));
        for (var featureSet : selectedPreset.settings().getBiome().value().getGenerationSettings().getFeatures())
            for (var feature : featureSet)
                expandable.content(text("   - " + idToWords(feature.getKey().get().getValue().getPath())).margins(Insets.top(4)));
        listB.child(expandable);

        listB.child( infoLine("Lakes ", text(selectedPreset.settings().hasLakes ? "§a✔" : "§c\uD83D\uDDD9")));
        listB.child(text("§lLayers").margins(Insets.of(6,4,0,0)));

        var layers = selectedPreset.settings().getLayers();
        for (int i = layers.size()-1; i >= 0; i--)
            listB.child(new LayerWidget(layers.get(i)));

    }



    public static Component infoLine(String property, Component right) {
        return Containers.horizontalFlow(Sizing.fill(), Sizing.content())
                .child(Containers.horizontalFlow(Sizing.fill(50), Sizing.content()).child(text("§l" + property)).allowOverflow(true))
                .child(Containers.horizontalFlow(Sizing.fill(50), Sizing.content()).child(right).horizontalAlignment(HorizontalAlignment.RIGHT).allowOverflow(true))
                .margins(Insets.of(4,0,6,12));
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

}
