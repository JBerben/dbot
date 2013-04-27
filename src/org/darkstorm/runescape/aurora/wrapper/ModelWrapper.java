package org.darkstorm.runescape.aurora.wrapper;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

import ms.aurora.api.util.GrahamScan;

import org.darkstorm.runescape.api.Calculations;
import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.api.wrapper.Model;
import org.darkstorm.runescape.aurora.GameContextImpl;

public class ModelWrapper extends AbstractWrapper implements Model {
	private final ms.aurora.rt3.Model model;
	private final Locatable locatable;

	public ModelWrapper(GameContextImpl context, ms.aurora.rt3.Model model,
			Locatable locatable) {
		super(context);
		this.model = model;
		this.locatable = locatable;
	}

	/**
	 * @return The vertex count for this model
	 */
	public int getVertices() {
		return model.getVerticesX().length;
	}

	/**
	 * @return The X vertices
	 */
	public int[] getVerticesX() {
		return model.getVerticesX();
	}

	/**
	 * @return The Y vertices
	 */
	public int[] getVerticesY() {
		return model.getVerticesY();
	}

	/**
	 * @return The Z vertices
	 */
	public int[] getVerticesZ() {
		return model.getVerticesZ();
	}

	public Point[] getPoints() {
		if(!isValid())
			return null;

		Calculations calc = context.getCalculations();
		List<Point> points = new ArrayList<Point>();
		/*
		 * Generate a list of all model vertices
		 */
		// instead of using getvectors we can deal directly with vertex arrays,
		// but no point yet
		Vec3[][] vectors = getVectors();
		Tile location = locatable.getLocation();
		int gx = (int) (location.getPreciseX() * 128D);
		int gy = (int) (location.getPreciseY() * 128D);
		for(Vec3[] vecs : vectors) {
			Vec3 pa = vecs[0];
			Vec3 pb = vecs[1];
			Vec3 pc = vecs[2];

			Point a = screenLocation(gx + (int) pa.x, gy + (int) pc.x,
					0 - (int) pb.x);
			Point b = screenLocation(gx + (int) pa.y, gy + (int) pc.y,
					0 - (int) pb.y);
			Point c = screenLocation(gx + (int) pa.z, gy + (int) pc.z,
					0 - (int) pb.z);

			if(!calc.isInGameArea(a) || !calc.isInGameArea(b)
					|| !calc.isInGameArea(c))
				continue;

			points.add(a);
			points.add(b);
			points.add(c);
		}

		return points.toArray(new Point[0]);
	}

	/**
	 * Generates a convex hull outlining the click radius of this actor's model
	 * 
	 * @return
	 */
	@Override
	public Polygon getHull() {
		Point[] points = getPoints();
		if(points == null || !isValid())
			return null;

		/*
		 * Generate a convex hull from the model vertices
		 */
		Point[] hull = ConvexHull.getConvexHull(points);

		if(hull == null || hull.length == 0) {
			return null;
		}

		/*
		 * Convert the hull into a polygon, and whilst doing so, find the minimum and maximum coordinates
		 */
		int[] x = new int[hull.length];
		int[] y = new int[hull.length];
		for(int i = 0; i < hull.length; i++) {
			Point p = hull[i];
			x[i] = p.x;
			y[i] = p.y;
		}

		// create the polygon
		Polygon poly = new Polygon(x, y, x.length);
		return poly;
	}

	/**
	 * Returns an array of all polygons in this model which have been translated
	 * to screen space
	 * 
	 * @return The polygons
	 */
	@Override
	public Polygon[] getTriangles() {
		Calculations calculations = context.getCalculations();
		List<Polygon> triangles = new ArrayList<Polygon>();
		Vec3[][] vectors = getVectors();
		Tile location = locatable.getLocation();
		int gx = (int) (location.getPreciseX() * 128D);
		int gy = (int) (location.getPreciseY() * 128D);
		for(Vec3[] vecs : vectors) {
			Vec3 pa = vecs[0];
			Vec3 pb = vecs[1];
			Vec3 pc = vecs[2];

			Point p1 = screenLocation(gx + (int) pa.x, gy + (int) pc.x,
					0 - (int) pb.x);
			Point p2 = screenLocation(gx + (int) pa.y, gy + (int) pc.y,
					0 - (int) pb.y);
			Point p3 = screenLocation(gx + (int) pa.z, gy + (int) pc.z,
					0 - (int) pb.z);

			if(!calculations.isInGameArea(p1) && !calculations.isInGameArea(p2)
					&& !calculations.isInGameArea(p3))
				continue;
			triangles.add(new Polygon(new int[] { p1.x, p2.x, p3.x },
					new int[] { p1.y, p2.y, p3.y }, 3));
		}
		return triangles.toArray(new Polygon[triangles.size()]);
	}

	private Point screenLocation(int gridX, int gridY, int height) {
		double x = gridX / 128D;
		double y = gridY / 128D;
		return context.getCalculations().getWorldScreenLocation(x, y, height);
	}

	/**
	 * Convenience method to return an array of {@link Vec3}'s denoting vertices
	 * 
	 * @return The list of vertices
	 */
	public Vec3[][] getVectors() {
		Vec3[][] vectors = new Vec3[model.getTrianglesX().length][3];

		int[] amap = getTrianglesA();
		int[] bmap = getTrianglesB();
		int[] cmap = getTrianglesB();

		int[] vx = getVerticesX();
		int[] vy = getVerticesY();
		int[] vz = getVerticesZ();

		for(int i = 0; i < vectors.length; i++) {
			vectors[i][0] = new Vec3(vx[amap[i]], vx[bmap[i]], vx[cmap[i]]);
			vectors[i][1] = new Vec3(vy[amap[i]], vy[bmap[i]], vy[cmap[i]]);
			vectors[i][2] = new Vec3(vz[amap[i]], vz[bmap[i]], vz[cmap[i]]);
		}
		return vectors;
	}

	@Override
	public Point getRandomPointWithin() {
		Polygon poly = getHull();
		if(poly == null)
			return new Point(-1, -1);
		// Point centroid = centerPoint(poly);
		/*
		 * Find the minimum x, y and maximum x, y vertices
		 */
		int minX = -1;
		int minY = -1;
		int maxX = -1;
		int maxY = -1;
		for(int i = 0; i < poly.npoints; i++) {
			int px = poly.xpoints[i];
			int py = poly.ypoints[i];
			if(minX == -1 || minY == -1 || maxX == -1 || maxY == -1) {
				minX = px;
				maxX = px;
				minY = py;
				maxY = py;
			}

			if(px < minX)
				minX = px;
			if(px > maxX)
				maxX = px;
			if(py < minY)
				minY = py;
			if(py > maxY)
				maxY = py;
		}

		// safety checks
		if(minX <= 0)
			minX = 1;
		if(minY <= 0)
			minY = 1;
		if(maxX <= 1)
			maxX = 2;
		if(maxY <= 1)
			maxY = 2;

		/*
		 * Generate a random point within the polygon in regards
		 * to the min/max vertices of the polygon
		 */
		Point gen = null;
		long start = System.currentTimeMillis();
		Random rand = new Random();
		int wdev = (maxX - minX) / 4;
		int hdev = (maxY - minY) / 4;
		Point centroid = getCenterPoint(poly);
		while(gen == null) {
			int dx = (int) Math.round(rand.nextGaussian() * wdev + centroid.x);
			int dy = (int) Math.round(rand.nextGaussian() * hdev + centroid.y);
			if(poly.contains(dx, dy)
					&& context.getCalculations().isOnScreen(dx, dy)) {
				gen = new Point(dx, dy);
			}
			if(System.currentTimeMillis() - start > 10) {
				// to avoid deadlocks for uncalculateable points
				gen = new Point(-1, -1);
			}
		}

		return gen;
	}

	/**
	 * Calculates the centroid point of the polygon
	 * 
	 * @param poly
	 * @return
	 */
	@Override
	public Point getCenterPoint() {
		Polygon poly = getHull();
		return getCenterPoint(poly);
	}

	private Point getCenterPoint(Polygon poly) {
		if(poly == null)
			return new Point(-1, -1);
		Point2D[] points = new Point2D.Double[poly.npoints];
		for(int i = 0; i < points.length; i++) {
			points[i] = new Point2D.Double(poly.xpoints[i], poly.ypoints[i]);
		}
		Point2D center = centerOfMass(points);
		return new Point((int) center.getX(), (int) center.getY());
	}

	/**
	 * Function to calculate the center of mass for a given polygon, according
	 * ot the algorithm defined at
	 * http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/
	 * 
	 * @param polyPoints
	 *            array of points in the polygon
	 * @return point that is the center of mass
	 */
	public static Point2D centerOfMass(Point2D[] polyPoints) {
		double cx = 0, cy = 0;
		double area = area(polyPoints);
		// could change this to Point2D.Float if you want to use less memory
		Point2D res = new Point2D.Double();
		int i, j, n = polyPoints.length;

		double factor = 0;
		for(i = 0; i < n; i++) {
			j = (i + 1) % n;
			factor = (polyPoints[i].getX() * polyPoints[j].getY() - polyPoints[j]
					.getX() * polyPoints[i].getY());
			cx += (polyPoints[i].getX() + polyPoints[j].getX()) * factor;
			cy += (polyPoints[i].getY() + polyPoints[j].getY()) * factor;
		}
		area *= 6.0f;
		factor = 1 / area;
		cx *= factor;
		cy *= factor;
		res.setLocation(cx, cy);
		return res;
	}

	/**
	 * Function to calculate the area of a polygon, according to the algorithm
	 * defined at http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/
	 * 
	 * @param polyPoints
	 *            array of points in the polygon
	 * @return area of the polygon defined by pgPoints
	 */
	public static double area(Point2D[] polyPoints) {
		int i, j, n = polyPoints.length;
		double area = 0;

		for(i = 0; i < n; i++) {
			j = (i + 1) % n;
			area += polyPoints[i].getX() * polyPoints[j].getY();
			area -= polyPoints[j].getX() * polyPoints[i].getY();
		}
		area /= 2.0;
		return(area);
	}

	/**
	 * @return The amount of triangles in the model
	 */
	public int getTriangleCount() {
		return model.getTrianglesX().length;
	}

	/**
	 * @return The A triangles
	 */
	public int[] getTrianglesA() {
		return model.getTrianglesX();
	}

	/**
	 * @return The B triangles
	 */
	public int[] getTrianglesB() {
		return model.getTrianglesY();
	}

	/**
	 * @return The C triangles
	 */
	public int[] getTrianglesC() {
		return model.getTrianglesZ();
	}

	@Override
	public boolean isValid() {
		return model != null && model.getVerticesX().length > 0
				&& model.getVerticesY().length > 0
				&& model.getVerticesZ().length > 0;
	}

	@Override
	public int getOrientation() {
		return -1;
	}

	@Override
	public void draw(Graphics g) {
		Shape currentClip = g.getClip();
		g.setClip(context.getCalculations().getGameArea());
		for(Polygon triangle : getTriangles())
			g.drawPolygon(triangle);
		g.setClip(currentClip);
	}

	@Override
	public void fill(Graphics g) {
		Shape currentClip = g.getClip();
		g.setClip(context.getCalculations().getGameArea());
		for(Polygon triangle : getTriangles())
			g.fillPolygon(triangle);
		g.setClip(currentClip);
	}

	@Override
	public boolean contains(Point point) {
		Polygon hull = getHull();
		if(hull == null)
			return false;
		return hull.contains(point);
	}

	public ms.aurora.rt3.Model getModel() {
		return model;
	}

	/**
	 * Simple 3D vector implementation. All respective methods return the vec3
	 * instance for chaining. The class is mutable, so beware and use copy()
	 * when applicable
	 * 
	 * @author tommo
	 */
	public class Vec3 {

		// TODO really should implement some /actual/ vector operations lol

		public float x;
		public float y;
		public float z;

		public Vec3(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public Vec3 set(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
			return this;
		}

		public Vec3 add(float x, float y, float z) {
			this.x += x;
			this.y += y;
			this.z += z;
			return this;
		}

		public Vec3 add(Vec3 other) {
			add(other.x, other.y, other.z);
			return this;
		}

		public Vec3 subtract(float x, float y, float z) {
			this.x -= x;
			this.y -= y;
			this.z -= z;
			return this;
		}

		public Vec3 subtract(Vec3 other) {
			subtract(other.x, other.y, other.z);
			return this;
		}

		public Vec3 multiply(float x, float y, float z) {
			this.x *= x;
			this.y *= y;
			this.z *= z;
			return this;
		}

		public Vec3 multiply(Vec3 other) {
			multiply(other.x, other.y, other.z);
			return this;
		}

		public Vec3 multiply(float scalar) {
			x *= scalar;
			y *= scalar;
			z *= scalar;
			return this;
		}

		/**
		 * Calculates the euclidean distance between the two vectors
		 * 
		 * @param other
		 *            The vector to calculate to
		 * @return The euclidean length
		 */
		public double distance(Vec3 other) {
			return Math.sqrt((other.x - x) * (other.x - x))
					+ ((other.y - y) * (other.y - y))
					+ ((other.z - z) * (other.z - z));
		}

		public Vec3 copy() {
			return new Vec3(x, y, z);
		}

		@Override
		public String toString() {
			return "[" + x + ", " + y + ", " + z + "]";
		}

	}

	/**
	 * Generates a convex hull from a given set of 2d points
	 * 
	 * @author bkiers
	 * @author tommo
	 * @author `Discardedx2
	 */
	private static class ConvexHull {

		/**
		 * An enum denoting a directional-turn between 3 points (vectors).
		 */
		protected static enum Turn {
			CLOCKWISE,
			COUNTER_CLOCKWISE,
			COLLINEAR
		}

		/**
		 * Returns true iff all points in <code>points</code> are collinear.
		 * 
		 * @param points
		 *            the list of points.
		 * @return true iff all points in <code>points</code> are collinear.
		 */
		protected static boolean areAllCollinear(List<Point> points) {

			if(points.size() < 2) {
				return true;
			}

			final Point a = points.get(0);
			final Point b = points.get(1);

			for(int i = 2; i < points.size(); i++) {

				Point c = points.get(i);

				if(getTurn(a, b, c) != Turn.COLLINEAR) {
					return false;
				}
			}

			return true;
		}

		/**
		 * Returns the convex hull of the points created from <code>xs</code>
		 * and <code>ys</code>. Note that the first and last point in the
		 * returned <code>List&lt;java.awt.Point&gt;</code> are the same point.
		 * 
		 * @param xs
		 *            the x coordinates.
		 * @param ys
		 *            the y coordinates.
		 * @return the convex hull of the points created from <code>xs</code>
		 *         and <code>ys</code>.
		 * @throws IllegalArgumentException
		 *             if <code>xs</code> and <code>ys</code> don't have the
		 *             same size, if all points are collinear or if there are
		 *             less than 3 unique points present.
		 */
		@SuppressWarnings("unused")
		public static Point[] getConvexHull(int[] xs, int[] ys)
				throws IllegalArgumentException {

			if(xs.length != ys.length) {
				throw new IllegalArgumentException(
						"xs and ys don't have the same size");
			}

			Point[] points = new Point[xs.length];

			for(int i = 0; i < points.length; i++) {
				points[i] = new Point(xs[i], ys[i]);
			}

			return getConvexHull(points);
		}

		/**
		 * Returns the convex hull of the points created from the list
		 * <code>points</code>. Note that the first and last point in the
		 * returned <code>List&lt;java.awt.Point&gt;</code> are the same point.
		 * 
		 * @param points
		 *            the list of points.
		 * @return the convex hull of the points created from the list
		 *         <code>points</code>.
		 * @throws IllegalArgumentException
		 *             if all points are collinear or if there are less than 3
		 *             unique points present.
		 */
		public static Point[] getConvexHull(Point[] points)
				throws IllegalArgumentException {

			Set<Point> unsorted = getSortedPointSet(points);

			if(unsorted == null) {
				return null;
			}

			List<Point> sorted = new ArrayList<Point>(unsorted);

			if(sorted.size() < 3) {
				return null;
			}

			if(areAllCollinear(sorted)) {
				return null;
			}

			Stack<Point> stack = new Stack<Point>();
			stack.push(sorted.get(0));
			stack.push(sorted.get(1));

			for(int i = 2; i < sorted.size(); i++) {

				Point head = sorted.get(i);
				Point middle = stack.pop();
				Point tail = stack.peek();

				Turn turn = getTurn(tail, middle, head);

				switch(turn) {
				case COUNTER_CLOCKWISE:
					stack.push(middle);
					stack.push(head);
					break;
				case CLOCKWISE:
					i--;
					break;
				case COLLINEAR:
					stack.push(head);
					break;
				}
			}

			// close the hull
			stack.push(sorted.get(0));

			return stack.toArray(new Point[0]);
		}

		/**
		 * Returns the points with the lowest y coordinate. In case more than 1
		 * such point exists, the one with the lowest x coordinate is returned.
		 * 
		 * @param points
		 *            the list of points to return the lowest point from.
		 * @return the points with the lowest y coordinate. In case more than 1
		 *         such point exists, the one with the lowest x coordinate is
		 *         returned.
		 */
		protected static Point getLowestPoint(Point[] points) {

			if(points.length < 1) {
				return null;
			}

			Point lowest = points[0];

			for(Point temp : points) {
				if(temp == null)
					continue;

				if(temp.y < lowest.y
						|| (temp.y == lowest.y && temp.x < lowest.x)) {
					lowest = temp;
				}
			}

			return lowest;
		}

		/**
		 * Returns a sorted set of points from the list <code>points</code>. The
		 * set of points are sorted in increasing order of the angle they and
		 * the lowest point <tt>P</tt> make with the x-axis. If tow (or more)
		 * points form the same angle towards <tt>P</tt>, the one closest to
		 * <tt>P</tt> comes first.
		 * 
		 * @param points
		 *            the list of points to sort.
		 * @return a sorted set of points from the list <code>points</code>.
		 * @see GrahamScan#getLowestPoint(java.util.List)
		 */
		protected static Set<Point> getSortedPointSet(Point[] points) {

			final Point lowest = getLowestPoint(points);

			if(lowest == null) {
				return null;
			}

			TreeSet<Point> set = new TreeSet<Point>(new Comparator<Point>() {
				@Override
				public int compare(Point a, Point b) {

					if(a == b || a.equals(b)) {
						return 0;
					}

					// use longs to guard against int-underflow
					double thetaA = Math.atan2((long) a.y - lowest.y,
							(long) a.x - lowest.x);
					double thetaB = Math.atan2((long) b.y - lowest.y,
							(long) b.x - lowest.x);

					if(thetaA < thetaB) {
						return -1;
					} else if(thetaA > thetaB) {
						return 1;
					} else {
						// collinear with the 'lowest' point, let the point
						// closest to it come first

						// use longs to guard against int-over/underflow
						double distanceA = Math
								.sqrt((((long) lowest.x - a.x) * ((long) lowest.x - a.x))
										+ (((long) lowest.y - a.y) * ((long) lowest.y - a.y)));
						double distanceB = Math
								.sqrt((((long) lowest.x - b.x) * ((long) lowest.x - b.x))
										+ (((long) lowest.y - b.y) * ((long) lowest.y - b.y)));

						if(distanceA < distanceB) {
							return -1;
						} else {
							return 1;
						}
					}
				}
			});

			Collections.addAll(set, points);
			return set;
		}

		/**
		 * Returns the GrahamScan#Turn formed by traversing through the ordered
		 * points <code>a</code>, <code>b</code> and <code>c</code>. More
		 * specifically, the cross product <tt>C</tt> between the 3 points
		 * (vectors) is calculated:
		 * <tt>(b.x-a.x * c.y-a.y) - (b.y-a.y * c.x-a.x)</tt> and if <tt>C</tt>
		 * is less than 0, the turn is CLOCKWISE, if <tt>C</tt> is more than 0,
		 * the turn is COUNTER_CLOCKWISE, else the three points are COLLINEAR.
		 * 
		 * @param a
		 *            the starting point.
		 * @param b
		 *            the second point.
		 * @param c
		 *            the end point.
		 * @return the GrahamScan#Turn formed by traversing through the ordered
		 *         points <code>a</code>, <code>b</code> and <code>c</code>.
		 */
		protected static Turn getTurn(Point a, Point b, Point c) {

			// use longs to guard against int-over/underflow
			long crossProduct = (((long) b.x - a.x) * ((long) c.y - a.y))
					- (((long) b.y - a.y) * ((long) c.x - a.x));

			if(crossProduct > 0) {
				return Turn.COUNTER_CLOCKWISE;
			} else if(crossProduct < 0) {
				return Turn.CLOCKWISE;
			} else {
				return Turn.COLLINEAR;
			}
		}

	}

}
