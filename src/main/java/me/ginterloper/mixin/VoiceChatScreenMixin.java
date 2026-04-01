package me.ginterloper.mixin;

import me.ginterloper.client.gui.MouthSelectScreen;
import de.maxhenkel.voicechat.gui.VoiceChatScreen;
import de.maxhenkel.voicechat.gui.widgets.ImageButton;
import me.ginterloper.core.ModConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VoiceChatScreen.class)
public abstract class VoiceChatScreenMixin extends Screen {

    @Unique
    private static final Identifier ICON =
            Identifier.of(ModConstants.MOD_ID, "textures/gui/icon_in_game.png");

    protected VoiceChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addMouthButton(CallbackInfo ci) {
        ImageButton button = new ImageButton(
                this.width / 2 + 27,
                this.height / 2 + 12,
                ICON,
                b -> MinecraftClient.getInstance().setScreen(
                        new MouthSelectScreen()
                )
        );
        button.setTooltip(Tooltip.of(Text.translatable("gui.voicemouth.select_mouth")));
        this.addDrawableChild(button);
    }

}