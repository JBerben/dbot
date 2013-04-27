package org.darkstorm.runescape.aurora.tab;

import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.tab.MagicTab;
import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.api.wrapper.*;

public class MagicTabImpl extends AbstractTab implements MagicTab {
	public MagicTabImpl(GameContext context) {
		super(context, "Magic", 53);
	}

	@Override
	public Spellbook getSpellbook() {
		return null;
	}

	@Override
	public Spell getSelectedSpell() {
		return null;
	}

	@Override
	public void selectSpell(Spell spell) {
	}

	@Override
	public boolean canCast(Spell spell) {
		return false;
	}

	@Override
	public Item[] getRequiredItems(Spell spell) {
		return null;
	}

	@Override
	public InterfaceComponent getComponent(Spell spell) {
		return null;
	}
}
