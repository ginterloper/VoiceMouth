package me.ginterloper.mixin;

import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;

@Mixin(EntryListWidget.class)
public class EntryListWidgetMutableMixin {

    @Shadow
    @Final
    @Mutable
    protected int itemHeight;
}

