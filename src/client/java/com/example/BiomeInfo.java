package com.example;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.Map;

public class BiomeInfo {


    Map<String, VanillaEntry> vanillaInfo;



    public interface EntryDrawer { void draw(DrawContext context, int x, int y); }

    public record VanillaEntry(int x, int y, String cat) implements EntryDrawer {
        @Override
        public void draw(DrawContext context, int x, int y) {
            context.drawTexture(new Identifier("modid","textures/gui/biome_icons.png"), x + 1 + 6, y + 1 + 2, this.x * 16, this.y * 16, 16, 16, 128, 192);
        }
    }

    public record ModdedEntry(Identifier texture, String cat) implements EntryDrawer {
        @Override
        public void draw(DrawContext context, int x, int y) {
            context.drawTexture(new Identifier("modid","textures/gui/biome_icons.png"), x + 1 + 6, y + 1 + 2, this.x * 16, this.y * 16, 16, 16, 128, 192);


        }
    }



}
