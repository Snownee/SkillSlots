package snownee.skillslots.network;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;
import snownee.skillslots.SkillSlotsCommonConfig;
import snownee.skillslots.SkillSlotsHandler;
import snownee.skillslots.SkillSlotsModule;
import snownee.skillslots.menu.PlaceMenu;

@KiwiPacket("open_container")
public class COpenContainerPacket extends PacketHandler {
	public static COpenContainerPacket I;

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor, FriendlyByteBuf buf, ServerPlayer sender) {
		if (!SkillSlotsCommonConfig.playerCustomizable)
			return null;
		return executor.apply(() -> {
			SkillSlotsModule.sync(sender);
			SkillSlotsHandler handler = SkillSlotsHandler.of(sender);
			if (handler.getContainerSize() == 0) {
				sender.displayClientMessage(Component.translatable("msg.skillslots.noSlots"), true);
			} else {
				sender.openMenu(PlaceMenu.ContainerProvider.INSTANCE);
			}
		});
	}

}
