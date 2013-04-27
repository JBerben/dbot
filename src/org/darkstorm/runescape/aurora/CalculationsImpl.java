package org.darkstorm.runescape.aurora;

import java.applet.Applet;
import java.awt.*;
import java.util.concurrent.*;

import ms.aurora.rt3.Client;

import org.darkstorm.runescape.api.*;
import org.darkstorm.runescape.api.pathfinding.*;
import org.darkstorm.runescape.api.pathfinding.astar.*;
import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;

public final class CalculationsImpl implements Calculations {
	private static final Rectangle GAMESCREEN = new Rectangle(4, 4, 512, 334);
	private static final int[] CURVESIN = new int[2048];
	private static final int[] CURVECOS = new int[2048];

	static {
		for(int i = 0; i < 2048; i++) {
			CURVESIN[i] = (int) (65536.0 * Math.sin(i * 0.0030679615));
			CURVECOS[i] = (int) (65536.0 * Math.cos(i * 0.0030679615));
		}
	}

	private final AuroraBot bot;
	private final GameContextImpl context;
	private final Client client;
	private final PathSearchProvider searchProvider;

	public CalculationsImpl(GameContextImpl context) {
		bot = context.getBot();
		this.context = context;
		client = context.getClient();
		searchProvider = new AStarPathSearchProvider(context,
				new LocalAStarHeuristic(context));
	}

	@Override
	public boolean isInGameArea(Point point) {
		return isInGameArea(point.x, point.y);
	}

	@Override
	public boolean isInGameArea(int x, int y) {
		return GAMESCREEN.contains(x, y);
	}

	@Override
	public Shape getGameArea() {
		return (Rectangle) GAMESCREEN.clone();
	}

	@Override
	public boolean isOnScreen(Point point) {
		return isOnScreen(point.x, point.y);
	}

	@Override
	public boolean isOnScreen(int x, int y) {
		Applet game = bot.getGame();
		return x >= 0 && y >= 0 && x < game.getWidth() && y < game.getHeight();
	}

	@Override
	public Rectangle getScreenArea() {
		Applet game = bot.getGame();
		return new Rectangle(0, 0, game.getWidth(), game.getHeight());
	}

	@Override
	public boolean canReach(Tile tile) {
		return generateNodePath(context.getPlayers().getSelf().getLocation(),
				tile) != null;
	}

	@Override
	public int random(int min, int max) {
		return min + (int) (Math.random() * (max - min));
	}

	@Override
	public double random(double min, double max) {
		return min + (Math.random() * (max - min));
	}

	@Override
	public void sleep(int min, int max) {
		sleep(random(min, max));
	}

	@Override
	public void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch(InterruptedException exception) {
			throw new ThreadDeath();
		}
	}

	@Override
	public Point getTileScreenLocation(Tile tile) {
		return getWorldScreenLocation(tile.getPreciseX(), tile.getPreciseY(), 0);
	}

	@Override
	public Point getWorldScreenLocation(double x, double y, int height) {
		Point point = getLimitlessWorldScreenLocation(x, y, height);
		if(!isInGameArea(point))
			return new Point(-1, -1);
		return point;
	}

	@Override
	public Point getLimitlessWorldScreenLocation(double x, double y, int height) {
		int calcX = (int) ((x - client.getBaseX()) * 128);
		int calcY = (int) ((y - client.getBaseY()) * 128);
		int calcZ = getWorldHeight(calcX, calcY, client.getPlane()) - height;

		return calculateUnboundedRawPoint(calcX, calcY, calcZ);
	}

	private Point calculateUnboundedRawPoint(int x, int y, int z) {
		if(x < 128 || y < 128 || x > 13056 || y > 13056)
			return new Point(-1, -1);

		x -= client.getCameraX();
		z -= client.getCameraZ();
		y -= client.getCameraY();

		int pitchSine = CURVESIN[client.getCameraPitch()];
		int pitchCosine = CURVECOS[client.getCameraPitch()];
		int yawSine = CURVESIN[client.getCameraYaw()];
		int yawCosine = CURVECOS[client.getCameraYaw()];

		int angle = y * yawSine + x * yawCosine >> 16;

		y = y * yawCosine - x * yawSine >> 16;
		x = angle;
		angle = z * pitchCosine - y * pitchSine >> 16;
		y = z * pitchSine + y * pitchCosine >> 16;
		z = angle;

		if(y >= 50)
			return new Point(256 + (x << 9) / y, (angle << 9) / y + 167);
		return new Point(-1, -1);
	}

	@Override
	public Point getTileMinimapLocation(Tile tile) {
		return getWorldMinimapLocation(tile.getX(), tile.getY());
	}

	@Override
	public Point getWorldMinimapLocation(int x, int y) {
		InterfaceComponent minimap = context.getInterfaces().getComponent(548,
				85);
		if(minimap == null || !minimap.isValid())
			return new Point(-1, -1);

		x = ((x - client.getBaseX()) * 4 + 2)
				- (client.getAllPlayers()[2047].getLocalX() / 32);
		y = ((y - client.getBaseY()) * 4 + 2)
				- (client.getAllPlayers()[2047].getLocalY() / 32);

		int curveIndex = (client.getMinimapInt3() + client.getMinimapInt1()) & 2047;
		int distance = (x * x) + (y * y);
		if(distance > 6400)
			return new Point(-1, -1);

		int curveSin = (CURVESIN[curveIndex] * 256)
				/ (client.getMinimapInt2() + 256);
		int curveCos = (CURVECOS[curveIndex] * 256)
				/ (client.getMinimapInt2() + 256);

		int relativeMinimapX = (x * curveCos + y * curveSin) >> 16;
		int relativeMinimapY = (x * curveSin - y * curveCos) >> 16;

		Rectangle bounds = minimap.getBounds();
		int size = (bounds.width + bounds.height) / 2;
		// distance < 2500 ? bounds.height : bounds.width;
		int screenX = 18 + ((bounds.x + size / 2) + relativeMinimapX);
		int screenY = (bounds.y + size / 2 - 1) + relativeMinimapY;
		return new Point(screenX, screenY);
	}

	@Override
	public int getTileHeight(Tile tile) {
		return getWorldHeight(tile.getPreciseX(), tile.getPreciseY(),
				tile.getPlane());
	}

	@Override
	public int getWorldHeight(double x, double y, int plane) {
		int calcX = (int) x;
		int calcY = (int) y;
		int[][][] ground = client.getTileHeights();
		int zidx = plane;
		int x1 = calcX >> 7;
		int y1 = calcY >> 7;
		int x2 = calcX & 127;
		int y2 = calcY & 127;

		if(x1 < 0 || y1 < 0 || x1 > 103 || y1 > 103)
			return 0;

		if(zidx < 3 && (2 & client.getTileSettings()[1][x1][y1]) == 2)
			zidx++;

		int i = ground[zidx][x1 + 1][y1] * x2 + ground[zidx][x1][y1]
				* (128 - x2) >> 7;
		int j = ground[zidx][x1 + 1][y1 + 1] * x2 + ground[zidx][x1][y1 + 1]
				* (128 - x2) >> 7;

		return j * y2 + (128 - y2) * i >> 7;
	}

	@Override
	public double distanceBetween(Tile tile1, Tile tile2) {
		return Math.hypot(tile2.getPreciseX() - tile1.getPreciseX(),
				tile2.getPreciseY() - tile1.getPreciseY());
	}

	@Override
	public double realDistanceBetween(Tile tile1, Tile tile2) {
		PathNode node = generateNodePath(tile1, tile2);
		if(node == null)
			return -1;
		PathNode current = node, next = node.getNext();
		double distance = 0;
		while(next != null) {
			distance += distanceBetween(current.getLocation(),
					next.getLocation());
			current = next;
			next = current.getNext();
		}
		return distance;
	}

	@Override
	public TilePath generatePath(Tile start, Tile end) {
		PathNode node = generateNodePath(start, end);
		if(node == null)
			return null;
		return new GeneratedTilePath(context, node);
	}

	private PathNode generateNodePath(final Tile start, final Tile end) {
		Future<PathNode> task = Executors.newSingleThreadExecutor().submit(
				new Callable<PathNode>() {
					@Override
					public PathNode call() throws Exception {
						PathSearch search = searchProvider.provideSearch(start,
								end);
						while(!search.isDone() && !Thread.interrupted())
							search.step();
						if(!search.isDone())
							return null;
						return search.getPath();
					}
				});
		try {
			return task.get(5, TimeUnit.SECONDS);
		} catch(InterruptedException e) {
			throw new ThreadDeath();
		} catch(TimeoutException | ExecutionException e) {}
		return null;
	}

	@Override
	public GameContext getContext() {
		return context;
	}
}
