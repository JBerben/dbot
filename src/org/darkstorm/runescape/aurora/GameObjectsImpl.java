package org.darkstorm.runescape.aurora;

import java.util.*;

import ms.aurora.rt3.*;

import org.darkstorm.runescape.api.GameObjects;
import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.api.wrapper.GameObject;
import org.darkstorm.runescape.aurora.wrapper.GameObjectWrapper;

public final class GameObjectsImpl extends AbstractUtility implements
		GameObjects {

	public GameObjectsImpl(GameContextImpl context) {
		super(context);
	}

	@Override
	public GameObject getClosest(Filter<GameObject> filter) {
		GameObject closest = null;
		double minDist = 0;
		Tile self = context.getPlayers().getSelf().getLocation();
		for(GameObject gameObject : getAll()) {
			if(filter.accept(gameObject)) {
				double dist = self.distanceTo(gameObject.getLocation());
				if(dist < minDist || closest == null) {
					closest = gameObject;
					minDist = dist;
				}
			}
		}
		return closest;
	}

	@Override
	public GameObject[] getAll(Filter<GameObject> filter) {
		List<GameObject> accepted = new ArrayList<GameObject>();
		for(GameObject object : getAll())
			if(filter.accept(object))
				accepted.add(object);
		return accepted.toArray(new GameObject[accepted.size()]);
	}

	@Override
	public GameObject[] getAll() {
		WorldController sceneGraph = client.getWorld();
		if(sceneGraph == null)
			return new GameObject[0];
		int plane = client.getPlane();
		List<GameObject> objects = new ArrayList<GameObject>();
		Ground[][][] tiles = sceneGraph.getGroundArray();
		if(tiles.length <= plane)
			return new GameObject[0];
		Ground[][] planeTiles = tiles[plane];
		for(int x = 0; x < planeTiles.length; x++) {
			if(planeTiles[x] == null)
				continue;
			for(int y = 0; y < planeTiles[x].length; y++) {
				Ground tile = planeTiles[x][y];
				if(tile == null)
					continue;
				WallObject wall = tile.getWallObject();
				WallDecoration decoration = tile.getWallDecoration();
				AnimableObject[] interactives = tile.getAnimableObjects();
				if(wall != null)
					objects.add(new GameObjectWrapper(context, wall));
				if(decoration != null)
					objects.add(new GameObjectWrapper(context, decoration));
				if(interactives != null)
					for(AnimableObject interactive : interactives)
						if(interactive != null)
							objects.add(new GameObjectWrapper(context,
									interactive));
			}
		}
		for(GameObject object : objects.toArray(new GameObject[objects.size()]))
			if(object.getId() == -1)
				objects.remove(object);
		return objects.toArray(new GameObject[objects.size()]);
	}
}
