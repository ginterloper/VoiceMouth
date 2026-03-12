package me.ginterloper.client.gui;

import me.ginterloper.client.VoiceMouthClient;
import me.ginterloper.client.config.MouthConfig;
import me.ginterloper.core.ModConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.Click;

public class MouthListWidget extends EntryListWidget<MouthListWidget.MouthEntry> {

    private static final int ITEM_HEIGHT = 30;
    private static final int ICON_SIZE = 16;
    private static final Identifier SELECTED_ICON = Identifier.of(ModConstants.MOD_ID, "textures/gui/mouth_selected.png");

    public MouthListWidget(MinecraftClient client, int width, int height, int top, int bottom) {
        super(client, width, height, top, bottom);
        ((me.ginterloper.mixin.EntryListWidgetAccessor) this).voicemouth$setItemHeight(ITEM_HEIGHT);
    }

    public void addMouth(String translationKey, Identifier texture, int texHeight, float scale) {
        this.addEntry(new MouthEntry(translationKey, texture, texHeight, scale));
    }

    @Override
    public int getRowWidth() {
        return this.width - 8;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    public class MouthEntry extends Entry<MouthEntry> {

        private final String translationKey;
        private final Identifier texture;
        private final int textureHeight;
        private final float scale;

        public MouthEntry(String translationKey, Identifier texture, int textureHeight, float scale) {
            this.translationKey = translationKey;
            this.texture = texture;
            this.textureHeight = textureHeight;
            this.scale = scale;
        }

        @Override
        public boolean mouseClicked(Click click, boolean doubled) {
            MouthConfig.setMouth(this.texture);
            var client = MinecraftClient.getInstance();
            client.getSoundManager().play(
                    PositionedSoundInstance.master(
                            SoundEvents.UI_BUTTON_CLICK.value(),
                            1.0f, 0.1f
                            )
            );
            VoiceMouthClient.syncSelectedMouthToServer();
            return super.mouseClicked(click, doubled);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            int index = MouthListWidget.this.children().indexOf(this);
            int x = MouthListWidget.this.getRowLeft();
            int y = MouthListWidget.this.getRowTop(index);

            int entryWidth = MouthListWidget.this.getRowWidth();
            int entryHeight = MouthListWidget.this.itemHeight;

            int iconX = x + 6;

            boolean isSelected = MouthConfig.getMouth().equals(this.texture);
            int padding = 2;

            int bgX = x - padding - 2;
            int bgY = y + padding;
            int bgHeight = entryHeight - 2 * padding;

            context.fill(bgX, bgY, x + entryWidth, bgY + bgHeight, 0xFF4a4a4a);

            if (isSelected) {
                context.fill(bgX, bgY, x + entryWidth, bgY + bgHeight, 0xFF282828);
            } else if (hovered) {
                context.fill(bgX, bgY, x + entryWidth, bgY + bgHeight, 0xFF5a5a5a);
            }

            int iconY = bgY + (bgHeight - ICON_SIZE) / 2;

            final int FRAME_SIZE = 16;
            final int FRAME_TIME_MS = 100;
            int totalFrames = textureHeight / FRAME_SIZE;
            long currentTime = System.currentTimeMillis();
            int frame = (int) ((currentTime / FRAME_TIME_MS) % totalFrames);
            float frameHeightNormalized = 1.0f / totalFrames;
            float vStart = frame * frameHeightNormalized;

            context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                texture,
                iconX,
                iconY,
                0,
                vStart * textureHeight,
                ICON_SIZE, ICON_SIZE,
                ICON_SIZE,
                textureHeight
            );

            context.drawText(
                    client.textRenderer,
                    Text.translatable(translationKey),
                    x + ICON_SIZE + 15,
                    y + (entryHeight - 8) / 2 + 1,
                    0xFFFFFFFF,
                    false
            );

            if (isSelected) {
                context.drawTexture(
                        RenderPipelines.GUI_TEXTURED,
                        SELECTED_ICON,
                        iconX,
                        iconY,
                        0,
                        0,
                        16, 16,
                        16, 16
                );
            }
        }
    }
}