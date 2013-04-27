package org.darkstorm.runescape.aurora.tab;

import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.tab.InventoryTab;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;

public class InventoryTabImpl extends AbstractTab implements InventoryTab {
	public InventoryTabImpl(GameContext context) {
		super(context, "Inventory", 50);
	}

	@Override
	public InterfaceComponent getInventoryArea() {
		return context.getInterfaces().getComponent(149, 0);
	}
}
