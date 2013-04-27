package org.darkstorm.runescape.aurora.tab;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.input.Mouse;
import org.darkstorm.runescape.api.tab.SkillTab;
import org.darkstorm.runescape.api.util.Skill;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;
import org.darkstorm.runescape.util.UnsupportedGameTypeException;

public class SkillTabImpl extends AbstractTab implements SkillTab {
	public SkillTabImpl(GameContext context) {
		super(context, "Stats", 48);
	}

	@Override
	public InterfaceComponent getComponent(Skill skill) {
		return context.getInterfaces().getComponent(320, getSkillIndex(skill));
	}

	private int getSkillIndex(Skill skill) {
		switch(skill) {
		case ATTACK:
			return 2;
		case CONSTITUTION:
			return 4;
		case MINING:
			return 6;
		case STRENGTH:
			return 9;
		case AGILITY:
			return 11;
		case SMITHING:
			return 13;
		case DEFENSE:
			return 16;
		case HERBLORE:
			return 18;
		case FISHING:
			return 20;
		case RANGED:
			return 23;
		case THIEVING:
			return 25;
		case COOKING:
			return 27;
		case PRAYER:
			return 30;
		case CRAFTING:
			return 32;
		case FIREMAKING:
			return 33;
		case MAGIC:
			return 37;
		case FLETCHING:
			return 39;
		case WOODCUTTING:
			return 41;
		case RUNECRAFTING:
			return 104;
		case SLAYER:
			return 109;
		case FARMING:
			return 114;
		case CONSTRUCTION:
			return 44;
		case HUNTER:
			return 119;
		default:
			throw new UnsupportedGameTypeException(GameType.CURRENT);
		}
	}

	@Override
	public void openSkillWindow(Skill skill) {
		InterfaceComponent component = getComponent(skill);
		if(!isOpen() || component == null || component.getBounds() == null)
			return;
		Mouse mouse = context.getMouse();
		mouse.click(component);
		mouse.await();
	}

	@Override
	public int getTotalLevel() {
		return 0;
	}
}
