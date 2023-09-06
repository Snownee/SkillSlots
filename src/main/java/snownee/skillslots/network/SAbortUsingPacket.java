package snownee.skillslots.network;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.KiwiPacket.Direction;
import snownee.kiwi.network.PacketHandler;
import snownee.skillslots.SkillSlotsHandler;
import snownee.skillslots.client.gui.UseScreen;
import snownee.skillslots.util.ClientProxy;

@KiwiPacket(value = "abort_using", dir = Direction.PLAY_TO_CLIENT)
public class SAbortUsingPacket extends PacketHandler {
	public static SAbortUsingPacket I;

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor, FriendlyByteBuf buf, ServerPlayer sender) {
		return executor.apply(() -> {
			Player player = ClientProxy.getPlayer();
			if (player == null) {
				return;
			}
			SkillSlotsHandler handler = SkillSlotsHandler.of(player);
			handler.abortUsing();
			Minecraft mc = Minecraft.getInstance();
			if (mc.screen instanceof UseScreen) {
				mc.setScreen(null);
			}
		});
	}

}
