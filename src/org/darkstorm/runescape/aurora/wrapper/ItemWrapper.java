package org.darkstorm.runescape.aurora.wrapper;

import java.awt.*;

import org.darkstorm.runescape.api.input.*;
import org.darkstorm.runescape.api.tab.InventoryTab;
import org.darkstorm.runescape.api.wrapper.Item;
import org.darkstorm.runescape.aurora.GameContextImpl;

public class ItemWrapper extends AbstractWrapper implements Item {
	private Rectangle area;
	private int id, stack;
	private String name;

	public ItemWrapper(GameContextImpl context, Rectangle area, int id,
			int stack, String name) {
		super(context);
		this.area = area;
		this.id = id;
		this.stack = stack;
		this.name = name;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public MouseTarget getTarget() {
		return new RectangleMouseTarget(area);
	}

	@Override
	public Point getScreenLocation() {
		return new Point(area.x + area.width / 2, area.y + area.height / 2);
	}

	@Override
	public boolean isOnScreen() {
		return context.getGame().getOpenTab() instanceof InventoryTab;
	}

	@Override
	public int getStackSize() {
		return stack;
	}
}
