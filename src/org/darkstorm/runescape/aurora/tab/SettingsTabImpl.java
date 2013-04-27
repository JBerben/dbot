package org.darkstorm.runescape.aurora.tab;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.api.*;
import org.darkstorm.runescape.api.input.Mouse;
import org.darkstorm.runescape.api.tab.SettingsTab;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;
import org.darkstorm.runescape.util.GameTypeSupport;

public class SettingsTabImpl extends AbstractTab implements SettingsTab {
	public SettingsTabImpl(GameContext context) {
		super(context, "Options", 34);
	}

	@Override
	@GameTypeSupport(GameType.OLDSCHOOL)
	public int getRunPercentage() {
		return 0;
	}

	@Override
	@GameTypeSupport(GameType.OLDSCHOOL)
	public boolean isRunning() {
		return context.getSettings().get(173) == 1;
	}

	@Override
	@GameTypeSupport(GameType.OLDSCHOOL)
	public void setRunning(boolean running) {
		if(!isOpen() || running == isRunning())
			return;
		InterfaceComponent component = getRunButtonComponent();
		if(component == null || component.getBounds() == null)
			return;
		Mouse mouse = context.getMouse();
		mouse.click(component);
		mouse.await();
	}

	@Override
	@GameTypeSupport(GameType.OLDSCHOOL)
	public InterfaceComponent getRunButtonComponent() {
		return context.getInterfaces().getComponent(261, 40);
	}

	@Override
	@GameTypeSupport(GameType.OLDSCHOOL)
	public int getSliderPercentage(SettingSlider slider) {
		Settings settings = context.getSettings();
		int current, max;
		switch(slider) {
		case BRIGHTNESS:
			current = settings.get(166) - 1;
			max = 3;
			break;
		case MUSIC:
			current = 4 - settings.get(168);
			max = 4;
		case AUDIO:
			current = 4 - settings.get(169);
			max = 4;
		case AMBIENT:
			current = 4 - settings.get(872);
			max = 4;
		default:
			return -1;
		}
		return (int) (100 * (current / (double) max));
	}

	@Override
	@GameTypeSupport(GameType.OLDSCHOOL)
	public void setSliderPercentage(SettingSlider slider, int percentage) {
		if(slider == SettingSlider.BRIGHTNESS) {

		} else {

		}
	}

	@Override
	@GameTypeSupport(GameType.OLDSCHOOL)
	public InterfaceComponent getSliderComponent(SettingSlider slider) {
		return null;
	}
}
