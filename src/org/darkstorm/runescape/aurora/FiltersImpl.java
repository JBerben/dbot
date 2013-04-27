package org.darkstorm.runescape.aurora;

import org.darkstorm.runescape.api.*;
import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.api.wrapper.*;
import org.darkstorm.runescape.api.wrapper.GameObject.GameObjectType;

public class FiltersImpl extends AbstractUtility implements Filters {

	public FiltersImpl(GameContextImpl context) {
		super(context);
	}

	@Override
	public Filter<NPC> npc(final int... ids) {
		return new Filter<NPC>() {

			@Override
			public boolean accept(NPC value) {
				if(value == null)
					return false;
				for(int id : ids)
					if(value.getId() == id)
						return true;
				return false;
			}
		};
	}

	@Override
	public Filter<NPC> npc(final String... names) {
		return new Filter<NPC>() {

			@Override
			public boolean accept(NPC value) {
				if(value == null || value.getName() == null)
					return false;
				for(String name : names)
					if(name.equals(value.getName()))
						return true;
				return false;
			}
		};
	}

	@Override
	public Filter<Player> player(final String name) {
		return new Filter<Player>() {

			@Override
			public boolean accept(Player value) {
				return value != null && value.getName() != null
						&& name.equals(value.getName());
			}
		};
	}

	@Override
	public Filter<GroundItem> ground(final int... ids) {
		return new Filter<GroundItem>() {

			@Override
			public boolean accept(GroundItem value) {
				if(value == null)
					return false;
				for(int id : ids)
					if(id == value.getId())
						return true;
				return false;
			}
		};
	}

	@Override
	public Filter<GameObject> object(final GameObjectType... types) {
		return new Filter<GameObject>() {

			@Override
			public boolean accept(GameObject value) {
				if(value == null)
					return false;
				for(GameObjectType type : types)
					if(type == value.getType())
						return true;
				return false;
			}
		};
	}

	@Override
	public Filter<GameObject> object(final int... ids) {
		return new Filter<GameObject>() {

			@Override
			public boolean accept(GameObject value) {
				if(value == null)
					return false;
				for(int id : ids)
					if(id == value.getId())
						return true;
				return false;
			}
		};
	}

	@Override
	public Filter<Item> item(final int... ids) {
		return new Filter<Item>() {

			@Override
			public boolean accept(Item value) {
				if(value == null)
					return false;
				for(int id : ids)
					if(id == value.getId())
						return true;
				return false;
			}
		};
	}

	@Override
	public Filter<Item> item(final String... names) {
		return new Filter<Item>() {

			@Override
			public boolean accept(Item value) {
				if(value == null || value.getName() == null)
					return false;
				for(String name : names)
					if(name.equals(value.getName()))
						return true;
				return false;
			}
		};
	}

	@Override
	public Filter<Interface> inter(final int... ids) {
		return new Filter<Interface>() {
			@Override
			public boolean accept(Interface value) {
				if(value == null)
					return false;
				for(int id : ids)
					if(id == value.getId())
						return true;
				return false;
			}
		};
	}

	@Override
	public Filter<Interface> inter(final String... text) {
		return new Filter<Interface>() {
			@Override
			public boolean accept(Interface value) {
				if(value == null)
					return false;
				for(InterfaceComponent child : value.getComponents()) {
					for(String t : text) {
						String childText = child.getText();
						if(childText != null && t.equals(childText))
							return true;
					}
					if(checkChildren(child))
						return true;
				}
				return false;
			}

			public boolean checkChildren(InterfaceComponent component) {
				for(InterfaceComponent child : component.getChildren()) {
					for(String t : text) {
						String childText = child.getText();
						if(childText != null && t.equals(childText))
							return true;
					}
					if(checkChildren(child))
						return true;
				}
				return false;
			}
		};
	}

	@Override
	public Filter<InterfaceComponent> component(final int id,
			final int... childIds) {
		return new Filter<InterfaceComponent>() {
			@Override
			public boolean accept(InterfaceComponent value) {
				if(value == null || id != value.getInterface().getId())
					return false;
				for(int id : childIds)
					if(id == value.getId())
						return true;
				return false;
			}
		};
	}

	@Override
	public Filter<InterfaceComponent> component(final String... text) {
		return new Filter<InterfaceComponent>() {
			@Override
			public boolean accept(InterfaceComponent value) {
				if(value == null)
					return false;
				String childText = value.getText();
				if(childText == null)
					return false;
				for(String t : text)
					if(t.equals(childText))
						return true;
				return false;
			}
		};
	}

	@Override
	public Filter<InterfaceComponent> component(final Filter<Interface> filter,
			final int... childIds) {
		return new Filter<InterfaceComponent>() {
			@Override
			public boolean accept(InterfaceComponent value) {
				if(value == null || !filter.accept(value.getInterface()))
					return false;
				for(int id : childIds)
					if(id == value.getId())
						return true;
				return false;
			}
		};
	}

	@Override
	public Filter<InterfaceComponent> component(final Filter<Interface> filter,
			final String... text) {
		return new Filter<InterfaceComponent>() {
			@Override
			public boolean accept(InterfaceComponent value) {
				if(value == null || !filter.accept(value.getInterface()))
					return false;
				String childText = value.getText();
				if(childText == null)
					return false;
				for(String t : text)
					if(t.equals(childText))
						return true;
				return false;
			}
		};
	}

	@Override
	public <T extends Identifiable> Filter<T> id(final int... ids) {
		return new Filter<T>() {
			@Override
			public boolean accept(T value) {
				if(value == null)
					return false;
				for(int id : ids)
					if(id == value.getId())
						return true;
				return false;
			}
		};
	}

	@Override
	public <T extends Nameable> Filter<T> name(final String... names) {
		return new Filter<T>() {
			@Override
			public boolean accept(T value) {
				if(value == null || value.getName() == null)
					return false;
				for(String name : names)
					if(name.equals(value.getName()))
						return true;
				return false;
			}
		};
	}

	@Override
	public <T extends Locatable> Filter<T> range(final int range) {
		return new Filter<T>() {
			@Override
			public boolean accept(T value) {
				Player self = context.getPlayers().getSelf();
				Tile location = self.getLocation();
				return location.distanceTo(value) <= range;
			}
		};
	}

	@Override
	public <T extends Locatable> Filter<T> range(final Locatable origin,
			final int range) {
		return new Filter<T>() {
			@Override
			public boolean accept(T value) {
				Tile location = origin.getLocation();
				return location.distanceTo(value) <= range;
			}
		};
	}

	@Override
	public <T extends Locatable> Filter<T> area(final TileArea area) {
		return new Filter<T>() {
			@Override
			public boolean accept(T value) {
				return area.contains(value.getLocation());
			}
		};
	}

	@Override
	public <T> Filter<T> all() {
		return new Filter<T>() {
			@Override
			public boolean accept(T value) {
				return true;
			}
		};
	}

	@Override
	public <T> Filter<T> none() {
		return new Filter<T>() {
			@Override
			public boolean accept(T value) {
				return false;
			}
		};
	}

	@Override
	public <T> Filter<T> all(TypedUtility<T> utility) {
		return all();
	}

	@Override
	public <T> Filter<T> none(TypedUtility<T> utility) {
		return none();
	}

	@Override
	public <T> Filter<T> inverse(final Filter<T> filter) {
		return new Filter<T>() {
			@Override
			public boolean accept(T value) {
				return !filter.accept(value);
			}
		};
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Filter<T> chain(Filter<T> filter1, Filter<T> filter2) {
		return chain(new Filter[] { filter1, filter2 });
	}

	@Override
	public <T> Filter<T> chain(
			@SuppressWarnings("unchecked") final Filter<T>... filters) {
		return new Filter<T>() {
			@Override
			public boolean accept(T value) {
				for(Filter<T> filter : filters)
					if(!filter.accept(value))
						return false;
				return true;
			}
		};
	}

}
