package org.darkstorm.runescape.aurora.tab;

import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.tab.FriendTab;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;

public class FriendTabImpl extends AbstractTab implements FriendTab {
	public FriendTabImpl(GameContext context) {
		super(context, "Friends List", 31);
	}

	@Override
	public boolean isFriend(String name) {
		return false;
	}

	@Override
	public Friend getFriend(String name) {
		return null;
	}

	@Override
	public void scrollTo(Friend friend) {
	}

	@Override
	public Friend[] getFriends() {
		return null;
	}

	@Override
	public Friend[] getOnlineFriends() {
		return null;
	}

	@Override
	public void sendMessage(Friend friend, String name) {
	}

	@Override
	public void addFriend(String name) {
	}

	@Override
	public void removeFriend(Friend friend) {
	}

	@Override
	public InterfaceComponent getComponent(Friend friend) {
		return null;
	}
}
