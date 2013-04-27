package org.darkstorm.runescape.aurora;

import java.awt.*;
import java.util.*;
import java.util.List;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.api.Walking;
import org.darkstorm.runescape.api.input.*;
import org.darkstorm.runescape.api.pathfinding.GeneratedTilePath;
import org.darkstorm.runescape.api.tab.SettingsTab;
import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.api.wrapper.*;
import org.darkstorm.runescape.event.*;
import org.darkstorm.runescape.event.EventListener;
import org.darkstorm.runescape.event.game.PaintEvent;
import org.darkstorm.runescape.util.UnsupportedGameTypeException;

public final class WalkingImpl extends AbstractUtility implements Walking,
		EventListener {
	private TilePath path;

	public WalkingImpl(GameContextImpl context) {
		super(context);

		context.getBot().getEventManager().registerListener(this);
	}

	@EventHandler
	public void onPaint(PaintEvent event) {
		Graphics2D g = (Graphics2D) event.getGraphics();
		TilePath path = this.path;
		if(path != null && path.isValid()) {
			Tile[] tiles;
			if(path instanceof FixedTilePath)
				tiles = ((FixedTilePath) path).getTiles();
			else if(path instanceof GeneratedTilePath)
				tiles = ((GeneratedTilePath) path).getFixedPath().getTiles();
			else if(path instanceof LocalCalculatedTilePath) {
				TilePath generatedPath = ((LocalCalculatedTilePath) path).tilePath;
				if(generatedPath != null)
					tiles = ((FixedTilePath) generatedPath).getTiles();
				else
					return;
			} else
				return;
			g.setColor(Color.RED);
			Map<?, ?> renderingHints = g.getRenderingHints();
			Stroke oldStroke = g.getStroke();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setStroke(new BasicStroke(2f));
			Tile self = context.getPlayers().getSelf().getLocation();
			Point lastScreenPoint = null;
			for(Tile tile : tiles) {
				if(self.distanceTo(tile) > 17)
					continue;
				Point screenPoint = calculations.getTileMinimapLocation(tile);
				if(lastScreenPoint != null)
					g.drawLine(lastScreenPoint.x, lastScreenPoint.y,
							screenPoint.x, screenPoint.y);
				lastScreenPoint = screenPoint;
			}
			g.setStroke(oldStroke);
			g.setRenderingHints(renderingHints);
		}
	}

	@Override
	public MouseTarget getMinimapTileTarget(Tile tile) {
		Point screenPos = calculations.getTileMinimapLocation(tile);
		if(screenPos.x == -1 || screenPos.y == -1)
			return null;
		return new PointMouseTarget(screenPos, 0);
	}

	@Override
	public boolean walkTo(Tile destination) {
		return walkTo(destination, WalkMode.EITHER);
	}

	@Override
	public boolean walkTo(Tile destination, WalkMode mode) {
		if(isOnMinimap(destination))
			return walkLocallyTo(destination, mode);
		return walkPath(
				calculations.generatePath(context.getPlayers().getSelf()
						.getLocation(), destination), mode);
	}

	@Override
	public boolean walkPath(TilePath path) {
		return walkPath(path, WalkMode.EITHER);
	}

	@Override
	public synchronized boolean walkPath(TilePath path, WalkMode mode) {
		try {
			this.path = path;
			final Player self = context.getPlayers().getSelf();
			int tries = 0;
			while(true) {
				if(tries > 5)
					return false;
				tries++;
				if(!path.isValid())
					return self.getLocation().equals(path.getEnd());
				Tile tile = path.getNext();
				if(tile == null) {
					calculations.sleep(50);
					continue;
				}
				Tile location = self.getLocation();
				if(((isOnMinimap(tile) && calculations.random(1, 4) == 2) || !self
						.isMoving()) && !walkLocallyTo(tile, WalkMode.EITHER)) {
					calculations.sleep(50);
					continue;
				}
				calculations.sleep(350);
				if(self.isMoving()) {
					tries = 0;
					calculations.sleep(800, 1200);
				}
				calculations.sleep(100, 150);

				Tile next = path.getNext();
				if(next != null && next.distanceTo(path.getEnd()) < 3) {
					while(self.isMoving())
						calculations.sleep(10, 50);
					location = self.getLocation();
					if(location.distanceTo(path.getEnd()) < 3)
						return true;
				}
			}
		} finally {
			this.path = null;
		}
	}

	@Override
	public boolean canReach(Locatable target) {
		return calculations.canReach(target.getLocation());
	}

	@Override
	public double distanceTo(Locatable target) {
		return calculations.realDistanceBetween(context.getPlayers().getSelf()
				.getLocation(), target.getLocation());
	}

	private boolean walkLocallyTo(Tile destination, WalkMode mode) {
		double distance = destination.distanceTo(context.getPlayers().getSelf()
				.getLocation());
		if(distance > 17)
			return false;
		boolean targetVisible = calculations.isInGameArea(calculations
				.getTileScreenLocation(destination));
		if(mode == WalkMode.EITHER)
			if(targetVisible
					&& calculations.random(3, Math.max(3, distance / 2)) == 3)
				mode = WalkMode.SCREEN;
			else
				mode = WalkMode.MINIMAP;
		if(mode == WalkMode.SCREEN)
			if(targetVisible)
				return context.getMenu().perform(
						new TileMouseTarget(context, destination), "Walk");
			else
				return false;
		else
			mouse.click(new PointMouseTarget(calculations
					.getTileMinimapLocation(destination), 1));
		return true;
	}

	@Override
	public boolean isRunning() {
		return context.getGame().getTab(SettingsTab.class).isRunning();
	}

	@Override
	public void setRunning(boolean running) {
		if(running == isRunning())
			return;
		SettingsTab tab = context.getGame().getTab(SettingsTab.class);
		if(!tab.isOpen()) {
			tab.open();
			calculations.sleep(250, 500);
			if(!tab.isOpen())
				return;
		}
		tab.setRunning(running);
	}

	@Override
	public boolean isResting() {
		throw new UnsupportedGameTypeException(GameType.CURRENT);
	}

	@Override
	public void setResting(boolean resting) {
		throw new UnsupportedGameTypeException(GameType.CURRENT);
	}

	@Override
	public int getRunEnergy() {
		return context.getGame().getTab(SettingsTab.class).getRunPercentage();
	}

	@Override
	public boolean hasMinimapDestination() {
		Player self = context.getPlayers().getSelf();
		if(self == null)
			return false;
		return self.isMoving();
	}

	@Override
	public boolean isOnMinimap(Tile tile) {
		return context.getPlayers().getSelf().getLocation().distanceTo(tile) < 17;
	}

	@Override
	public Tile getMinimapDestination() {
		Player self = context.getPlayers().getSelf();
		if(self == null || !self.isMoving())
			return null;
		Tile[] waypoints = self.getWaypoints();
		return waypoints[waypoints.length - 1];
	}

	@Override
	public InterfaceComponent getRunComponent() {
		return null;
	}

	public class LocalCalculatedTilePath implements TilePath {
		public static final int WALL_NORTH_WEST = 0x1;
		public static final int WALL_NORTH = 0x2;
		public static final int WALL_NORTH_EAST = 0x4;
		public static final int WALL_EAST = 0x8;
		public static final int WALL_SOUTH_EAST = 0x10;
		public static final int WALL_SOUTH = 0x20;
		public static final int WALL_SOUTH_WEST = 0x40;
		public static final int WALL_WEST = 0x80;
		public static final int BLOCKED = 0x100;
		public static final int INVALID = 0x200000;

		protected Tile end;
		protected Tile base;
		protected int[][] flags;
		protected int offX, offY;

		private TilePath tilePath;

		private LocalCalculatedTilePath(Tile end) {
			this.end = end;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isValid() {
			return getNext() != null
					&& !context.getPlayers().getSelf().getLocation()
							.equals(getEnd());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Tile getNext() {
			int baseX = context.getGame().getRegionBaseX(), baseY = context
					.getGame().getRegionBaseY();
			Tile base = new Tile(baseX, baseY, context.getGame()
					.getCurrentFloor());
			if(this.base == null || !this.base.equals(base)) {
				int[][] flags = context.getGame().getTileCollisionData();
				if(flags != null) {
					this.base = base;
					Tile start = context.getPlayers().getSelf().getLocation();
					Tile[] tiles = findPath(start, end);
					if(tiles == null) {
						this.base = null;
						return null;
					}
					tilePath = new FixedTilePath(context, tiles);
				}
			}
			return tilePath.getNext();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Tile getStart() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Tile getEnd() {
			return end;
		}

		/**
		 * Returns the calculated TilePath that is currently providing data to
		 * this RSLocalPath.
		 * 
		 * @return The current Tile path; or <code>null</code>.
		 */
		public TilePath getCurrentTilePath() {
			return tilePath;
		}

		protected class Node {

			public int x, y;
			public Node prev;
			public double g, f;
			public boolean border;

			public Node(int x, int y, boolean border) {
				this.border = border;
				this.x = x;
				this.y = y;
				g = f = 0;
			}

			public Node(int x, int y) {
				this.x = x;
				this.y = y;
				g = f = 0;
			}

			@Override
			public int hashCode() {
				return (x << 4) | y;
			}

			@Override
			public boolean equals(Object o) {
				if(o instanceof Node) {
					Node n = (Node) o;
					return x == n.x && y == n.y;
				}
				return false;
			}

			@Override
			public String toString() {
				return "(" + x + "," + y + ")";
			}

			public Tile toTile(int baseX, int baseY) {
				return new Tile(x + baseX, y + baseY);
			}

		}

		protected Tile[] findPath(Tile start, Tile end) {
			return findPath(start, end, false);
		}

		private Tile[] findPath(Tile start, Tile end, boolean remote) {
			int base_x = base.getX(), base_y = base.getY();
			int curr_x = start.getX() - base_x, curr_y = start.getY() - base_y;
			int dest_x = end.getX() - base_x, dest_y = end.getY() - base_y;

			// load client data
			int plane = context.getGame().getCurrentFloor();
			flags = context.getGame().getTileCollisionData();
			Tile offset = getCollisionOffset(plane);
			offX = offset.getX();
			offY = offset.getY();

			// loaded region only
			if(flags == null || curr_x < 0 || curr_y < 0
					|| curr_x >= flags.length || curr_y >= flags.length) {
				return null;
			} else if(dest_x < 0 || dest_y < 0 || dest_x >= flags.length
					|| dest_y >= flags.length) {
				remote = true;
				if(dest_x < 0) {
					dest_x = 0;
				} else if(dest_x >= flags.length) {
					dest_x = flags.length - 1;
				}
				if(dest_y < 0) {
					dest_y = 0;
				} else if(dest_y >= flags.length) {
					dest_y = flags.length - 1;
				}
			}

			// structs
			HashSet<Node> open = new HashSet<Node>();
			HashSet<Node> closed = new HashSet<Node>();
			Node curr = new Node(curr_x, curr_y);
			Node dest = new Node(dest_x, dest_y);

			curr.f = heuristic(curr, dest);
			open.add(curr);

			// search
			while(!open.isEmpty()) {
				curr = lowest_f(open);
				if(curr.equals(dest)) {
					// reconstruct from pred tree
					return path(curr, base_x, base_y);
				}
				open.remove(curr);
				closed.add(curr);
				for(Node next : successors(curr)) {
					if(!closed.contains(next)) {
						double t = curr.g + dist(curr, next);
						boolean use_t = false;
						if(!open.contains(next)) {
							open.add(next);
							use_t = true;
						} else if(t < next.g) {
							use_t = true;
						}
						if(use_t) {
							next.prev = curr;
							next.g = t;
							next.f = t + heuristic(next, dest);
						}
					}
				}
			}

			// no path
			if(!remote
					|| context.getPlayers().getSelf().getLocation()
							.distanceTo(end) < 10) {
				return null;
			}
			return findPath(start, pull(end));
		}

		private Tile getCollisionOffset(int plane) {
			return new Tile(0, 0);
		}

		private Tile pull(Tile tile) {
			Tile p = context.getPlayers().getSelf().getLocation();
			int x = tile.getX(), y = tile.getY();
			if(x - base.getX() < 0)
				x = base.getX() + (x - base.getX()) / 2;
			else if(x - base.getX() > 104)
				x = base.getX() + 104 + (x - base.getX() - 104) / 2;
			if(y - base.getY() < 0)
				y = base.getY() + (y - base.getY()) / 2;
			else if(y - base.getY() > 104)
				y = base.getY() + 104 + (y - base.getY() - 104) / 2;
			if(p.getX() < x)
				x -= 2;
			else if(p.getX() > x)
				x += 2;
			if(p.getY() < y)
				y -= 2;
			else if(p.getY() > y)
				y += 2;
			return new Tile(x, y);
		}

		private double heuristic(Node start, Node end) {
			double dx = start.x - end.x;
			double dy = start.y - end.y;
			if(dx < 0)
				dx = -dx;
			if(dy < 0)
				dy = -dy;
			return dx < dy ? dy : dx;
			// double diagonal = dx > dy ? dy : dx;
			// double manhattan = dx + dy;
			// return 1.41421356 * diagonal + (manhattan - 2 * diagonal);
		}

		private double dist(Node start, Node end) {
			if(start.x != end.x && start.y != end.y) {
				return 1.41421356;
			} else {
				return 1.0;
			}
		}

		private Node lowest_f(Set<Node> open) {
			Node best = null;
			for(Node t : open) {
				if(best == null || t.f < best.f) {
					best = t;
				}
			}
			return best;
		}

		private Tile[] path(Node end, int base_x, int base_y) {
			LinkedList<Tile> path = new LinkedList<Tile>();
			Node p = end;
			while(p != null) {
				path.addFirst(p.toTile(base_x, base_y));
				p = p.prev;
			}
			return path.toArray(new Tile[path.size()]);
		}

		private List<Node> successors(Node t) {
			LinkedList<Node> tiles = new LinkedList<Node>();
			int x = t.x, y = t.y;
			int f_x = x - offX, f_y = y - offY;
			int here = flags[f_x][f_y];
			int upper = flags.length - 1;
			if(f_y > 0 && (here & WALL_SOUTH) == 0
					&& (flags[f_x][f_y - 1] & BLOCKED) == 0
					&& (flags[f_x][f_y - 1] & INVALID) == 0
					&& checkHeightMap(x, y - 1)) {
				tiles.add(new Node(x, y - 1));
			}
			if(f_x > 0 && (here & WALL_WEST) == 0
					&& (flags[f_x - 1][f_y] & BLOCKED) == 0
					&& (flags[f_x - 1][f_y] & INVALID) == 0
					&& checkHeightMap(x - 1, y)) {
				tiles.add(new Node(x - 1, y));
			}
			if(f_y < upper && (here & WALL_NORTH) == 0
					&& (flags[f_x][f_y + 1] & BLOCKED) == 0
					&& (flags[f_x][f_y + 1] & INVALID) == 0
					&& checkHeightMap(x, y + 1)) {
				tiles.add(new Node(x, y + 1));
			}
			if(f_x < upper && (here & WALL_EAST) == 0
					&& (flags[f_x + 1][f_y] & BLOCKED) == 0
					&& (flags[f_x + 1][f_y] & INVALID) == 0
					&& checkHeightMap(x + 1, y)) {
				tiles.add(new Node(x + 1, y));
			}
			if(f_x > 0 && f_y > 0
					&& (here & (WALL_SOUTH_WEST | WALL_SOUTH | WALL_WEST)) == 0
					&& (flags[f_x - 1][f_y - 1] & BLOCKED) == 0
					&& (flags[f_x][f_y - 1] & (BLOCKED | WALL_WEST)) == 0
					&& (flags[f_x - 1][f_y] & (BLOCKED | WALL_SOUTH)) == 0
					&& (flags[f_x - 1][f_y - 1] & INVALID) == 0
					&& (flags[f_x][f_y - 1] & INVALID) == 0
					&& (flags[f_x - 1][f_y] & INVALID) == 0
					&& checkHeightMap(x - 1, y - 1)) {
				tiles.add(new Node(x - 1, y - 1));
			}
			if(f_x > 0 && f_y < upper
					&& (here & (WALL_NORTH_WEST | WALL_NORTH | WALL_WEST)) == 0
					&& (flags[f_x - 1][f_y + 1] & BLOCKED) == 0
					&& (flags[f_x][f_y + 1] & (BLOCKED | WALL_WEST)) == 0
					&& (flags[f_x - 1][f_y] & (BLOCKED | WALL_NORTH)) == 0
					&& (flags[f_x - 1][f_y + 1] & INVALID) == 0
					&& (flags[f_x][f_y + 1] & INVALID) == 0
					&& (flags[f_x - 1][f_y] & INVALID) == 0
					&& checkHeightMap(x - 1, y + 1)) {
				tiles.add(new Node(x - 1, y + 1));
			}
			if(f_x < upper && f_y > 0
					&& (here & (WALL_SOUTH_EAST | WALL_SOUTH | WALL_EAST)) == 0
					&& (flags[f_x + 1][f_y - 1] & BLOCKED) == 0
					&& (flags[f_x][f_y - 1] & (BLOCKED | WALL_EAST)) == 0
					&& (flags[f_x + 1][f_y] & (BLOCKED | WALL_SOUTH)) == 0
					&& (flags[f_x + 1][f_y - 1] & INVALID) == 0
					&& (flags[f_x][f_y - 1] & INVALID) == 0
					&& (flags[f_x + 1][f_y] & INVALID) == 0
					&& checkHeightMap(x + 1, y - 1)) {
				tiles.add(new Node(x + 1, y - 1));
			}
			if(f_x > 0 && f_y < upper
					&& (here & (WALL_NORTH_EAST | WALL_NORTH | WALL_EAST)) == 0
					&& (flags[f_x + 1][f_y + 1] & BLOCKED) == 0
					&& (flags[f_x][f_y + 1] & (BLOCKED | WALL_EAST)) == 0
					&& (flags[f_x + 1][f_y] & (BLOCKED | WALL_NORTH)) == 0
					&& (flags[f_x + 1][f_y + 1] & INVALID) == 0
					&& (flags[f_x][f_y + 1] & INVALID) == 0
					&& (flags[f_x + 1][f_y] & INVALID) == 0
					&& checkHeightMap(x + 1, y + 1)) {
				tiles.add(new Node(x + 1, y + 1));
			}
			return tiles;
		}

		private boolean checkHeightMap(int x, int y) {
			x += context.getGame().getRegionBaseX();
			y += context.getGame().getRegionBaseY();
			int plane = context.getGame().getCurrentFloor();
			int[] heights = new int[] {
					-calculations.getWorldHeight(x, y, plane),
					-calculations.getWorldHeight(x + 0.5, y, plane),
					-calculations.getWorldHeight(x + 0.5, y + 0.5, plane),
					-calculations.getWorldHeight(x, y + 0.5, plane), };
			int lowest = Integer.MAX_VALUE, highest = Integer.MIN_VALUE;
			for(int i = 0; i < heights.length; i++) {
				if(heights[i] < lowest)
					lowest = heights[i];
				if(heights[i] > highest)
					highest = heights[i];
			}
			return (highest - lowest) / 128D < 1;
		}
	}
}