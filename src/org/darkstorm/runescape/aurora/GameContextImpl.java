package org.darkstorm.runescape.aurora;

import ms.aurora.rt3.Client;

import org.darkstorm.runescape.api.*;
import org.darkstorm.runescape.api.input.*;

public final class GameContextImpl implements GameContext {
	private final AuroraBot bot;
	private final Client client;

	private final Calculations calculations;
	private final Game game;
	private final Players players;
	private final NPCs npcs;
	private final GameObjects gameObjects;
	private final GroundItems groundItems;
	private final Skills skills;
	private final Interfaces interfaces;
	private final Menu menu;
	private final Camera camera;
	private final Inventory inventory;
	private final Bank bank;
	private final Walking walking;
	private final Mouse mouse;
	private final Keyboard keyboard;
	private final Settings settings;
	private final Filters filters;

	public GameContextImpl(AuroraBot bot) {
		this.bot = bot;
		client = bot.getClient();

		calculations = new CalculationsImpl(this);
		mouse = new BasicMouse(this);
		keyboard = new BasicKeyboard(this);
		game = new GameImpl(this);
		players = new PlayersImpl(this);
		npcs = new NPCsImpl(this);
		gameObjects = new GameObjectsImpl(this);
		groundItems = new GroundItemsImpl(this);
		skills = new SkillsImpl(this);
		interfaces = new InterfacesImpl(this);
		menu = new MenuImpl(this);
		camera = new CameraImpl(this);
		inventory = new InventoryImpl(this);
		bank = new BankImpl(this);
		walking = new WalkingImpl(this);
		settings = new SettingsImpl(this);
		filters = new FiltersImpl(this);
	}

	@Override
	public Calculations getCalculations() {
		return calculations;
	}

	@Override
	public Game getGame() {
		return game;
	}

	@Override
	public Players getPlayers() {
		return players;
	}

	@Override
	public NPCs getNPCs() {
		return npcs;
	}

	@Override
	public Mouse getMouse() {
		return mouse;
	}

	@Override
	public Keyboard getKeyboard() {
		return keyboard;
	}

	@Override
	public Interfaces getInterfaces() {
		return interfaces;
	}

	@Override
	public GroundItems getGroundItems() {
		return groundItems;
	}

	@Override
	public GameObjects getGameObjects() {
		return gameObjects;
	}

	@Override
	public Skills getSkills() {
		return skills;
	}

	@Override
	public Menu getMenu() {
		return menu;
	}

	@Override
	public Bank getBank() {
		return bank;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public Camera getCamera() {
		return camera;
	}

	@Override
	public Walking getWalking() {
		return walking;
	}

	@Override
	public Settings getSettings() {
		return settings;
	}

	@Override
	public Filters getFilters() {
		return filters;
	}

	@Override
	public AuroraBot getBot() {
		return bot;
	}

	public Client getClient() {
		return client;
	}
}
