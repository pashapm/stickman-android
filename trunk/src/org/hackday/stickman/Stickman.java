package org.hackday.stickman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Stickman implements Cloneable {

    public static final int RESCALE_MULT = 1000;
    public static final int RELATIVE_WIDTH = (int) (1.0f* RESCALE_MULT);
    public static final int RELATIVE_HEIGHT = (int) (1.0f* RESCALE_MULT);

	private static float KOEFF = 1.1f;

    private static int ST_LEN_VERY_SHORT = (int) (RESCALE_MULT / 20 * KOEFF);
    private static int ST_LEN_SHORT = (int) (RESCALE_MULT / 10);
    private static int ST_LEN_AVER = (int) (ST_LEN_SHORT * 1.5);
    private static int ST_LEN_LONG = (int) (ST_LEN_AVER * 1.5);


	private HashMap<String, Point> mPoints = new HashMap<String, Point>();

	private ArrayList<Edge> mEdges = new ArrayList<Edge>();
    private Point mCenterPoint = new Point(RELATIVE_WIDTH / 2, RELATIVE_HEIGHT / 3, "centerPoint");

	protected Point cent = new Point(mCenterPoint.x, mCenterPoint.y, "cent");
	protected Point head = new Point("head");
	protected Point lhand = new Point("lhand");
	protected Point rhand = new Point("rhand");
	protected Point pah = new Point("pah");
	protected Point lleg = new Point("lleg");
	protected Point rleg = new Point("rleg");
	protected Point lbothand = new Point("lbothand");
	protected Point rbothand = new Point("rbothand");
	protected Point lbotleg = new Point("lbotleg");
	protected Point rbotleg = new Point("rbotleg");

	public Stickman() {

		mPoints.put(head.mName, head);
		mPoints.put(cent.mName, cent);
		mPoints.put(lhand.mName, lhand);
		mPoints.put(rhand.mName, rhand);
		mPoints.put(lbothand.mName, lbothand);
		mPoints.put(rbothand.mName, rbothand);
		mPoints.put(pah.mName, pah);
		mPoints.put(lleg.mName, lleg);
		mPoints.put(rleg.mName, rleg);
		mPoints.put(lbotleg.mName, lbotleg);
		mPoints.put(rbotleg.mName, rbotleg);

		//edges
		mEdges.add(new Edge(head, cent, ST_LEN_VERY_SHORT));
		mEdges.add(new Edge(cent, lhand, ST_LEN_SHORT));
		mEdges.add(new Edge(cent, rhand, ST_LEN_SHORT));
		mEdges.add(new Edge(lhand, lbothand, ST_LEN_SHORT));
		mEdges.add(new Edge(rhand, rbothand, ST_LEN_SHORT));
		mEdges.add(new Edge(cent, pah, ST_LEN_AVER));
		mEdges.add(new Edge(pah, lleg, ST_LEN_AVER));
		mEdges.add(new Edge(pah, rleg, ST_LEN_AVER));
		mEdges.add(new Edge(lleg, lbotleg, ST_LEN_AVER));
		mEdges.add(new Edge(rleg, rbotleg, ST_LEN_AVER));

		rleg.mBasePoint = "pah";
		lleg.mBasePoint = "pah";
		pah.mBasePoint = "cent";
		lhand.mBasePoint = "cent";
		rhand.mBasePoint = "cent";
		head.mBasePoint = "cent";
		lbothand.mBasePoint = "lhand";
		rbothand.mBasePoint = "rhand";
		lbotleg.mBasePoint = "lleg";
		rbotleg.mBasePoint = "rleg";

		setBig(head);

		setAngle(lhand, Math.PI);
		setAngle(rhand, 0);
		setAngle(pah, Math.PI/2);

		setAngle(head, -Math.PI/2);
		setAngle(lleg, Math.PI/4);
		setAngle(rleg, Math.PI*3/4);
		setAngle(lbothand, Math.PI*1/4);
		setAngle(rbothand, Math.PI*3/4);
		setAngle(lbotleg, Math.PI*1/4);
		setAngle(rbotleg, Math.PI*3/4);
	}

	public ArrayList<Edge> getEdges() {
		return mEdges;
	}

	public HashMap<String, Point> getPoints() {
		return mPoints;
	}
	
	public void setPoints(HashMap<String, Point> points) {
		mPoints = points;
	}

	public Point getHead() {
		return mCenterPoint;
	}

	public void selectPoint(Point p) {
		for (Point ip : getPoints().values()) {
			ip.mSelected = false;
		}

		p.mSelected = true;
	}

	public void setBig(Point p) {
		for (Point ip : getPoints().values()) {
			ip.mBig = false;
		}

		p.mBig = true;
	}

	private Point getPoint(String name) {
		return getPoints().get(name);
	}

	private double getAngle(int x1, int y1, int x2, int y2) {
		int dx = x2 - x1;
		int dy = y2 - y1;
		double lenght = (int) Math.hypot(dx, dy);

		double alpha = Math.asin( dy / lenght );
    	if (dx > 0) {

    	} else {
    		alpha = Math.PI - alpha;
    	}
    	return alpha;
	}

	public void setAngle(Point p, int x, int y) {
		if (p.mBasePoint == null) {
			return;
		}
		double alpha = getAngle(getPoint(p.mBasePoint).x, getPoint(p.mBasePoint).y, x, y);
		setAngle(p, alpha);
	}

	public void move(Point p, int x, int y, int dx, int dy) {
		if (p == cent) {
//			Log.d("!!!", dx + " "+dy);
//			for (Point po : getPoints()) {
//				po.x += dx;
//				po.y += dy;
//			}
		} else {
			setAngle(p, x, y);
		}
	}

	private Edge findEdge(Point p1, Point p2) {
		for (Edge ed : getEdges()) {
			if ((ed.mEnd == p1 && ed.mStart == p2)
					|| (ed.mEnd == p2 && ed.mStart == p1)) {
				return ed;
			}
		}
		return null;
	}

	public void setAngle(Point p, double alpha) {
		Edge ed = findEdge(p, getPoint(p.mBasePoint));
		if (ed == null) {
			return;
		}
		int len = ed.length;
		int a = (int) (Math.sin(alpha) * len);
		int b = (int) (Math.cos(alpha) * len);
		p.set(getPoint(p.mBasePoint).x + b, getPoint(p.mBasePoint).y + a);
	}

	public void set(Stickman another) {
		for (String pname : another.getPoints().keySet()) {
			getPoint(pname).set( another.getPoint(pname) );
		}
	}
	
	public static ArrayList<Stickman> getIntermediateFrames(Stickman st1, Stickman st2, int framenum) {
		
		ArrayList<Stickman> interframes = new ArrayList<Stickman>(framenum);
		
		for(int i=0; i<framenum; ++i) { //for each frame
			
			HashMap<String, Point> points = new HashMap<String, Point>();
			
			for (String pname : st1.getPoints().keySet()) {
				Point st1p = st1.getPoint(pname);
				Point st2p = st2.getPoint(pname);
				
				int xdiff = st2p.x - st1p.x;
				int ydiff = st2p.y - st1p.y;
				
				int xquant = xdiff / framenum; 
				int yquant = ydiff / framenum; 
				
				Point newp = new Point(pname);
				newp.set(st1p);
				newp.set(newp.x + i*xquant, newp.y + i*yquant);
				points.put(pname, newp);
			}
			
			Stickman st = new Stickman();
			st.setPoints(points);
			interframes.add(st);
		}
		
		return interframes;
	}
}

class Edge {
	public Point mStart;
	public Point mEnd;
	public int length;

	public Edge(Point start, Point end, int length) {
		mStart = start;
		mEnd = end;
		this.length = length;
	}

	public double getLength() {
		return Math.hypot(mStart.x - mEnd.x, mStart.y - mEnd.y);
	}
}

class Point extends android.graphics.Point {

	protected static int OBJ_ID = 0;
	
	Point(int x, int y, String name) {
		super(x, y);
		mName = name;
	}

	Point(String name) {
		mName = name;
	}

	@Override
	public String toString() {
		return "Point["+x+","+y+"]";
	}

	public String mName;
	
	protected int id = OBJ_ID++;
	public boolean mSelected = false;
	public boolean mBig = false;
	protected String mBasePoint;
	protected HashSet<String> mDerivedPoints = new HashSet<String>();

	@Override
	public int hashCode() {
		return id;
	}

	public void set(Point psource) {
		x = psource.x;
		y = psource.y;
		mSelected = psource.mSelected;
		mBig = psource.mBig;
	}
}