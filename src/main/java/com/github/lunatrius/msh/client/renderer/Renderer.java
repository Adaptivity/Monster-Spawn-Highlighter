package com.github.lunatrius.msh.client.renderer;

import com.github.lunatrius.core.util.vector.Vector4i;
import com.github.lunatrius.msh.MonsterSpawnHighlighter;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

public class Renderer {
	private MonsterSpawnHighlighter msh = MonsterSpawnHighlighter.instance;
	private Minecraft minecraft = null;
	private final int[] list = new int[] {
			-1, -1
	};

	public Renderer(Minecraft minecraft) {
		this.minecraft = minecraft;
		compileList();
	}

	private void compileList() {
		this.list[0] = GL11.glGenLists(1);
		GL11.glNewList(this.list[0], GL11.GL_COMPILE);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3f(0.1f, 0.03f, 0.1f);
		GL11.glVertex3f(0.1f, 0.03f, 0.9f);
		GL11.glVertex3f(0.9f, 0.03f, 0.9f);
		GL11.glVertex3f(0.9f, 0.03f, 0.1f);
		GL11.glEnd();

		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(0.1f, 0.03f, 0.1f);
		GL11.glVertex3f(0.1f, 0.03f, 0.9f);
		GL11.glVertex3f(0.9f, 0.03f, 0.9f);
		GL11.glVertex3f(0.9f, 0.03f, 0.1f);
		GL11.glEnd();
		GL11.glEndList();

		this.list[1] = GL11.glGenLists(1);
		GL11.glNewList(this.list[1], GL11.GL_COMPILE);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3f(0.1f, 0.03f, 0.1f);
		GL11.glVertex3f(0.1f, 0.03f, 0.9f);
		GL11.glVertex3f(0.9f, 0.03f, 0.9f);
		GL11.glVertex3f(0.9f, 0.03f, 0.1f);
		GL11.glEnd();

		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3f(0.1f, 0.03f, 0.1f);
		GL11.glVertex3f(0.1f, 0.03f, 0.9f);
		GL11.glVertex3f(0.9f, 0.03f, 0.9f);
		GL11.glVertex3f(0.9f, 0.03f, 0.1f);
		GL11.glEnd();

		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3f(0.5f, 0.0f, 0.5f);
		GL11.glVertex3f(0.5f, this.msh.config.guideLength, 0.5f);
		GL11.glEnd();
		GL11.glEndList();
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		if (this.minecraft != null && this.msh.config.renderSpawns != 0) {
			EntityPlayerSP player = this.minecraft.thePlayer;
			if (player != null) {
				this.msh.playerPosition.x = (float) (player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks);
				this.msh.playerPosition.y = (float) (player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks);
				this.msh.playerPosition.z = (float) (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks);

				render();
			}
		}
	}

	private void render() {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);

		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);

		GL11.glLineWidth(2.0f);

		GL11.glTranslatef(-this.msh.playerPosition.x, -this.msh.playerPosition.y, -this.msh.playerPosition.z);

		Vector4i blockPos;
		float delta;
		Block block;
		for (int i = 0; i < this.msh.spawnList.size(); i++) {
			blockPos = this.msh.spawnList.get(i);

			switch (blockPos.w) {
			case 1:
				this.msh.config.glColorDay();
				break;

			case 2:
				this.msh.config.glColorNight();
				break;

			case 3:
				this.msh.config.glColorBoth();
				break;
			}

			delta = 0.0f;
			block = this.minecraft.theWorld.getBlock(blockPos.x, blockPos.y, blockPos.z);
			if (block != null) {
				if (block == Blocks.snow || block == Blocks.wooden_pressure_plate || block == Blocks.stone_pressure_plate || block == Blocks.light_weighted_pressure_plate || block == Blocks.heavy_weighted_pressure_plate) {
					block.setBlockBoundsBasedOnState(this.minecraft.theWorld, blockPos.x, blockPos.y, blockPos.z);
					delta = (float) block.getBlockBoundsMaxY();
				}
			}

			GL11.glTranslatef(blockPos.x, blockPos.y + delta, blockPos.z);
			GL11.glCallList(this.list[this.msh.config.renderSpawns == 1 ? 0 : 1]);
			GL11.glTranslatef(-blockPos.x, -(blockPos.y + delta), -blockPos.z);
		}

		GL11.glTranslatef(this.msh.playerPosition.x, this.msh.playerPosition.y, this.msh.playerPosition.z);

		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}
}
