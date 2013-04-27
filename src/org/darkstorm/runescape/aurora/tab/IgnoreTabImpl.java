package org.darkstorm.runescape.aurora.tab;

import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.tab.IgnoreTab;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;

public class IgnoreTabImpl extends AbstractTab implements IgnoreTab {
	public IgnoreTabImpl(GameContext context) {
		super(context, "Ignore List", 32);
	}

	@Override
	public boolean isIgnoring(String name) {
		return false;
	}

	@Override
	public void addIgnore(String name) {
	}

	@Override
	public void removeIgnore(String name) {
	}

	@Override
	public InterfaceComponent getComponent(String name) {
		return null;
	}
}
