package org.darkstorm.runescape.aurora.wrapper;

import ms.aurora.rt3.*;

import org.darkstorm.runescape.api.wrapper.NPC;
import org.darkstorm.runescape.aurora.GameContextImpl;

public class NPCWrapper extends CharacterWrapper implements NPC {
	private Npc handle;

	public NPCWrapper(GameContextImpl context, Npc handle) {
		super(context, handle);
		this.handle = handle;
	}

	@Override
	public int getId() {
		NpcComposite definition = handle.getComposite();
		if(definition == null)
			return -1;
		return definition.getId();
	}

	@Override
	public String getName() {
		NpcComposite definition = handle.getComposite();
		if(definition == null)
			return null;
		return definition.getName();
	}

	@Override
	public boolean isCombatEntity() {
		return false;
	}

	@Override
	public String[] getActions() {
		return new String[0];
	}

	@Override
	public int getLevel() {
		// NpcComposite definition = handle.getComposite();
		// if(definition == null)
		return -1;
		// return definition.getCombatLevel();
	}
}
