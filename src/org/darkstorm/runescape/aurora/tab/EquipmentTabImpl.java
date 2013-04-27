package org.darkstorm.runescape.aurora.tab;

import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.tab.EquipmentTab;
import org.darkstorm.runescape.api.wrapper.*;

public class EquipmentTabImpl extends AbstractTab implements EquipmentTab {
	public EquipmentTabImpl(GameContext context) {
		super(context, "Worn Equipment", 51);
	}

	@Override
	public Item getItem(EquipmentSlot slot) {
		return null;
	}

	@Override
	public boolean hasItem(EquipmentSlot slot) {
		return false;
	}

	@Override
	public void removeItem(EquipmentSlot slot) {
	}

	@Override
	public InterfaceComponent getComponent(EquipmentSlot slot) {
		return null;
	}
}
