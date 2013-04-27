package org.darkstorm.runescape.aurora;

import java.util.*;

import ms.aurora.rt3.*;
import ms.aurora.rt3.Deque;

import org.darkstorm.runescape.api.GroundItems;
import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.api.wrapper.GroundItem;
import org.darkstorm.runescape.aurora.wrapper.GroundItemWrapper;

public final class GroundItemsImpl extends AbstractUtility implements
		GroundItems {

	public GroundItemsImpl(GameContextImpl context) {
		super(context);
	}

	@Override
	public GroundItem getClosest(Filter<GroundItem> filter) {
		GroundItem closest = null;
		double minDist = 0;
		Tile self = context.getPlayers().getSelf().getLocation();
		for(GroundItem groundItem : getAll()) {
			if(filter.accept(groundItem)) {
				double dist = self.distanceTo(groundItem.getLocation());
				if(dist < minDist || closest == null) {
					closest = groundItem;
					minDist = dist;
				}
			}
		}
		return closest;
	}

	@Override
	public GroundItem[] getAll(Filter<GroundItem> filter) {
		List<GroundItem> accepted = new ArrayList<GroundItem>();
		for(GroundItem groundItem : getAll())
			if(filter.accept(groundItem))
				accepted.add(groundItem);
		return accepted.toArray(new GroundItem[accepted.size()]);
	}

	@Override
	public GroundItem[] getAll() {
		List<GroundItem> items = new ArrayList<GroundItem>();
		Deque[][][] groundItems = client.getGroundItems();
		for(int i = 0; i < groundItems.length; i++) {
			Deque[][] depthGroundItems = groundItems[i];
			for(int x = 0; x < depthGroundItems.length; x++) {
				Deque[] xGroundItems = depthGroundItems[x];
				for(int y = 0; y < xGroundItems.length; y++) {
					Deque item = xGroundItems[y];
					if(item == null)
						continue;
					DequeIterator iterator = new DequeIterator(item);
					while(iterator.hasNext()) {
						Node node = iterator.next();
						if(!(node instanceof Item))
							continue;
						GroundItem groundItem = new GroundItemWrapper(context,
								(Item) node, context.getGame().getRegionBaseX()
										+ x, context.getGame().getRegionBaseY()
										+ y);
						items.add(groundItem);
					}
				}
			}
		}
		return items.toArray(new GroundItem[items.size()]);
	}

	private class DequeIterator implements Iterator<Node> {
		private final Deque deque;

		private Node node;

		public DequeIterator(Deque deque) {
			this.deque = deque;
			node = deque.getHead() != null ? deque.getHead().getNext() : null;
		}

		@Override
		public boolean hasNext() {
			return node != null && node.getNext() != null;
		}

		@Override
		public Node next() {
			Node next = node;
			if(next == deque.getHead())
				return node = null;
			node = next.getNext();
			return next;
		}

		@Override
		public void remove() {
		}

		/*public int size() {
			int size = 0;
			Node start = deque.getHead();
			for(Node current = start.getNext(); start != current; current = current.getNext())
				size++;
			return size;
		}*/
	}
}
