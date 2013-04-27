package org.darkstorm.runescape.aurora.wrapper;

import ms.aurora.rt3.Widget;

import org.darkstorm.runescape.api.util.Filter;
import org.darkstorm.runescape.api.wrapper.*;
import org.darkstorm.runescape.aurora.GameContextImpl;

public class InterfaceWrapper extends AbstractWrapper implements Interface {
	private int index;
	private Widget[] widgets;

	public InterfaceWrapper(GameContextImpl context, int index, Widget[] widgets) {
		super(context);
		this.index = index;
		this.widgets = widgets;
	}

	@Override
	public int getId() {
		return index;
	}

	@Override
	public InterfaceComponent[] getComponents() {
		InterfaceComponent[] components = new InterfaceComponent[widgets.length];
		for(int i = 0; i < widgets.length; i++)
			if(widgets[i] != null)
				components[i] = new InterfaceComponentWrapper(context, this, i,
						widgets[i]);
		return components;
	}

	@Override
	public InterfaceComponent getComponent(int id) {
		if(widgets[id] == null)
			return null;
		return new InterfaceComponentWrapper(context, this, id, widgets[id]);
	}

	@Override
	public InterfaceComponent getComponent(Filter<InterfaceComponent> filter) {
		for(int i = 0; i < widgets.length; i++) {
			if(widgets[i] == null)
				continue;
			InterfaceComponent component = new InterfaceComponentWrapper(
					context, this, i, widgets[i]);
			if(filter.accept(component))
				return component;
		}
		return null;
	}

	@Override
	public boolean isValid() {
		return widgets.length > 0 && widgets[0] != null;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof InterfaceWrapper && getClass() == obj.getClass()
				&& index == ((InterfaceWrapper) obj).index;
	}
}
