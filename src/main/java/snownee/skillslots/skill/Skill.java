package snownee.skillslots.skill;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Either;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import snownee.skillslots.SkillSlotsHandler;
import snownee.skillslots.SkillSlotsModule;

public abstract class Skill {
	public static final Skill EMPTY = new Skill(ItemStack.EMPTY) {
	};
	public final ItemStack item;
	public float progress;
	public float speed = 1;
	public int color = 0xCCCCCC;

	public Skill(ItemStack item) {
		this.item = item.copy();
	}

	/**
	 * Only modify skill.item's nbt here. Or refresh skills in the handler yourself.
	 */
	public void finishUsing(Player player, int slot) {
	}

	public void startUsing(Player player, int slot) {
	}

	public void abortUsing(Player player, int slot) {
	}

	public final Component getDisplayName() {
		if (item.isEmpty()) {
			return Component.empty();
		}
		if (item.hasCustomHoverName()) {
			return item.getHoverName();
		}
		return getDisplayNameInternal();
	}

	protected Component getDisplayNameInternal() {
		List<MobEffectInstance> effects = PotionUtils.getMobEffects(item);
		if (effects.size() == 1) {
			return effects.get(0).getEffect().getDisplayName();
		}
		return item.getHoverName();
	}

	public Component getActionDescription() {
		return getDisplayName();
	}

	public final boolean isEmpty() {
		return this == EMPTY;
	}

	public int getUseDuration() {
		return item.getUseDuration();
	}

	public boolean canBeToggled() {
		return false;
	}

	public void onToggled(Player player, SkillSlotsHandler handler, int slot) {
	}

	public int getChargeDuration(Player player) {
		return 0;
	}

	public boolean canUse(Player player) {
		return progress >= getChargeDuration(player);
	}

	public float getChargeSpeed(Player player) {
		return speed;
	}

	public boolean isConflicting(Skill that) {
		return false;
	}

	@Nullable
	public Either<SoundEvent, ResourceLocation> getChargeCompleteSound() {
		return Either.left(SkillSlotsModule.POTION_CHARGE_COMPLETE_SOUND.get());
	}
}
