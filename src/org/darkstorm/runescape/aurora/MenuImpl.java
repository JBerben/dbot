package org.darkstorm.runescape.aurora;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

import org.darkstorm.runescape.api.Menu;
import org.darkstorm.runescape.api.input.*;
import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.api.wrapper.GroundItem;

public final class MenuImpl extends AbstractUtility implements Menu {
	private static final Pattern pattern = Pattern.compile("\\<.+?\\>");

	public MenuImpl(GameContextImpl context) {
		super(context);
	}

	@Override
	public int getActionIndex(String action) {
		action = action.toLowerCase();
		try {
			String[] actions = getActions();
			for(int i = 0; i < actions.length; i++)
				if(actions[i].toLowerCase().contains(action))
					return i;
			return -1;
		} catch(NullPointerException ignored) {
			return -1;
		}
	}

	@Override
	public String[] getActions() {
		String[] options = client.getMenuTargets();
		String[] actions = client.getMenuActions();
		int offset = client.getMenuCount();
		List<String> out = new ArrayList<String>();
		for(int i = offset - 1; i >= 0; i--) {
			String action = actions[i];
			String option = options[i];
			if(option != null && action != null) {
				String text = removeFormatting(action);
				if(!option.isEmpty())
					text += " " + removeFormatting(option);
				out.add(text);
			}
		}
		return out.toArray(new String[1]);
	}

	@Override
	public boolean perform(String action) {
		int idx = getActionIndex(action);
		for(int iterator = 0; iterator < 16; iterator++) {
			if(idx != -1) {
				break;
			}
			idx = getActionIndex(action);
			calculations.sleep(10, 30);
		}
		if(idx == -1) {

			while(isOpen())
				mouse.moveRandomly(750);
			return false; // Could not find item
		}
		if(!isOpen()) {
			if(idx == 0) {
				// First menu item is the same as top text so a left click will
				// do.
				mouse.click(true);
				return true;
			} else
				mouse.click(false);
		}
		for(int i = 0; i < 20; i++) {
			if(isOpen())
				break;
			calculations.sleep(10, 20);
		}
		if(!isOpen())
			return false; // Could not open menu
		idx = getActionIndex(action);
		if(idx == -1)
			return false;
		return perform(idx);
	}

	@Override
	public boolean perform(int index) {
		if(!isOpen())
			return false;
		try {
			Rectangle rect = new Rectangle(client.getMenuX() + 4,
					client.getMenuY() + 27 + 15 * index,
					client.getMenuWidth() - 4, 7);
			mouse.click(new RectangleMouseTarget(rect));
			if(!mouse.await())
				return false;
			calculations.sleep(50, 100);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean isOpen() {
		return client.isMenuOpen();
	}

	@Override
	public void close() {
		if(isOpen()) {
			int idx = getActionIndex("cancel");
			if(idx != -1 && calculations.random(0, 5) == 2) {
				perform(idx);
				return;
			}
			int failedTries = 0;
			while(isOpen()) {
				if(failedTries > 10) {
					idx = getActionIndex("cancel");
					if(idx != -1)
						perform(idx);
					else
						mouse.move(new PointMouseTarget(new Point(-1, -1), 1));
					return;
				}
				Rectangle bounds = getBounds();
				bounds.x -= 12;
				bounds.y -= 12;
				bounds.width += 24;
				bounds.height += 24;
				Point randomPoint;
				do {
					randomPoint = new Point(calculations.random(bounds.x - 10,
							bounds.x + bounds.width + 10), calculations.random(
							bounds.y - 10, bounds.y + bounds.height + 10));
				} while(bounds.contains(randomPoint));
				mouse.move(new PointMouseTarget(randomPoint, 1));
				calculations.sleep(50, 100);
				failedTries++;
			}
		}
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(client.getMenuX(), client.getMenuY(),
				client.getMenuWidth(), client.getMenuHeight());
	}

	@Override
	public String getLastSelectedItemName() {
		return null;
	}

	@Override
	public boolean perform(MouseTargetable target, String action) {
		for(int i = 0; i < 2; i++) {
			mouse.move(target);
			if(!mouse.await())
				continue;
			calculations.sleep(50, 150);
			if(target instanceof GroundItem) {
				if(customPerform((GroundItem) target, action))
					return true;
			} else if(perform(action))
				return true;
		}
		return false;
	}

	private boolean customPerform(GroundItem item, String action) {
		Filter<GroundItem> filter = context.getFilters().area(
				new TileArea(item.getLocation(), 1, 1));
		GroundItem[] items = context.getGroundItems().getAll(filter);
		for(int i = 0; i < items.length; i++) {
			if(items[i].getId() != item.getId()
					|| items[i].getHeight() != item.getHeight())
				continue;
			// i = items.length - i - 1;
			if(i == 0)
				break;
			if(!isOpen()) {
				mouse.click(false);
				for(int wait = 0; wait < 20 && !isOpen(); wait++)
					calculations.sleep(25);
				if(!isOpen())
					break;
			}
			action = action.toLowerCase();
			try {
				String[] actions = getActions();
				int actionCount = 0, actionIndex = -1;
				for(int j = 0; j < actions.length; j++) {
					if(!actions[j].toLowerCase().contains(action))
						continue;
					if(actionCount == i)
						actionIndex = j;
					actionCount++;
				}
				if(actionIndex == -1)
					break;
				return perform(actionIndex);
			} catch(NullPointerException ignored) {}
			break;
		}
		return perform(action);
	}

	public String removeFormatting(String in) {
		if(in == null)
			return "null";
		return pattern.matcher(in).replaceAll("");
	}

}
