package org.darkstorm.runescape.aurora;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.api.Game;
import org.darkstorm.runescape.api.tab.*;
import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.api.wrapper.Player;
import org.darkstorm.runescape.aurora.tab.*;
import org.darkstorm.runescape.util.UnsupportedGameTypeException;

public class GameImpl extends AbstractUtility implements Game {
	private final Tab[] tabs;

	public GameImpl(GameContextImpl context) {
		super(context);

		tabs = new Tab[] { new CombatTabImpl(context),
				new InventoryTabImpl(context), new EmoteTabImpl(context),
				new EquipmentTabImpl(context), new FriendTabImpl(context),
				new IgnoreTabImpl(context), new LogoutTabImpl(context),
				new MagicTabImpl(context), new MusicTabImpl(context),
				new PrayerTabImpl(context), new QuestTabImpl(context),
				new SettingsTabImpl(context), new SkillTabImpl(context), };
	}

	@Override
	public Tab[] getTabs() {
		return tabs.clone();
	}

	@Override
	public Tab getOpenTab() {
		for(Tab tab : tabs)
			if(tab.isOpen())
				return tab;
		return null;
	}

	@Override
	public Tab getTab(String name) {
		for(Tab tab : tabs)
			if(tab.getName().equals(name))
				return tab;
		for(Tab tab : tabs)
			if(tab.getName().contains(name))
				return tab;
		return null;
	}

	@Override
	public <T extends Tab> T getTab(Class<T> tabClass) {
		for(Tab tab : tabs)
			if(tabClass.isInstance(tab))
				return tabClass.cast(tab);
		return null;
	}

	@Override
	public int getRegionBaseX() {
		return client.getBaseX();
	}

	@Override
	public int getRegionBaseY() {
		return client.getBaseY();
	}

	@Override
	public int getHealthPercentage() {
		return context.getPlayers().getSelf().getHealthPercentage();
	}

	@Override
	public int getHealth() {
		return context.getPlayers().getSelf().getHealth();
	}

	@Override
	public int getMaxHealth() {
		return context.getPlayers().getSelf().getMaxHealth();
	}

	@Override
	public int getRunPercentage() {
		return getTab(SettingsTab.class).getRunPercentage();
	}

	@Override
	public int getPrayerPercentage() {
		return (int) (100 * (getPrayerPoints() / (double) getMaxPrayerPoints()));
	}

	@Override
	public int getPrayerPoints() {
		return context.getSkills().getActualLevel(Skill.PRAYER);
	}

	@Override
	public int getMaxPrayerPoints() {
		return context.getSkills().getLevel(Skill.PRAYER);
	}

	@Override
	public void enableQuickPrayers() {
		throw new UnsupportedGameTypeException(GameType.CURRENT);
	}

	@Override
	public void disableQuickPrayers() {
		throw new UnsupportedGameTypeException(GameType.CURRENT);
	}

	@Override
	public boolean isUsingQuickPrayers() {
		throw new UnsupportedGameTypeException(GameType.CURRENT);
	}

	@Override
	public boolean hasSelectedItem() {
		return false;
	}

	@Override
	public boolean hasDestination() {
		return getDestination() != null;
	}

	@Override
	public Tile getDestination() {
		Player self = context.getPlayers().getSelf();
		if(!self.isMoving())
			return null;
		Tile[] waypoints = self.getWaypoints();
		return waypoints[waypoints.length - 1];
	}

	@Override
	public int getCurrentFloor() {
		return client.getPlane();
	}

	@Override
	public int[][] getTileCollisionData() {
		return client.getRegions()[getCurrentFloor()].getClippingMasks();
	}

	@Override
	public GameState getGameState() {
		switch(client.getLoginIndex()) {
		case 30:
			return GameState.LOGIN;
		case 20:
			return GameState.CONNECTING;
		case 25:
			return GameState.INITIATION;
		case 10:
			return GameState.INGAME;
		}
		return GameState.UNKNOWN;
	}

	@Override
	public boolean isInFixedMode() {
		return true;
	}

	@Override
	public boolean isLoading() {
		switch(getGameState()) {
		case LOGIN:
		case INGAME:
			return false;
		default:
			return true;
		}
	}

	@Override
	public boolean isLoggedIn() {
		return getGameState() == GameState.INGAME;
	}

	@Override
	public boolean isInLobby() {
		return false;
	}

	@Override
	public void logout() {
		if(getGameState() != GameState.LOGIN) {
			Player self = context.getPlayers().getSelf();
			if(self.isInCombat()) {
				for(int i = 0; i < 50 && self.isInCombat(); i++)
					calculations.sleep(100);
				if(self.isInCombat())
					return;
			}
			if(!(getOpenTab() instanceof LogoutTab)) {
				getTab(LogoutTab.class).open();
				calculations.sleep(50, 100);
				int fails = 0;
				while(!(getOpenTab() instanceof InventoryTab)) {
					fails++;
					if(fails > 20)
						return;
					calculations.sleep(100, 150);
				}
			}
			LogoutTab tab = (LogoutTab) getOpenTab();
			tab.logout();
			calculations.sleep(250, 500);
			int fails = 0;
			while(getGameState() != GameState.LOGIN) {
				fails++;
				if(fails > 20)
					return;
				calculations.sleep(100, 150);
			}
		}
	}

	@Override
	public void logout(boolean toLobby) {
		if(toLobby)
			throw new UnsupportedGameTypeException(GameType.CURRENT);
		logout();
	}

}
