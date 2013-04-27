package org.darkstorm.runescape.aurora.tab;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.input.Mouse;
import org.darkstorm.runescape.api.tab.CombatTab;
import org.darkstorm.runescape.api.wrapper.*;
import org.darkstorm.runescape.util.GameTypeSupport;

public class CombatTabImpl extends AbstractTab implements CombatTab {
	private class CombatStyleImpl implements CombatStyle {
		private final String name;
		private final int index, textureId;
		private final String[] descriptions;

		public CombatStyleImpl(String name, int index, int textureId,
				String[] descriptions) {
			this.name = name;
			this.index = index;
			this.textureId = textureId;
			this.descriptions = descriptions.clone();
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public int getIndex() {
			return index;
		}

		@Override
		public int getTextureId() {
			return textureId;
		}

		@Override
		public String[] getDescriptions() {
			return descriptions.clone();
		}

		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof CombatStyleImpl))
				return false;
			CombatStyleImpl style = (CombatStyleImpl) obj;
			if(descriptions.length != style.descriptions.length)
				return false;
			for(int i = 0; i < descriptions.length; i++)
				if((descriptions[i] == null && style.descriptions[i] == null)
						|| (descriptions[i] != null && style.descriptions[i] == null))
					return false;
				else if(descriptions[i] != null
						&& style.descriptions[i] != null
						&& !descriptions[i].equals(style.descriptions[i]))
					return false;
			return index == style.index && name.equals(style.name)
					&& textureId == style.textureId;
		}
	}

	public CombatTabImpl(GameContext context) {
		super(context, "Combat Options", 47);
	}

	@Override
	@GameTypeSupport(GameType.OLDSCHOOL)
	public CombatStyle[] getStyles() {
		Interface combat = context.getInterfaces().getInterface(81);
		if(combat == null)
			return new CombatStyle[0];
		CombatStyle[] styles = new CombatStyle[4];
		for(int i = 0; i < 4; i++) {
			InterfaceComponent nameComponent = combat.getComponent(i + 27);
			InterfaceComponent spriteComponent = combat.getComponent(i + 6);
			InterfaceComponent descriptionComponent = combat
					.getComponent(i + 32);
			if(nameComponent == null || spriteComponent == null
					|| descriptionComponent == null
					|| nameComponent.getBounds() == null
					|| spriteComponent.getBounds() == null
					|| descriptionComponent.getBounds() == null)
				continue;
			String name = nameComponent.getText();
			int textureId = spriteComponent.getTextureId();
			String[] descriptions = descriptionComponent.getText()
					.split("<br>");
			for(int j = 0; j < descriptions.length; j++)
				descriptions[j] = descriptions[j].replaceAll("[\\(\\)]", "");
			styles[i] = new CombatStyleImpl(name, i, textureId, descriptions);
		}
		return styles;
	}

	@Override
	@GameTypeSupport(GameType.OLDSCHOOL)
	public CombatStyle getSelectedStyle() {
		return getStyles()[context.getSettings().get(43)];
	}

	@Override
	@GameTypeSupport(GameType.OLDSCHOOL)
	public void selectStyle(CombatStyle style) {
		if(style.equals(getSelectedStyle()))
			return;
		InterfaceComponent component = context.getInterfaces().getComponent(81,
				style.getIndex() + 27);
		if(component == null || component.getBounds() == null)
			return;
		Mouse mouse = context.getMouse();
		mouse.click(component);
		mouse.await();
	}

	@Override
	public boolean isAutoRetaliating() {
		return context.getSettings().get(172) == 0;
	}

	@Override
	public void setAutoRetaliating(boolean autoRetaliate) {
		if(autoRetaliate == isAutoRetaliating())
			return;
		InterfaceComponent autoRetaliating = context.getInterfaces()
				.getComponent(81, 26);
		if(autoRetaliating == null || autoRetaliating.getBounds() == null)
			return;
		Mouse mouse = context.getMouse();
		mouse.click(autoRetaliating);
		mouse.await();
	}

	@Override
	public int getCombatLevel() {
		return context.getPlayers().getSelf().getLevel();
	}

	@Override
	public String getCurrentWeaponName() {
		InterfaceComponent component = context.getInterfaces().getComponent(81,
				0);
		if(component == null || component.getBounds() == null)
			return null;
		return component.getText();
	}

	@Override
	public boolean hasSpecialAttack() {
		return false;
	}

	@Override
	public int getSpecialAttackPercentage() {
		return 0;
	}

	@Override
	public void performSpecialAttack() {
	}
}
