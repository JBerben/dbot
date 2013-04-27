package org.darkstorm.runescape.aurora.tab;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.tab.PrayerTab;
import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;
import org.darkstorm.runescape.util.GameTypeSupport;

public class PrayerTabImpl extends AbstractTab implements PrayerTab {
	public PrayerTabImpl(GameContext context) {
		super(context, "Prayer", 52);
	}

	@Override
	public boolean isPraying() {
		return false;
	}

	@Override
	public PrayerSet getPrayerSet() {
		return null;
	}

	@Override
	public Prayer[] getEnabledPrayers() {
		return null;
	}

	@Override
	public boolean isPraying(Prayer prayer) {
		return false;
	}

	@Override
	public boolean canPray(Prayer prayer) {
		return false;
	}

	@Override
	public void disablePrayers() {
	}

	@Override
	public void setPraying(Prayer prayer, boolean praying) {
	}

	@Override
	@GameTypeSupport(GameType.CURRENT)
	public Prayer[] getQuickPrayers() {
		return null;
	}

	@Override
	@GameTypeSupport(GameType.CURRENT)
	public void setQuickPrayers(Prayer... prayers) {
	}

	@Override
	@GameTypeSupport(GameType.CURRENT)
	public boolean isSelectingQuickPrayers() {
		return false;
	}

	@Override
	@GameTypeSupport(GameType.CURRENT)
	public void completeQuickPrayerSelection() {
	}

	@Override
	public InterfaceComponent getComponent(Prayer prayer) {
		return null;
	}
}
