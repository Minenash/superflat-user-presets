package com.example.widgets;

import com.example.Utils;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.util.Delta;
import io.wispforest.owo.ui.util.UISounds;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.lwjgl.glfw.GLFW;


public class Expandable extends FlowLayout {

    private final FlowLayout contents = Containers.verticalFlow(Sizing.fill(),Sizing.content());
    private boolean expanded = false;
    private final SpinnyBoiComponent spinny = new SpinnyBoiComponent();

    public Expandable(String header, Component right) {
        super(Sizing.fill(), Sizing.content(), Algorithm.VERTICAL);
        margins(Insets.of(6,2,6, 12));

        BaseComponent h = Containers.horizontalFlow(Sizing.fill(), Sizing.content())
                .child(Containers.horizontalFlow(Sizing.fill(50), Sizing.content())
                        .child(Utils.text("§l" + header))
                        .child(spinny)
                        .allowOverflow(true))
                .child(Containers.horizontalFlow(Sizing.fill(50), Sizing.content())
                        .child(right)
                        .horizontalAlignment(HorizontalAlignment.RIGHT));

        child(h);
    }

    public FlowLayout content(Component child) {
        contents.child(child);
        return this;
    }

    public void toggleExpansion() {
        expanded = !expanded;
        if (expanded) {
            child(contents);
            spinny.targetRotation = 90;
        }
        else {
            removeChild(contents);
            spinny.targetRotation = 0;
        }
        UISounds.playInteractionSound();
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        final var superResult = super.onMouseDown(mouseX, mouseY, button);

        if (mouseY <= this.children.get(0).fullSize().height() && !superResult) {
            toggleExpansion();
            return true;
        }

        return superResult;
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            toggleExpansion();
            super.onKeyPress(keyCode, scanCode, modifiers);
            return true;
        }
        return super.onKeyPress(keyCode, scanCode, modifiers);
    }



    protected static class SpinnyBoiComponent extends BaseComponent {
        private Identifier TEXTURE = new Identifier("transferable_list/select");

        protected float rotation = 0;
        protected float targetRotation = 0;
        protected int size = 16;

        public SpinnyBoiComponent() {
            this.margins(Insets.of(0, 0, 2, 5+2));
        }

        @Override
        public void update(float delta, int mouseX, int mouseY) {
            super.update(delta, mouseX, mouseY);
            this.rotation += Delta.compute(this.rotation, this.targetRotation, delta * .65);
        }

        @Override
        public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
            var matrices = context.getMatrices();

            matrices.push();
            matrices.translate(this.x + size / 2f - 1, this.y + size / 2f - 1 - 2, 0);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(this.rotation));
            matrices.translate(-(this.x + size / 2f), -(this.y + size / 2f), 0);
//            matrices.scale(0.5F, 0.5F,1);

            context.drawGuiTexture(TEXTURE, x, y, size, size);

            matrices.pop();
        }

        @Override protected int determineVerticalContentSize(Sizing sizing) { return 9; }
        @Override protected int determineHorizontalContentSize(Sizing sizing) { return size; }
    }

}
