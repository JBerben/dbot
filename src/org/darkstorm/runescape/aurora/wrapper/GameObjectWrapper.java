package org.darkstorm.runescape.aurora.wrapper;

import java.awt.Point;

import ms.aurora.rt3.*;

import org.darkstorm.runescape.api.Game;
import org.darkstorm.runescape.api.input.*;
import org.darkstorm.runescape.api.util.Tile;
import org.darkstorm.runescape.api.wrapper.Model;
import org.darkstorm.runescape.aurora.GameContextImpl;

public class GameObjectWrapper extends AbstractWrapper implements
		org.darkstorm.runescape.api.wrapper.GameObject {
	private final GameObjectType type;
	private final Tile tile;

	private AnimableObject object;
	private WallDecoration wallDecoration;
	private WallObject boundary;
	private ModelWrapper model;
	private int id;

	public GameObjectWrapper(GameContextImpl context, AnimableObject obj) {
		this(context, GameObjectType.INTERACTIVE, obj.getX(), obj.getY());
		id = (obj.getHash() >> 14) & 0x7FFF;
		object = obj;

		Renderable render = obj.getModel();
		if(render == null)
			return;
		ms.aurora.rt3.Model raw = AnimableWrapper.getRawModel(render);
		if(raw == null)
			return;
		model = new ModelWrapper(context, raw, this);
	}

	public GameObjectWrapper(GameContextImpl context, WallDecoration dec) {
		this(context, GameObjectType.DECORATION, dec.getX(), dec.getY());
		id = (dec.getHash() >> 14) & 0x7FFF;
		wallDecoration = dec;

		Renderable render = dec.getModel();
		if(render == null)
			return;
		ms.aurora.rt3.Model raw = AnimableWrapper.getRawModel(render);
		if(raw == null)
			return;
		model = new ModelWrapper(context, raw, this);
	}

	public GameObjectWrapper(GameContextImpl context, WallObject boundary) {
		this(context, GameObjectType.BOUNDARY, boundary.getHash() & 0x7F,
				boundary.getHash() >> 7 & 0x7F);
		id = (boundary.getHash() >> 14) & 0x7FFF;
		this.boundary = boundary;

		Renderable render = boundary.getModel();
		if(render == null)
			return;
		ms.aurora.rt3.Model raw = AnimableWrapper.getRawModel(render);
		if(raw == null)
			return;
		model = new ModelWrapper(context, raw, this);
	}

	private GameObjectWrapper(GameContextImpl context, GameObjectType type,
			int x, int y) {
		super(context);
		this.type = type;
		Game game = context.getGame();
		tile = new Tile(game.getRegionBaseX() + x, game.getRegionBaseY() + y,
				game.getCurrentFloor());
	}

	@Override
	public Model getModel() {
		return model;
	}

	@Override
	public GameObjectType getType() {
		return type;
	}

	@Override
	public Tile getLocation() {
		return tile;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public MouseTarget getTarget() {
		Model model = getModel();
		if(model == null)
			return null;
		return new ModelMouseTarget(model);
	}

	@Override
	public boolean isOnScreen() {
		return getModel().getTriangles().length > 0;
	}

	@Override
	public Point getScreenLocation() {
		Model model = getModel();
		if(model == null)
			return new Point(-1, -1);
		return model.getCenterPoint();
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof GameObjectWrapper && getClass() == obj.getClass()))
			return false;
		GameObjectWrapper o = (GameObjectWrapper) obj;
		if(object != null && o.object != null && object == o.object)
			return true;
		if(boundary != null && o.boundary != null && boundary == o.boundary)
			return true;
		if(wallDecoration != null && o.wallDecoration != null
				&& wallDecoration == o.wallDecoration)
			return true;
		return false;
	}
}
