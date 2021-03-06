package com.github.lunatrius.msh.client.gui;

import com.github.lunatrius.msh.client.ClientProxy;
import com.github.lunatrius.msh.entity.SpawnCondition;
import com.github.lunatrius.msh.lib.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLiving;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GuiMonsterSpawnHighlighterSlot extends GuiSlot {
	private final GuiMonsterSpawnHighlighter guiMonsterSpawnHighlighter;
	private final TextureManager renderEngine;
	private final FontRenderer fontRenderer;
	private final List<SpawnCondition> spawnConditions;

	public GuiMonsterSpawnHighlighterSlot(Minecraft minecraft, GuiMonsterSpawnHighlighter guiMonsterSpawnHighlighter) {
		super(minecraft, guiMonsterSpawnHighlighter.width, guiMonsterSpawnHighlighter.height, 16, guiMonsterSpawnHighlighter.height - 30, 24);
		this.guiMonsterSpawnHighlighter = guiMonsterSpawnHighlighter;
		this.renderEngine = minecraft.renderEngine;
		this.fontRenderer = minecraft.fontRenderer;
		this.spawnConditions = SpawnCondition.SPAWN_CONDITIONS;
	}

	@Override
	protected int getSize() {
		return this.spawnConditions.size();
	}

	@Override
	protected void elementClicked(int index, boolean isDoubleClick, int a, int b) {
		if (index < 0 || index >= this.spawnConditions.size()) {
			return;
		}

		SpawnCondition spawnCondition = this.spawnConditions.get(index);
		spawnCondition.enabled = !spawnCondition.enabled;

		Reference.config.setEntityEnabled(spawnCondition.name, spawnCondition.enabled);
	}

	@Override
	protected boolean isSelected(int index) {
		return !(index < 0 || index >= this.spawnConditions.size()) && this.spawnConditions.get(index).enabled;

	}

	@Override
	protected void drawBackground() {
	}

	@Override
	protected void drawContainerBackground(Tessellator tessellator) {
	}

	@Override
	protected void drawSlot(int index, int x, int y, int par4, Tessellator tessellator, int a, int b) {
		if (index < 0 || index >= this.spawnConditions.size()) {
			return;
		}

		drawEntity(x, y, this.spawnConditions.get(index).entity);
		this.guiMonsterSpawnHighlighter.drawString(this.fontRenderer, this.spawnConditions.get(index).entity.func_145748_c_().getFormattedText(), x + 24, y + 6, 0x00FFFFFF);
	}

	private void drawEntity(int x, int y, EntityLiving entityLiving) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		Tessellator tess = Tessellator.instance;

		this.renderEngine.bindTexture(Gui.statIcons);
		drawEntitySlot(tess, x, y);

		TextureInformation ti = ClientProxy.ENTITY_ICONS.get(entityLiving.getClass());
		if (ti != null) {
			this.renderEngine.bindTexture(ti.resourceLocation);
			drawTextureParts(tess, x, y, ti);

			if (ti.resourceSpecial != null) {
				this.renderEngine.bindTexture(ti.resourceSpecial);
				drawTextureParts(tess, x, y, ti);
			}
		}
	}

	private void drawEntitySlot(Tessellator tess, int x, int y) {
		tess.startDrawingQuads();
		tess.addVertexWithUV(x + 1, y + 19, 0, 0 * 0.0078125, 18 * 0.0078125);
		tess.addVertexWithUV(x + 19, y + 19, 0, 18 * 0.0078125, 18 * 0.0078125);
		tess.addVertexWithUV(x + 19, y + 1, 0, 18 * 0.0078125, 0 * 0.0078125);
		tess.addVertexWithUV(x + 1, y + 1, 0, 0 * 0.0078125, 0 * 0.0078125);
		tess.draw();
	}

	private void drawTextureParts(Tessellator tess, int x, int y, TextureInformation ti) {
		tess.startDrawingQuads();
		for (TextureInformation.TexturePart tp : ti.textureParts) {
			tess.addVertexWithUV(x + 2 + tp.x, y + 2 + tp.y + tp.height, 0, tp.srcX / ti.width, (tp.srcY + tp.srcHeight) / ti.height);
			tess.addVertexWithUV(x + 2 + tp.x + tp.width, y + 2 + tp.y + tp.height, 0, (tp.srcX + tp.srcWidth) / ti.width, (tp.srcY + tp.srcHeight) / ti.height);
			tess.addVertexWithUV(x + 2 + tp.x + tp.width, y + 2 + tp.y, 0, (tp.srcX + tp.srcWidth) / ti.width, tp.srcY / ti.height);
			tess.addVertexWithUV(x + 2 + tp.x, y + 2 + tp.y, 0, tp.srcX / ti.width, tp.srcY / ti.height);
		}
		tess.draw();
	}
}
