package org.darkstorm.runescape.aurora.tab;

import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.tab.QuestTab;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;

public class QuestTabImpl extends AbstractTab implements QuestTab {
	public QuestTabImpl(GameContext context) {
		super(context, "Quest List", 49);
	}

	@Override
	public void scrollTo(String quest) {
	}

	@Override
	public void openQuestWindow(String quest) {
	}

	@Override
	public boolean hasStarted(String quest) {
		return false;
	}

	@Override
	public boolean hasCompleted(String quest) {
		return false;
	}

	@Override
	public InterfaceComponent getComponent(String quest) {
		return null;
	}
}
