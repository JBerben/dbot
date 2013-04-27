package org.darkstorm.runescape.aurora.wrapper;

import java.awt.Point;

import org.darkstorm.runescape.api.input.*;
import org.darkstorm.runescape.api.util.Tile;
import org.darkstorm.runescape.api.wrapper.*;
import org.darkstorm.runescape.aurora.GameContextImpl;

public class GroundItemWrapper extends AbstractWrapper implements GroundItem {
	private final ms.aurora.rt3.Item handle;
	private final Tile location;

	private int height;

	public GroundItemWrapper(GameContextImpl context,
			ms.aurora.rt3.Item handle, int x, int y) {
		super(context);
		this.handle = handle;
		location = new Tile(x, y, context.getClient().getPlane());
	}

	@Override
	public int getId() {
		return handle.getId();
	}

	@Override
	public Tile getLocation() {
		return location;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public Model getModel() {
		return null;// getModel(GroundItemModelLayer.TOP);
	}

	@Override
	public Model getModel(GroundItemModelLayer layer) {
		// XClient client = context.getClient();
		// int x = location.getX() - client.getMapBaseX();
		// int y = location.getY() - client.getMapBaseY();
		//
		// XTile tile =
		// client.getCurrentRegion().getTiles()[client.getPlane()][x][y];
		//
		// if(tile == null) {
		// return null;
		// }
		//
		// IGroundLayer groundLayer = tile.getGroundLayer();
		// if(layer == null) {
		// return null;
		// }
		//
		// IRenderable rend = null;
		//
		// switch(layer) {
		// case TOP:
		// rend = groundLayer.getTop();
		// break;
		// case MIDDLE:
		// rend = groundLayer.getMiddle();
		// break;
		// case BOTTOM:
		// rend = groundLayer.getBottom();
		// break;
		// }
		//
		// if(rend == null) {
		// return null;
		// }
		//
		// height = rend.getModelHeight();
		// return new ModelWrapper(context,
		// PersistentModelCache.table.get(rend),
		// this);
		return null;
	}

	@Override
	public MouseTarget getTarget() {
		return new TileMouseTarget(context, getLocation());
		// new ModelMouseTarget(getModel());
	}

	@Override
	public boolean isOnScreen() {
		return context.getCalculations().isInGameArea(getScreenLocation());
	}

	@Override
	public Point getScreenLocation() {
		Tile location = getLocation();
		return context.getCalculations()
				.getWorldScreenLocation(location.getPreciseX(),
						location.getPreciseY(), getHeight() / 2);
	}

}
