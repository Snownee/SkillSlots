package snownee.skillslots.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import snownee.skillslots.SkillSlotsModule;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
	@Inject(method = "addPlayer", at = @At("HEAD"))
	private void skillslots$addPlayer(ServerPlayer player, CallbackInfo ci) {
		SkillSlotsModule.sync(player);
	}
}
