package org.darkstorm.runescape.aurora.wrapper;

import java.awt.*;

import ms.aurora.rt3.Widget;

import org.darkstorm.runescape.api.input.*;
import org.darkstorm.runescape.api.util.Filter;
import org.darkstorm.runescape.api.wrapper.*;
import org.darkstorm.runescape.aurora.GameContextImpl;

public class InterfaceComponentWrapper extends AbstractWrapper implements
		InterfaceComponent {
	private Interface source;
	private InterfaceComponent parent;
	private int index;
	private Widget widget;

	public InterfaceComponentWrapper(GameContextImpl context, Interface source,
			int index, Widget widget) {
		this(context, source, null, index, widget);
	}

	public InterfaceComponentWrapper(GameContextImpl context, Interface source,
			InterfaceComponent parent, int index, Widget widget) {
		super(context);
		this.source = source;
		this.parent = parent;
		this.index = index;
		this.widget = widget;
	}

	@Override
	public int getId() {
		return index;
	}

	@Override
	public InterfaceComponent getComponent() {
		return this;
	}

	@Override
	public MouseTarget getTarget() {
		return new RectangleMouseTarget(getBounds());
	}

	@Override
	public InterfaceComponent[] getChildren() {
		Widget[] widgets = widget.getChildren();
		if(widgets == null)
			return null;
		InterfaceComponent[] components = new InterfaceComponent[widgets.length];
		for(int i = 0; i < widgets.length; i++)
			if(widgets[i] != null)
				components[i] = new InterfaceComponentWrapper(context, source,
						this, i, widgets[i]);
		return components;
	}

	@Override
	public InterfaceComponent getChild(int id) {
		Widget[] widgets = widget.getChildren();
		if(widgets == null || widgets[id] == null)
			return null;
		return new InterfaceComponentWrapper(context, source, this, id,
				widgets[id]);
	}

	@Override
	public InterfaceComponent getChild(Filter<InterfaceComponent> filter) {
		Widget[] widgets = widget.getChildren();
		if(widgets == null)
			return null;
		for(int i = 0; i < widgets.length; i++) {
			if(widgets[i] == null)
				continue;
			InterfaceComponent component = new InterfaceComponentWrapper(
					context, source, this, i, widgets[i]);
			if(filter.accept(component))
				return component;
		}
		return null;
	}

	@Override
	public int[] getItemIds() {
		return widget.getInventoryItems();
	}

	@Override
	public int[] getItemStackSizes() {
		return widget.getInventoryStackSizes();
	}

	@Override
	public int getContainedItemId() {
		return -1;
	}

	@Override
	public int getContainedItemStackSize() {
		return -1;
	}

	@Override
	public boolean hasParent() {
		return parent != null;
	}

	@Override
	public InterfaceComponent getParent() {
		return parent;
	}

	@Override
	public Interface getInterface() {
		return source;
	}

	@Override
	public Rectangle getRelativeBounds() {
		return new Rectangle(widget.getX(), widget.getY(), widget.getWidth(),
				widget.getHeight());
	}

	@Override
	public Rectangle getBounds() {
		int boundsIndex = widget.getBoundsIndex();

		int x = widget.getX();
		if(parent == null) {
			int[] boundsX = context.getClient().getBoundsX();
			if(boundsIndex != -1 && boundsX[boundsIndex] > 0) {
				if(widget.getType() <= 0)
					x = 0;
				x += boundsX[boundsIndex];
			}
		} else
			x += parent.getBounds().x;

		int y = widget.getY();
		if(parent == null) {
			int[] boundsY = context.getClient().getBoundsY();
			if(boundsIndex != -1 && boundsY[boundsIndex] > 0) {
				if(widget.getType() <= 0)
					y = 0;
				y += boundsY[boundsIndex];
			}
		} else
			y += parent.getBounds().y;

		return new Rectangle(x, y, widget.getWidth(), widget.getHeight());
	}

	@Override
	public Point getCenter() {
		return new Point(widget.getX() + widget.getWidth() / 2, widget.getY()
				+ widget.getHeight() / 2);
	}

	@Override
	public String getText() {
		return widget.getText();
	}

	@Override
	public String getTooltip() {
		return null; // widget.getToolTip();
	}

	@Override
	public String[] getActions() {
		return widget.getActions();
	}

	@Override
	public int getTextColor() {
		return widget.getTextColor();
	}

	@Override
	public boolean isInventory() {
		int[] slotContents = widget.getInventoryItems();
		return slotContents != null && slotContents.length > 0;
	}

	@Override
	public int getTextureId() {
		return widget.getTextureId();
	}

	@Override
	public int getScrollPosition() {
		return -1;// widget.getScrollPosition();
	}

	@Override
	public int getScrollHeight() {
		return widget.getScrollMaxHorizontal();
	}

	@Override
	public int getModelId() {
		return widget.getModelId();
	}

	@Override
	public int getModelType() {
		return widget.getModelType();
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public String getSelectedAction() {
		return widget.getSelectedAction();
	}

	@Override
	public int getType() {
		return widget.getType();
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof InterfaceComponentWrapper && getClass() == obj
				.getClass()))
			return false;
		InterfaceComponentWrapper wrapper = (InterfaceComponentWrapper) obj;
		if(index != wrapper.index)
			return false;
		if(source.getId() != wrapper.source.getId())
			return false;
		if((parent == null || wrapper.parent == null)
				&& parent != wrapper.parent)
			return false;
		return parent == wrapper.parent || parent.equals(wrapper.parent);
	}
}
