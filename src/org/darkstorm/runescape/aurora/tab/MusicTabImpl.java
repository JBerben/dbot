package org.darkstorm.runescape.aurora.tab;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.tab.MusicTab;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;
import org.darkstorm.runescape.util.GameTypeSupport;

public class MusicTabImpl extends AbstractTab implements MusicTab {
	public MusicTabImpl(GameContext context) {
		super(context, "Music Player", 36);
	}

	@Override
	public String getCurrentTrack() {
		return null;
	}

	@Override
	@GameTypeSupport(GameType.OLDSCHOOL)
	public PlayMode getPlayMode() {
		return null;
	}

	@Override
	@GameTypeSupport(GameType.OLDSCHOOL)
	public void setPlayMode(PlayMode mode) {
	}

	@Override
	public void scrollTo(String track) {
	}

	@Override
	public void playTrack(String track) {
	}

	@Override
	public boolean isUnlocked(String track) {
		return false;
	}

	@Override
	public String[] getUnlockedTracks() {
		return null;
	}

	@Override
	public String[] getTracks() {
		return null;
	}

	@Override
	public int getUnlockedTrackCount() {
		return 0;
	}

	@Override
	public int getTrackCount() {
		return 0;
	}

	@Override
	public InterfaceComponent getComponent(String track) {
		return null;
	}

	@Override
	@GameTypeSupport(GameType.OLDSCHOOL)
	public InterfaceComponent getComponent(PlayMode mode) {
		return null;
	}
}
