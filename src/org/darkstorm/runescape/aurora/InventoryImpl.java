package org.darkstorm.runescape.aurora;

import java.awt.Rectangle;
import java.util.*;

import org.darkstorm.runescape.api.Inventory;
import org.darkstorm.runescape.api.tab.InventoryTab;
import org.darkstorm.runescape.api.util.Filter;
import org.darkstorm.runescape.api.wrapper.*;
import org.darkstorm.runescape.aurora.wrapper.ItemWrapper;

public final class InventoryImpl extends AbstractUtility implements Inventory {
	private final Item[] itemCache = new Item[28];

	public InventoryImpl(GameContextImpl context) {
		super(context);
	}

	@Override
	public boolean contains(Filter<Item> filter) {
		for(Item item : getItems())
			if(item != null && filter.accept(item))
				return true;
		return false;
	}

	@Override
	public Item getItem(Filter<Item> filter) {
		for(Item item : getItems())
			if(item != null && filter.accept(item))
				return item;
		return null;
	}

	@Override
	public Item[] getItems(Filter<Item> filter) {
		List<Item> items = new ArrayList<>();
		for(Item item : getItems())
			if(item != null && filter.accept(item))
				items.add(item);
		return items.toArray(new Item[items.size()]);
	}

	@Override
	public synchronized Item[] getItems() {
		InterfaceComponent inventory = context.getGame()
				.getTab(InventoryTab.class).getInventoryArea();
		if(inventory == null || !inventory.isValid()
				|| !inventory.isInventory())
			return null;

		int[] ids = inventory.getItemIds();
		int[] stacks = inventory.getItemStackSizes();
		if(ids == null || stacks == null)
			return null;

		for(int i = 0; i < ids.length; i++) {
			int id = ids[i] - 1, stack = stacks[i];
			if(itemCache[i] == null && id == -1)
				continue;
			if(itemCache[i] != null && itemCache[i].getId() == id
					&& itemCache[i].getStackSize() == stack)
				continue;
			itemCache[i] = id != -1 ? new ItemWrapper(context, getArea(i), id,
					stack, null) : null;
		}
		return itemCache.clone();
	}

	private Rectangle getArea(int slot) {
		int col = (slot % 4);
		int row = (slot / 4);
		int x = 580 + (col * 42);
		int y = 228 + (row * 36);
		return new Rectangle(x - 12, y - 12, 24, 24);
	}

	@Override
	public boolean isFull() {
		for(Item item : getItems())
			if(item == null)
				return false;
		return true;
	}

	@Override
	public int getCount(Filter<Item> filter) {
		int count = 0;
		for(Item item : getItems())
			if(item != null && filter.accept(item))
				count += item.getStackSize();
		return count;
	}

}
