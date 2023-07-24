package snownee.skillslots;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Categories;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.loader.event.ClientInitEvent;
import snownee.kiwi.loader.event.InitEvent;
import snownee.skillslots.item.UnlockSlotItem;
import snownee.skillslots.menu.PlaceMenu;
import snownee.skillslots.network.SAbortUsingPacket;
import snownee.skillslots.network.SSyncSlotsPacket;
import snownee.skillslots.skill.SimpleSkill;
import snownee.skillslots.util.ClientProxy;
import snownee.skillslots.util.CommonProxy;

@KiwiModule(SkillSlots.ID)
public class SkillSlotsModule extends AbstractModule {
	public static final TagKey<Item> SKILL = itemTag(SkillSlots.ID, "skill");
	@KiwiModule.Category(Categories.TOOLS_AND_UTILITIES)
	public static final KiwiGO<UnlockSlotItem> UNLOCK_SLOT = go(UnlockSlotItem::new);
	public static final KiwiGO<MenuType<PlaceMenu>> PLACE = go(() -> new MenuType<>(PlaceMenu::new, FeatureFlags.VANILLA_SET));
	public static final KiwiGO<SoundEvent> HOVER_SOUND = go(() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(SkillSlots.ID, "hover")));
	public static final KiwiGO<SoundEvent> POTION_CHARGE_COMPLETE_SOUND = go(() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(SkillSlots.ID, "potion_charge_complete")));
	public static final KiwiGO<SoundEvent> USE_SHORT_SOUND = go(() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(SkillSlots.ID, "use_short")));
	public static final KiwiGO<SoundEvent> USE_LONG_SOUND = go(() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(SkillSlots.ID, "use_long")));

	public static void sync(ServerPlayer player) {
		if (CommonProxy.isFakePlayer(player)) {
			return;
		}
		SSyncSlotsPacket.send(player);
	}

	public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(SkillSlotsCommand.init(dispatcher));
	}

	public static void playerLoggedIn(ServerPlayer player) {
		sync(player);
	}

	public static void causeDamage(DamageSource source, LivingEntity target, float amount) {
		if (target.level().isClientSide) {
			return;
		}
		Entity entity = source.getEntity();
		if (entity == null) {
			return;
		}

		if (SkillSlotsCommonConfig.interruptedOnHurt && target instanceof ServerPlayer) {
			SkillSlotsHandler targetHandler = SkillSlotsHandler.of(target);
			if (targetHandler != null) {
				targetHandler.abortUsing();
				SAbortUsingPacket.I.send((ServerPlayer) target, $ -> {
				});
			}
		}

		if (SkillSlotsCommonConfig.damageAcceleration > 0 && entity instanceof ServerPlayer sourceEntity) {
			SkillSlotsHandler.of(sourceEntity).accelerate(.05f * amount * (float) SkillSlotsCommonConfig.damageAcceleration);
		}
	}

	public static void clonePlayer(Player original, Player clone) {
		SkillSlotsHandler newHandler = SkillSlotsHandler.of(clone);
		SkillSlotsHandler oldHandler = SkillSlotsHandler.of(original);
		newHandler.copyFrom(oldHandler);
	}

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> SkillSlots.SKILL_FACTORIES.add(SimpleSkill::new));
	}

	@Override
	protected void clientInit(ClientInitEvent event) {
		event.enqueueWork(ClientProxy::loadComplete);
	}


}
