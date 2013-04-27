package org.darkstorm.runescape.aurora;

import ms.aurora.rt3.Client;

import org.darkstorm.runescape.api.*;
import org.darkstorm.runescape.api.input.*;

abstract class AbstractUtility implements Utility {
	protected final AuroraBot bot;
	protected final GameContextImpl context;
	protected final Client client;

	protected final Calculations calculations;
	protected final Mouse mouse;
	protected final Keyboard keyboard;

	public AbstractUtility(GameContextImpl context) {
		bot = context.getBot();
		this.context = context;
		client = context.getClient();

		calculations = context.getCalculations();
		mouse = context.getMouse();
		keyboard = context.getKeyboard();
	}

	@Override
	public GameContext getContext() {
		return context;
	}
}
