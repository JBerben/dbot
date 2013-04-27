package org.darkstorm.runescape.aurora.wrapper;

import java.awt.Point;
import java.lang.reflect.Method;

import ms.aurora.rt3.*;

import org.darkstorm.runescape.api.input.*;
import org.darkstorm.runescape.api.util.Tile;
import org.darkstorm.runescape.api.wrapper.Animable;
import org.darkstorm.runescape.aurora.GameContextImpl;

public abstract class AnimableWrapper extends AbstractWrapper implements
		Animable {
	private Renderable handle;

	public AnimableWrapper(GameContextImpl context, Renderable handle) {
		super(context);
		this.handle = handle;
	}

	@Override
	public int getHeight() {
		return -handle.getHeight() / 2;
	}

	@Override
	public ModelWrapper getModel() {
		Model raw = getRawModel(handle);
		if(raw != null)
			return new ModelWrapper(context, raw, this);
		return null;
	}

	@Override
	public abstract Tile getLocation();

	@Override
	public Point getScreenLocation() {
		Tile location = getLocation();
		int height = getHeight();
		return context.getCalculations().getWorldScreenLocation(
				location.getPreciseX(), location.getPreciseY(),
				height / 2 + height / 4);
	}

	@Override
	public MouseTarget getTarget() {
		return new ModelMouseTarget(getModel());
	}

	@Override
	public boolean isOnScreen() {
		Point point = getTarget().getLocation();
		return point != null && context.getCalculations().isInGameArea(point);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof AnimableWrapper && getClass() == obj.getClass()
				&& handle == ((AnimableWrapper) obj).handle;
	}

	static final Model getRawModel(Renderable renderable) {
		Class<?> handleClass = renderable.getClass();
		for(Method method : handleClass.getDeclaredMethods()) {
			if(!Model.class.isAssignableFrom(method.getReturnType())
					|| method.getParameterTypes().length != 0)
				continue;
			try {
				method.setAccessible(true);
				Object o = method.invoke(renderable);
				if(o != null && o instanceof Model)
					return (Model) o;
			} catch(Exception exception) {}
		}
		return null;
	}
}
