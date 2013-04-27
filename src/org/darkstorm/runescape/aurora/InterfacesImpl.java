package org.darkstorm.runescape.aurora;

import java.util.*;

import ms.aurora.rt3.Widget;

import org.darkstorm.runescape.api.Interfaces;
import org.darkstorm.runescape.api.util.Filter;
import org.darkstorm.runescape.api.wrapper.*;
import org.darkstorm.runescape.aurora.wrapper.InterfaceWrapper;

public final class InterfacesImpl extends AbstractUtility implements Interfaces {

	public InterfacesImpl(GameContextImpl context) {
		super(context);
	}

	@Override
	public boolean interfaceComponentExists(int id, int childId) {
		return getComponent(id, childId) != null;
	}

	@Override
	public Interface getInterface(int id) {
		Widget[][] widgets = client.getWidgetCache();
		if(id < 0 || id > widgets.length - 1 || widgets[id] == null)
			return null;
		return new InterfaceWrapper(context, id, widgets[id]);
	}

	@Override
	public InterfaceComponent getComponent(int id, int childId) {
		Interface i = getInterface(id);
		if(i == null)
			return null;
		return i.getComponent(childId);
	}

	@Override
	public InterfaceComponent getComponent(Filter<InterfaceComponent> filter) {
		Widget[][] widgets = client.getWidgetCache();
		for(int i = 0; i < widgets.length; i++) {
			if(widgets[i] != null) {
				Interface iface = new InterfaceWrapper(context, i, widgets[i]);
				InterfaceComponent component = iface.getComponent(filter);
				if(component != null)
					return component;
			}
		}
		return null;
	}

	@Override
	public Interface getInterface(Filter<Interface> filter) {
		Widget[][] widgets = client.getWidgetCache();
		for(int i = 0; i < widgets.length; i++) {
			if(widgets[i] != null) {
				Interface iface = new InterfaceWrapper(context, i, widgets[i]);
				if(filter.accept(iface))
					return iface;
			}
		}
		return null;
	}

	@Override
	public Interface[] getInterfaces(Filter<Interface> filter) {
		List<Interface> interfaces = new ArrayList<>();
		Widget[][] widgets = client.getWidgetCache();
		for(int i = 0; i < widgets.length; i++) {
			if(widgets[i] != null) {
				Interface iface = new InterfaceWrapper(context, i, widgets[i]);
				if(filter.accept(iface))
					interfaces.add(iface);
			}
		}
		return interfaces.toArray(new Interface[interfaces.size()]);
	}

	@Override
	public Interface[] getInterfaces() {
		Widget[][] widgets = client.getWidgetCache();
		Interface[] interfaces = new Interface[widgets.length];
		for(int i = 0; i < widgets.length; i++)
			if(widgets[i] != null)
				interfaces[i] = new InterfaceWrapper(context, i, widgets[i]);
		return interfaces;
	}

}
