package org.darkstorm.runescape.aurora;

import org.darkstorm.runescape.api.Settings;

public class SettingsImpl extends AbstractUtility implements Settings {

	public SettingsImpl(GameContextImpl context) {
		super(context);
	}

	@Override
	public int get(int setting) {
		return client.getWidgetSettings()[setting];
	}

	@Override
	public int getCount() {
		return client.getWidgetSettings().length;
	}
}
