package com.example.widgets;

import com.example.SuperflatCustomizeScreen;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.util.UISounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;


public class BiomeEntry extends BaseComponent {
    private static final Identifier SLOT_TEXTURE = new Identifier("container/slot");

    private final SuperflatCustomizeScreen presetList;
    public final RegistryKey<Biome> biome;
    public final String biomeName;
    public boolean selected = false;

    public BiomeEntry(SuperflatCustomizeScreen biomeList, RegistryKey<Biome> biome) {
//        this.padding(Insets.both(12, 2));
        sizing(Sizing.fill(), Sizing.content());
        this.biome = biome;
        this.biomeName = WordUtils.capitalize(I18n.translate("biome." + biome.getValue().toTranslationKey()));
        this.presetList = biomeList;
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        if (this.selected) {
            context.fill(this.x + 4, this.y+2, this.x + this.width - 10, this.y + this.height, 0xFF000000);
            context.drawRectOutline(this.x + 2 + 2, this.y, this.width - 4 - 4 - 6, this.height, 0xFFFFFFFF);
        }
//        context.drawGuiTexture(SLOT_TEXTURE, x+6, y+2, 0, 18, 18);

        int texCords = BIOME_TO_ICON_CORDS.getOrDefault(biome.getValue().toString(), 0x7B);
//        System.out.println(biome.getValue().toString() + ": " + ((texCords & 0xF0)>>4) * 16 + "," + (texCords & 0x0F) * 16);
        context.drawTexture(new Identifier("modid","textures/gui/biome_icons.png"), x + 1 + 6, y + 1 + 2, ((texCords & 0xF0)>>4) * 16, (texCords & 0x0F) * 16, 16, 16, 128, 192);


//        context.drawItemWithoutEntity(new ItemStack(Items.GRASS_BLOCK), x + 1 + 6, y + 1 + 2);
        context.drawText(MinecraftClient.getInstance().textRenderer, biomeName, x + 18 + 5 + 6, y + 6 + 2, 0xFFFFFF, false);
//        super.draw(context, mouseX, mouseY, partialTicks, delta);
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT)
            return super.onMouseDown(mouseX, mouseY, button);
        super.onMouseDown(mouseX, mouseY, button);
        this.select();
        return true;
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        boolean success = super.onKeyPress(keyCode, scanCode, modifiers);

        if (keyCode != GLFW.GLFW_KEY_ENTER && keyCode != GLFW.GLFW_KEY_SPACE && keyCode != GLFW.GLFW_KEY_KP_ENTER)
            return success;

        this.select();
        return true;
    }

    public void select() {
        if (this.parent != null)
            for (var sibling : this.parent.children())
                if (sibling instanceof BiomeEntry container && sibling != this)
                    container.selected = false;

        this.selected = true;
        presetList.setBiome(biome);
        UISounds.playInteractionSound();
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return true;
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return 18 + 5 + MinecraftClient.getInstance().textRenderer.getWidth(biomeName);
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return 18 + 4;
    }

    public static final Map<String, Integer> BIOME_TO_ICON_CORDS = new HashMap<>() {{
        // Offland
        put("minecraft:deep_lukewarm_ocean", 0x1_8);
        put("minecraft:cold_ocean"         , 0x7_7);
        put("minecraft:warm_ocean"         , 0x4_8);
        put("minecraft:frozen_ocean"       , 0x2_2);
        put("minecraft:deep_ocean"         , 0x2_3);
        put("minecraft:ocean"              , 0x1_2);
        put("minecraft:deep_cold_ocean"    , 0x0_8);
        put("minecraft:lukewarm_ocean"     , 0x2_8);
        put("minecraft:deep_frozen_ocean"  , 0x0_7);
        put("minecraft:mushroom_fields"    , 0x0_1);

        // Highland
        put("minecraft:jagged_peaks", 0x7_A);
        put("minecraft:frozen_peaks", 0x0_B);
        put("minecraft:stony_peaks" , 0x2_B);
        put("minecraft:meadow"      , 0x4_A);
        put("minecraft:cherry_grove", 0x5_8);
        put("minecraft:grove"       , 0x5_A);
        put("minecraft:snowy_slopes", 0x6_A);
        put("minecraft:windswept_hills"         , 0x7_0);
        put("minecraft:windswept_gravelly_hills", 0x3_5);
        put("minecraft:windswept_forest"        , 0x5_3);

        // Woodland
        put("minecraft:forest"                 , 0x0_0);
        put("minecraft:flower_forest"          , 0x1_4);
        put("minecraft:taiga"                  , 0x6_3);
        put("minecraft:old_growth_pine_taiga"  , 0x4_1);
        put("minecraft:old_growth_spruce_taiga", 0x7_3);
        put("minecraft:snowy_taiga"            , 0x6_0);
        put("minecraft:birch_forest"           , 0x5_2);
        put("minecraft:old_growth_birch_forest", 0x2_4);
        put("minecraft:dark_forest"            , 0x7_2);
        put("minecraft:jungle"                 , 0x4_0);
        put("minecraft:sparse_jungle"          , 0x4_4);
        put("minecraft:bamboo_jungle"          , 0x5_8);

        // Wetland
        put("minecraft:river"                 , 0x5_1);
        put("minecraft:frozen_river"          , 0x6_1);
        put("minecraft:swamp"                 , 0x3_0);
        put("minecraft:mangrove_swamp"        , 0x3_B);
        put("minecraft:beach"                 , 0x7_1);
        put("minecraft:snowy_beach"           , 0x1_3);
        put("minecraft:stony_shore"           , 0x0_3);

        // Flatland
        put("minecraft:plains"          , 0x2_0);
        put("minecraft:sunflower_plains", 0x0_4);
        put("minecraft:snowy_plains"    , 0x5_0);
        put("minecraft:ice_spikes"      , 0x0_5);

        // Arid-land
        put("minecraft:desert"           , 0x1_0);
        put("minecraft:savanna"          , 0x6_2);
        put("minecraft:savanna_plateau"  , 0x2_6);
        put("minecraft:windswept_savanna", 0x5_4);
        put("minecraft:badlands"         , 0x3_1);
        put("minecraft:wooded_badlands"  , 0x6_4);
        put("minecraft:eroded_badlands"  , 0x7_4);

        // Cave
        put("minecraft:deep_dark"             , 0x4_B);
        put("minecraft:dripstone_caves"       , 0x3_A);
        put("minecraft:lush_caves"            , 0x1_B);

        // Void
        put("minecraft:the_void"              , 0x3_3);

        // Nether
        put("minecraft:nether_wastes"         , 0x1_1);
        put("minecraft:soul_sand_valley"      , 0x1_A);
        put("minecraft:crimson_forest"        , 0x7_9);
        put("minecraft:warped_forest"         , 0x0_A);
        put("minecraft:basalt_deltas"         , 0x2_A);

        // The End
        put("minecraft:the_end"               , 0x2_1);
        put("minecraft:small_end_islands"     , 0x4_7);
        put("minecraft:end_midlands"          , 0x5_7);
        put("minecraft:end_highlands"         , 0x3_7);
        put("minecraft:end_barrens"           , 0x6_7);

    }};

}
