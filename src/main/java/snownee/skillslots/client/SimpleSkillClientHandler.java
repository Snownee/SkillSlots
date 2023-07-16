package snownee.skillslots.client;

import java.util.function.IntUnaryOperator;

import org.apache.commons.lang3.mutable.MutableInt;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import snownee.skillslots.skill.SimpleSkill;
import snownee.skillslots.util.ClientProxy;

public class SimpleSkillClientHandler implements SkillClientHandler<SimpleSkill> {
	@Override
	public void renderGUI(SimpleSkill skill, PoseStack matrix, float xCenter, float yCenter, float scale, float alpha, int textColor, MutableInt textYOffset) {
		Font font = Minecraft.getInstance().font;
		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

		CompoundTag tag = skill.item.getTagElement("SkillSlots");
		if (alpha > 0.3F) {
			float yCenter2 = yCenter - 6 * (1 + 0.125f * scale);
			PoseStack modelViewStack = RenderSystem.getModelViewStack();
			modelViewStack.pushPose();
			modelViewStack.translate(xCenter, yCenter2, 0);
			scale = 1.5F + scale * 0.25F;
			if (tag != null && tag.contains("IconScale", Tag.TAG_ANY_NUMERIC)) {
				scale *= tag.getFloat("IconScale");
			}
			modelViewStack.scale(scale, scale, 1);
			modelViewStack.translate(-8, -8, 0);
			itemRenderer.renderAndDecorateItem(skill.item, 0, 0);
			modelViewStack.popPose();
			RenderSystem.applyModelViewMatrix();
		}

		int count = skill.item.getCount();
		if (tag != null && tag.contains("AlternativeAmount", Tag.TAG_ANY_NUMERIC)) {
			count = tag.getInt("AlternativeAmount");
		}
		if (count != 1) {
			matrix.pushPose();
			matrix.translate(xCenter, yCenter + 10, 300);
			matrix.scale(0.75f, 0.75f, 0.75f);
			GuiComponent.drawString(matrix, font, Integer.toString(skill.item.getCount()), 6, -12, textColor);
			matrix.popPose();
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
