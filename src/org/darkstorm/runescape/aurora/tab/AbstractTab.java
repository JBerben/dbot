package org.darkstorm.runescape.aurora.tab;

import java.awt.Rectangle;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.input.Mouse;
import org.darkstorm.runescape.api.tab.Tab;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;
import org.darkstorm.runescape.util.*;

abstract class AbstractTab implements Tab {
	protected final GameContext context;

	private final String name;
	private final int interfaceId;

	public AbstractTab(GameContext context, String name, int interfaceId) {
		this.context = context;
		this.name = name;
		this.interfaceId = interfaceId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public InterfaceComponent getButtonComponent() {
		return context.getInterfaces().getComponent(548, interfaceId);
	}

	@Override
	public void open() {
		if(isOpen())
			return;
		Mouse mouse = context.getMouse();
		mouse.click(getButtonComponent());
		mouse.await();
	}

	@Override
	@GameTypeSupport(GameType.CURRENT)
	public void close() {
		throw new UnsupportedGameTypeException(GameType.CURRENT);
	}

	@Override
	public boolean isOpen() {
		InterfaceComponent button = getButtonComponent();
		if(button == null)
			return false;
		System.out.println("Checking tab " + name + " texture id: "
				+ button.getTextureId());
		return button.getTextureId() != -1;
	}

	@Override
	public Rectangle getTabArea() {
		InterfaceComponent component = context.getInterfaces().getComponent(
				548, 95);
		if(component == null)
			return null;
		return component.getBounds();
	}

	@Override
	public GameContext getContext() {
		return context;
	}
}
