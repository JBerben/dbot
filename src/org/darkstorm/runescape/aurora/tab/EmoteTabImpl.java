package org.darkstorm.runescape.aurora.tab;

import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.tab.EmoteTab;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;

public class EmoteTabImpl extends AbstractTab implements EmoteTab {
	public EmoteTabImpl(GameContext context) {
		super(context, "Emotes", 35);
	}

	@Override
	public String getEmote(int row, int column) {
		return null;
	}

	@Override
	public void performEmote(String name) {
	}

	@Override
	public boolean isUnlocked(String name) {
		return false;
	}

	@Override
	public InterfaceComponent getComponent(String name) {
		return null;
	}
}
