package org.darkstorm.runescape.aurora.tab;

import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.tab.ClanChatTab;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;

public class ClanChatTabImpl extends AbstractTab implements ClanChatTab {
	public ClanChatTabImpl(GameContext context) {
		super(context, "Clan Chat", 30);
	}

	@Override
	public boolean isInChat() {
		return false;
	}

	@Override
	public String getCurrentChat() {
		return null;
	}

	@Override
	public void joinChat(String name) {
	}

	@Override
	public ChatMember getChatMember(String name) {
		return null;
	}

	@Override
	public ChatMember[] getChatMembers() {
		return null;
	}

	@Override
	public void leaveChat() {
	}

	@Override
	public void addUserToChat(String name) {
	}

	@Override
	public InterfaceComponent getMemberComponent(ChatMember member) {
		return null;
	}
}
