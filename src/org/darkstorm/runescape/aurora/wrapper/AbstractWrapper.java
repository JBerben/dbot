package org.darkstorm.runescape.aurora.wrapper;

import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.wrapper.Wrapper;
import org.darkstorm.runescape.aurora.GameContextImpl;

abstract class AbstractWrapper implements Wrapper {
	protected final GameContextImpl context;

	public AbstractWrapper(GameContextImpl context) {
		this.context = context;
	}

	@Override
	public GameContext getContext() {
		return context;
	}
}
