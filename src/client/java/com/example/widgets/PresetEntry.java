package com.example.widgets;

import com.example.SuperflatCustomizeScreen;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.util.UISounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import org.lwjgl.glfw.GLFW;


public class PresetEntry extends BaseComponent {
    private static final Identifier SLOT_TEXTURE = new Identifier("container/slot");

    private final SuperflatCustomizeScreen presetList;
    public final Pair<FlatLevelGeneratorPreset, Text> preset;
    public boolean selected = false;

    public PresetEntry(SuperflatCustomizeScreen presetList, Pair<FlatLevelGeneratorPreset, Text> preset) {
//        this.padding(Insets.both(12, 2));
        sizing(Sizing.fill(), Sizing.content());
        this.preset = preset;
        this.presetList = presetList;
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        if (this.selected) {
            context.fill(this.x + 4, this.y+2, this.x + this.width - 10, this.y + this.height, 0xFF000000);
            context.drawRectOutline(this.x + 2 + 2, this.y, this.width - 4 - 4 - 6, this.height, 0xFFFFFFFF);
        }
        context.drawGuiTexture(SLOT_TEXTURE, x+6, y+2, 0, 18, 18);
        context.drawItemWithoutEntity(new ItemStack(preset.getLeft().displayItem().value()), x + 1 + 6, y + 1 + 2);
        context.drawText(MinecraftClient.getInstance().textRenderer, preset.getRight(), x + 18 + 5 + 6, y + 6 + 2, 0xFFFFFF, false);
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
                if (sibling instanceof PresetEntry container && sibling != this)
                    container.selected = false;

        this.selected = true;
        presetList.setPreset(preset.getLeft());
        UISounds.playInteractionSound();
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return true;
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return 18 + 5 + MinecraftClient.getInstance().textRenderer.getWidth(preset.getRight());
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return 18 + 4;
    }

}
