package snownee.skillslots.client;

import java.util.function.IntUnaryOperator;

import org.apache.commons.lang3.mutable.MutableInt;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import snownee.skillslots.skill.SimpleSkill;
import snownee.skillslots.util.ClientProxy;

public class SimpleSkillClientHandler implements SkillClientHandler<SimpleSkill> {
	@Override
	public void renderGUI(SimpleSkill skill, GuiGraphics graphics, float xCenter, float yCenter, float scale, float alpha, int textColor, MutableInt textYOffset) {
		Minecraft mc = Minecraft.getInstance();
		CompoundTag tag = skill.item.getTagElement("SkillSlots");
		if (alpha > 0.3F) {
			float yCenter2 = yCenter - 6 * (1 + 0.125f * scale);
			scale = 1.5F + scale * 0.25F;
			if (tag != null && tag.contains("IconScale", Tag.TAG_ANY_NUMERIC)) {
				scale *= tag.getFloat("IconScale");
			}
			graphics.pose().pushPose();
			graphics.pose().translate(xCenter, yCenter2, 0);
			graphics.pose().scale(scale, scale, 1);
			graphics.pose().translate(-8, -8, 0);
			graphics.renderItem(mc.player, skill.item, 0, 0, 0);
			graphics.pose().popPose();
		}

		int count = skill.item.getCount();
		if (tag != null && tag.contains("AlternativeAmount", Tag.TAG_ANY_NUMERIC)) {
			count = tag.getInt("AlternativeAmount");
		}
		if (count != 1) {
			graphics.pose().pushPose();
			graphics.pose().translate(xCenter, yCenter + 10, 300);
			graphics.pose().scale(0.75f, 0.75f, 0.75f);
			graphics.drawString(mc.font, Integer.toString(skill.item.getCount()), 6, -12, textColor);
			graphics.pose().popPose();
		}
	}

	@Override
	public void pickColor(SimpleSkill skill, IntUnaryOperator saturationModifier) {
		skill.color = saturationModifier.applyAsInt(ClientProxy.pickItemColor(skill.item, skill.color));
	}

	@Override
	public float getDisplayChargeProgress(SimpleSkill skill, Player player, float pTicks) {
		int duration = skill.getChargeDuration(player);
		if (duration == 0) {
			float progress = player.getCooldowns().getCooldownPercent(skill.item.getItem(), pTicks);
			return 1 - progress;
		}
		return SkillClientHandler.super.getDisplayChargeProgress(skill, player, pTicks);
	}
}
