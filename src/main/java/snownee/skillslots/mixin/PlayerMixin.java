package snownee.skillslots.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import snownee.skillslots.duck.SkillSlotsPlayer;
import snownee.skillslots.SkillSlotsHandler;

@Mixin(Player.class)
public class PlayerMixin implements SkillSlotsPlayer {

	@Unique
	private final SkillSlotsHandler skillslots$handler = new SkillSlotsHandler((Player) (Object) this);

	@Override
	public @Nullable SkillSlotsHandler skillslots$getHandler() {
		return skillslots$handler;
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void skillslots$tick(CallbackInfo info) {
		skillslots$handler.tick();
	}

	@Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
	private void skillslots$readAdditionalSaveData(CompoundTag compound, CallbackInfo info) {
		if (compound.contains("SkillSlots")) {
			skillslots$handler.deserializeNBT(compound.getCompound("SkillSlots"));
		} else if (compound.contains("EverPotion")) {
			// data-fixing. remove in 1.21
			skillslots$handler.deserializeNBT(compound.getCompound("EverPotion"));
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
	private void skillslots$addAdditionalSaveData(CompoundTag compound, CallbackInfo info) {
		compound.put("SkillSlots", skillslots$handler.serializeNBT());
	}
}
