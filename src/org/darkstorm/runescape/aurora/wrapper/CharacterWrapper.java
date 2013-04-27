package org.darkstorm.runescape.aurora.wrapper;

import ms.aurora.rt3.*;

import org.darkstorm.runescape.api.util.Tile;
import org.darkstorm.runescape.api.wrapper.Character;
import org.darkstorm.runescape.aurora.GameContextImpl;

public abstract class CharacterWrapper extends AnimableWrapper implements
		Character {
	private ms.aurora.rt3.Character handle;

	public CharacterWrapper(GameContextImpl context,
			ms.aurora.rt3.Character handle) {
		super(context, handle);
		this.handle = handle;
	}

	@Override
	public double getRotation() {
		return handle.getTurnDirection();
	}

	@Override
	public Tile[] getWaypoints() {
		/*int[] waypointsX = handle.getWalkingQueueX().clone();
		int[] waypointsY = handle.getWalkingQueueY().clone();
		int plane = context.getClient().getPlane();
		if(waypointsX.length != waypointsY.length)*/
		return new Tile[0];
		/*Tile[] tiles = new Tile[waypointsX.length];
		for(int i = 0; i < tiles.length; i++)
			tiles[i] = new Tile(waypointsX[i], waypointsY[i], plane);
		return tiles;*/
	}

	@Override
	public String getOverheadMessage() {
		return handle.getMessage();
	}

	@Override
	public int getAnimation() {
		return handle.getAnimation();
	}

	@Override
	public boolean isInCombat() {
		return context.getClient().getLoopCycle() < handle.getLoopCycleStatus();
	}

	@Override
	public int getHealth() {
		return handle.getCurrentHealth();
	}

	@Override
	public int getMaxHealth() {
		return handle.getMaxHealth();
	}

	@Override
	public int getHealthPercentage() {
		return (int) (handle.getCurrentHealth()
				/ (double) handle.getMaxHealth() * 100);
	}

	@Override
	public boolean isDead() {
		return handle.getCurrentHealth() <= 0;
	}

	@Override
	public int getMotion() {
		return -1;// handle.getWalkAnimation();
	}

	@Override
	public boolean isMoving() {
		return handle.getPathLength() > 0;
	}

	@Override
	public Character getInteractionTarget() {
		int interacting = handle.getInteractingEntity();
		if(interacting == -1)
			return null;
		if(interacting < 32768) {
			Npc[] npcs = context.getClient().getAllNpcs();
			if(npcs == null || interacting >= npcs.length)
				return null;
			Npc npc = npcs[interacting];
			if(npc == null)
				return null;
			return new NPCWrapper(context, npc);
		}
		Player[] players = context.getClient().getAllPlayers();
		if(players == null || interacting - 32768 >= players.length)
			return null;
		Player player = players[interacting - 32768];
		if(player == null)
			return null;
		return new PlayerWrapper(context, player);
	}

	@Override
	public abstract int getLevel();

	@Override
	public Tile getLocation() {
		double x = context.getGame().getRegionBaseX()
				+ (handle.getLocalX() / 128D);
		double y = context.getGame().getRegionBaseY()
				+ (handle.getLocalY() / 128D);
		return new Tile(x, y, context.getClient().getPlane());
	}
}
