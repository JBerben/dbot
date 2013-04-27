package org.darkstorm.runescape.aurora;

import java.awt.Frame;

import org.darkstorm.runescape.*;

public class DarkBotAurora extends AbstractDarkBot {
	private final ms.aurora.api.script.Script script;

	public DarkBotAurora(ms.aurora.api.script.Script script) {
		this.script = script;
	}

	@Override
	public Bot createBot(GameType type) {
		if(type.equals(GameType.OLDSCHOOL))
			return new AuroraBot(this, script.getSession());
		return null;
	}

	@Override
	public Frame getFrame() {
		return null;
	}
}
