package me.ginterloper.client.gui;

import me.ginterloper.client.config.MouthConfig;
import me.ginterloper.core.ModConstants;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MouthSelectScreen extends Screen {
    private static final int WIDTH = 200;
    private static final int HEIGHT = 150;

    private MouthListWidget mouthList;

    public MouthSelectScreen() {
        super(Text.translatable("gui.voicemouth.title"));
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x8C101010);
    }

    @Override
    protected void init() {
        mouthList = new MouthListWidget(
                client,
                WIDTH - 10,
                HEIGHT - 23,
                (height - HEIGHT) / 2,
                (height - HEIGHT) / 2
        );
        mouthList.setPosition((width - WIDTH + 9) / 2, (height - HEIGHT - 10) / 2);

        addSelectableChild(mouthList);

        for (MouthConfig.MouthDefinition mouth : MouthConfig.getRegisteredMouths()) {
            mouthList.addMouth(mouth.translationKey(), mouth.texture(), mouth.textureHeight(), mouth.scale());
        }

        int panelLeft = (width - WIDTH + 4) / 2;
        int panelRight = (width + WIDTH - 10) / 2;
        int panelBottom = (height + HEIGHT - 50) / 2;

        int sliderWidth = panelRight - panelLeft - 8;
        int sliderHeight = 15;
        int sliderX = panelLeft + 4;

        double normOffsetX = (MouthConfig.getOffsetX() + 4.0) / 8.0;
        double normOffsetY = (MouthConfig.getOffsetY() + 4.0) / 8.0;

        SliderWidget offsetXSlider = new SliderWidget(
                sliderX,
                panelBottom + 4,
                sliderWidth,
                sliderHeight,
                Text.translatable("gui.voicemouth.slider_x"),
                clamp01(normOffsetX)
        ) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("X: " + String.format("%.1f", sliderToOffset(this.value))));
            }

            @Override
            protected void applyValue() {
                MouthConfig.setOffsetX(sliderToOffset(this.value));
            }
        };

        SliderWidget offsetYSlider = new SliderWidget(
                sliderX,
                panelBottom + 4 + sliderHeight + 4,
                sliderWidth,
                sliderHeight,
                Text.translatable("gui.voicemouth.slider_y"),
                clamp01(normOffsetY)
        ) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Y: " + String.format("%.1f", sliderToOffset(this.value))));
            }

            @Override
            protected void applyValue() {
                MouthConfig.setOffsetY(sliderToOffset(this.value));
            }
        };

        addDrawableChild(offsetXSlider);
        addDrawableChild(offsetYSlider);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int x = (this.width - WIDTH) / 2 - 2;
        int y = (this.height - HEIGHT) / 2 - 20;

        Identifier tex = Identifier.of(ModConstants.MOD_ID, "textures/gui/background.png");

        int middleY = y + 16;
        int middleHeight = HEIGHT - 16;
        int repeats = (middleHeight + 17) / 18;

        for (int i = 0; i < repeats; i++) {
            int drawY = middleY + i * 18;
            int drawHeight = Math.min(18, middleY + middleHeight - drawY);

            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    tex,
                    x, drawY,
                    0, 16,
                    WIDTH + 1, drawHeight,
                    WIDTH + 19, 256
            );
        }

        context.fill(
                (width - WIDTH + 10) / 2,
                (height - HEIGHT - 10) / 2,
                (width + WIDTH - 14) / 2,
                (height + HEIGHT - 40) / 2,
                0xFF626262
        );

        mouthList.render(context, mouseX, mouseY, delta);

        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                tex,
                x, y,
                0, 0,
                WIDTH + 1, 16,
                WIDTH + 19, 256
        );
        for (int i = 0; i < 3; i++) {

            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    tex,
                    x, y + HEIGHT - 8 + i * 10,
                    0, 50,
                    WIDTH + 1, 10,
                    WIDTH + 19, 256
            );
        }

        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                tex,
                x, y + HEIGHT + 16,
                0, 40,
                WIDTH + 1, 64,
                WIDTH + 19, 256
        );

        context.drawText(
                client.textRenderer,
                Text.translatable("gui.voicemouth.title"),
                this.width / 2 - client.textRenderer.getWidth(Text.translatable("gui.voicemouth.title")) / 2,
                y + 5,
                0xFF000000,
                false
        );

        super.render(context, mouseX, mouseY, delta);
    }

    private static double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private static float sliderToOffset(double sliderValue) {
        return (float) Math.round((sliderValue * 8.0 - 4.0) * 10) / 10;
    }
}