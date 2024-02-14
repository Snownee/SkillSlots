package snownee.skillslots.skill;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import snownee.skillslots.SkillSlotsHandler;
import snownee.skillslots.util.CommonProxy;

public class SimpleSkill extends Skill {

	public boolean wasOnCooldown;

	public SimpleSkill(ItemStack item) {
		super(item);
	}

	@Override
	public void finishUsing(Player player, int slot) {
		Level level = player.level();
		ServerPlayer serverPlayer = level.isClientSide ? null : (ServerPlayer) player;
		ItemStack prev = player.getMainHandItem();
		ItemStack copy = this.item.copy();
		CompoundTag tag = copy.getOrCreateTagElement("SkillSlots");
		tag.putBoolean("IsUsing", true);
		Inventory inventory = player.getInventory();
		inventory.items.set(inventory.selected, copy); // avoid emit game event
		player.setItemInHand(InteractionHand.MAIN_HAND, copy);

		Vec3 start = player.getEyePosition(1);
		Vec3 end = start.add(player.getLookAngle().scale(CommonProxy.getBlockReach(player)));
		HitResult hit = level.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
		if (hit.getType() != HitResult.Type.MISS) {
			end = hit.getLocation();
		} else {
			end = start.add(player.getLookAngle().scale(CommonProxy.getEntityReach(player)));
		}
		EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(level, player, start, end, player.getBoundingBox().expandTowards(end).inflate(1), entity -> entity != player);
		if (entityHit != null) {
			hit = entityHit;
		}

		InteractionResult result = InteractionResult.PASS;
		switch (hit.getType()) {
			case BLOCK -> {
				if (level.isClientSide) {
					UseOnContext ctx = new UseOnContext(player, InteractionHand.MAIN_HAND, (BlockHitResult) hit);
					result = copy.useOn(ctx);
				} else {
					result = serverPlayer.gameMode.useItemOn(serverPlayer, level, copy, InteractionHand.MAIN_HAND, (BlockHitResult) hit);
				}
			}
			case ENTITY -> {
				if (!level.isClientSide) {
					ServerboundInteractPacket.createInteractionPacket(entityHit.getEntity(), player.isSecondaryUseActive(), InteractionHand.MAIN_HAND).handle(serverPlayer.connection);
					result = InteractionResult.CONSUME;
				}
			}
			case MISS -> {
			}
		}

		if (result == InteractionResult.PASS && !copy.isEmpty()) {
			if (level.isClientSide) {
				copy.use(level, player, InteractionHand.MAIN_HAND);
			} else {
				serverPlayer.gameMode.useItem(serverPlayer, level, copy, InteractionHand.MAIN_HAND);
				if (getUseDuration() > 0) {
					copy.finishUsingItem(level, player);
				}
			}
		}

		ItemStack now = player.getMainHandItem();
		tag = now.getOrCreateTagElement("SkillSlots");
		tag.remove("IsUsing");
		if (tag.isEmpty()) {
			now.getOrCreateTag().remove("SkillSlots");
			if (now.getOrCreateTag().isEmpty()) {
				now.setTag(null);
			}
		}
		if (!ItemStack.matches(item, now)) {
			SkillSlotsHandler handler = SkillSlotsHandler.of(player);
			if (handler.canPlaceItem(slot, now)) {
				handler.setItem(slot, now);
			} else {
				handler.setItem(slot, ItemStack.EMPTY);
				player.addItem(now);
			}
		}
		inventory.items.set(inventory.selected, prev);
		if (!level.isClientSide) {
			wasOnCooldown = player.getCooldowns().isOnCooldown(item.getItem());
		}
	}

	@Override
	public int getUseDuration() {
		CompoundTag tag = item.getTagElement("SkillSlots");
		if (tag != null && tag.contains("UseDuration", Tag.TAG_ANY_NUMERIC)) {
			return tag.getInt("UseDuration");
		}
		if (item.getItem() instanceof InstrumentItem || item.getItem() instanceof ThrowablePotionItem) {
			return 0;
		}
		return super.getUseDuration();
	}

	@Override
	public boolean canBeToggled() {
		CompoundTag tag = item.getTagElement("SkillSlots");
		if (tag != null && tag.contains("CanBeToggled", Tag.TAG_BYTE)) {
			return tag.getBoolean("CanBeToggled");
		}
		return false;
	}


	@Override
	public boolean canUse(Player player) {
		return !player.getCooldowns().isOnCooldown(item.getItem());
	}

	@Override
	public @Nullable Holder<SoundEvent> getChargeCompleteSound() {
		CompoundTag tag = item.getTagElement("SkillSlots");
		if (tag != null && tag.contains("ChargeCompleteSound", Tag.TAG_STRING)) {
			String s = tag.getString("ChargeCompleteSound");
			if (s.isEmpty()) {
				return null;
			}
			ResourceLocation id = ResourceLocation.tryParse(s);
			if (id != null) {
				return Holder.direct(SoundEvent.createVariableRangeEvent(id));
			}
		}
		return super.getChargeCompleteSound();
	}
}
