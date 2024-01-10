package snownee.skillslots.util;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import snownee.kiwi.Mod;
import snownee.skillslots.SkillSlots;
import snownee.skillslots.SkillSlotsModule;

@Mod(SkillSlots.ID)
public class CommonProxy implements ModInitializer {

	public static boolean isFakePlayer(Player player) {
		return false;
	}

	public static double getReachDistance(Player player) {
		return player.isCreative() ? 6 : 3;
	}

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(this::registerCommands);
		ServerPlayerEvents.COPY_FROM.register(this::clonePlayer);
	}

	private void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
		SkillSlotsModule.registerCommands(dispatcher);
	}

	private void clonePlayer(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
		SkillSlotsModule.clonePlayer(oldPlayer, newPlayer);
	}
}
