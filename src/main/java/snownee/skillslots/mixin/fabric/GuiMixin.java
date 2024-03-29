package snownee.skillslots.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import snownee.skillslots.client.gui.UseScreen;

@Mixin(Gui.class)
public class GuiMixin {
	@Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
	private void skillslots$renderCrosshair(GuiGraphics guiGraphics, CallbackInfo ci) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.screen != null && mc.screen.getClass() == UseScreen.class) {
			ci.cancel();
		}
	}
}
