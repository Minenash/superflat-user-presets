package com.example.widgets;

import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;

import java.util.HashMap;
import java.util.Map;

public class LayerWidget extends BaseComponent {
    private static final Identifier SLOT_TEXTURE = new Identifier("container/slot");
    private static final MinecraftClient client = MinecraftClient.getInstance();

    private final FlatChunkGeneratorLayer layer;

    public LayerWidget(FlatChunkGeneratorLayer layer) {
        this.sizing(Sizing.fill(), Sizing.content());
        this.margins(Insets.right(6));

        this.layer = layer;
    }
    private final ButtonWidget up = ButtonWidget.builder(Text.literal("▲"), b -> {}).size(9,9).build();
    private final ButtonWidget down = ButtonWidget.builder(Text.literal("▼"), b -> {}).size(9,9).build();
    private final ButtonWidget delete = ButtonWidget.builder(Text.literal("§c-"), b -> {}).size(14,16).build();
    private final TextFieldWidget layerInput = new TextFieldWidget(client.textRenderer, 26, 16, Text.empty());

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        Block block = layer.getBlockState().getBlock();
        String thickness = "900";//Integer.toString(layer.getThickness());
        Item item = block == Blocks.WATER ? Items.WATER_BUCKET : block == Blocks.LAVA ? Items.LAVA_BUCKET : block.asItem();

        context.drawGuiTexture(SLOT_TEXTURE, x+6+12, y+2, 0, 18, 18);
        context.drawItemWithoutEntity(new ItemStack(item), x + 1 + 6 + 12, y + 1 + 2);
        context.drawText(client.textRenderer, block.getName(), x + 18 + 5 + 6 + 12, y + 6 + 2, 0xFFFFFF, false);

        if (hovered) {
            up.setX(x + 6);
            up.setY(y + 2);
            up.draw(context, mouseX, mouseY, partialTicks, delta);
            down.setX(x + 6);
            down.setY(y + 12);
            down.draw(context, mouseX, mouseY, partialTicks, delta);

            layerInput.setText(thickness);
            layerInput.setX(x + width - 5 - 24 - 17);
            layerInput.setY(y + 4);
            layerInput.renderWidget(context, mouseX, mouseY, delta);

            delete.setX(x + width - 6 - 12);
            delete.setY(y + 4);
            delete.render(context, mouseX, mouseY, delta);
        }
        else
            context.drawText(client.textRenderer, thickness, x + width - client.textRenderer.getWidth(thickness) - 24, y + 6 + 2, 0xFFFFFF, false);

    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return 18 + 5 + client.textRenderer.getWidth(layer.getBlockState().getBlock().getName());
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return 18 + 4;
    }

}
