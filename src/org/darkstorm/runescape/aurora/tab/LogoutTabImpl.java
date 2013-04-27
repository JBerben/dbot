package org.darkstorm.runescape.aurora.tab;

import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.input.Mouse;
import org.darkstorm.runescape.api.tab.LogoutTab;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;

public class LogoutTabImpl extends AbstractTab implements LogoutTab {
	public LogoutTabImpl(GameContext context) {
		super(context, "Logout", 33);
	}

	@Override
	public InterfaceComponent getLogoutButtonComponent() {
		return context.getInterfaces().getComponent(182, 6);
	}

	@Override
	public void logout() {
		if(!isOpen())
			return;
		InterfaceComponent component = getLogoutButtonComponent();
		if(component == null || component.getBounds() == null)
			return;
		Mouse mouse = context.getMouse();
		mouse.click(component);
		mouse.await();
	}
}
