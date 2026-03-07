package me.ginterloper.client.gui;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MouthSelectScreen extends Screen {
    private static final int WIDTH = 200;
    private static final int HEIGHT = 150;

    private MouthListWidget mouthList;

    public MouthSelectScreen() {
        super(Text.translatable("gui.mouth_voice.title"));
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x8C101010);
//        context.enableScissor(0, 0, this.width, this.height);
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
        mouthList.setPosition((width - WIDTH + 10) / 2, (height - HEIGHT + 35) / 2);

        addSelectableChild(mouthList);

        mouthList.addMouth("gui.mouth_voice.standard", Identifier.of("voicemouth", "textures/entity/mouth-standard.png"), 48);
        mouthList.addMouth("gui.mouth_voice.classic", Identifier.of("voicemouth", "textures/entity/mouth-classic.png"), 48);
        mouthList.addMouth("gui.mouth_voice.minimal", Identifier.of("voicemouth", "textures/entity/mouth-minimal.png"), 48);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int x = (this.width - WIDTH) / 2 - 2;
        int y = (this.height - HEIGHT) / 2 + 2;

        Identifier tex = Identifier.of("voicemouth", "textures/gui/background.png");

        // Средняя повторяющаяся часть (18 px по текстуре)
        int middleY = y + 16;
        int middleHeight = HEIGHT - 16;  // вся высота минус header и footer
        int repeats = (middleHeight + 17) / 18;  // округляем вверх

        for (int i = 0; i < repeats; i++) {
            int drawY = middleY + i * 18;
            int drawHeight = Math.min(18, middleY + middleHeight - drawY);  // не выходим за пределы

            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    tex,
                    x, drawY,
                    0, 16,         // v = 16 (начало средней части)
                    WIDTH + 1, drawHeight,
                    WIDTH + 19, 256
            );
        }
        context.fill(
                (width - WIDTH + 10) / 2,
                (height - HEIGHT + 35) / 2,
                (width + WIDTH - 14) / 2,
                (height + HEIGHT - 12) / 2,
                0xFF626262
        );

        mouthList.render(context, mouseX, mouseY, delta);

        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                tex,
                x, y,
                0, 0,          // u, v начала
                WIDTH + 1, 16,     // ширина, высота на экране
                WIDTH + 19, 256       // размер всей текстуры (подставь свои, если текстура другого размера)
        );

        // Нижняя часть (32 px высотой)
        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                tex,
                x, y + HEIGHT - 8,
                0, 50,         // v = 34 (начало footer)
                WIDTH + 1, 16,
                WIDTH + 19, 256
        );

        // Заголовок поверх фона
        assert client != null;
        context.drawText(
                client.textRenderer,
                Text.translatable("gui.mouth_voice.title"),
                this.width / 2 - client.textRenderer.getWidth("Select Mouth") / 2,
                y + 5,
                0xFF000000,
                false
        );


    }
}
