package snownee.skillslots.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import snownee.skillslots.SkillSlotsModule;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	@Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setAbsorptionAmount(F)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void skillslots$actuallyHurt(DamageSource damageSource, float f, CallbackInfo ci, float g) {
		SkillSlotsModule.causeDamage(damageSource, (LivingEntity) (Object) this, g);
	}
}
