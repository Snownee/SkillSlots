package snownee.skillslots.util;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.ItemStack;
import snownee.kiwi.loader.Platform;
import snownee.skillslots.SkillSlotsModule;
import snownee.skillslots.client.SimpleSkillClientHandler;
import snownee.skillslots.client.SkillSlotsClient;
import snownee.skillslots.client.gui.PlaceScreen;
import snownee.skillslots.compat.JEIPlugin;
import snownee.skillslots.skill.SimpleSkill;

public class ClientProxy implements ClientModInitializer {
	private static final boolean hasJEI = Platform.isModLoaded("jei");

	public static int pickItemColor(ItemStack stack, int fallback) {
		ItemColor itemColor = ColorProviderRegistry.ITEM.get(stack.getItem());
		if (itemColor != null) {
			return itemColor.getColor(stack, 0);
		}
		if (hasJEI) {
			return JEIPlugin.pickItemColor(stack, fallback);
		}
		return fallback;
	}

	public static void loadComplete() {
		MenuScreens.register(SkillSlotsModule.PLACE.get(), PlaceScreen::new);
		SkillSlotsClient.registerClientHandler(SimpleSkill.class, new SimpleSkillClientHandler());
		SkillSlotsClient.registerItemColors(ColorProviderRegistry.ITEM::register);
	}

	private static void onKeyInput(Minecraft mc) {
		SkillSlotsClient.onKeyInput();
	}

	public static InputConstants.Key getBoundKeyOf(KeyMapping keyMapping) {
		return KeyBindingHelper.getBoundKeyOf(keyMapping);
	}

	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(SkillSlotsClient.kbOpen);
		for (KeyMapping kbUse : SkillSlotsClient.kbUses) {
			KeyBindingHelper.registerKeyBinding(kbUse);
		}
		ClientTickEvents.END_CLIENT_TICK.register(ClientProxy::onKeyInput);
	}
}
