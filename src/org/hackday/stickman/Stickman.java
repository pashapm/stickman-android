package org.hackday.stickman;

import java.util.ArrayList;
import java.util.HashSet;

import android.util.Log;

public class Stickman {
	
	protected static int OBJ_ID = 0;
	
	private static float KOEFF = 1.1f;
	
	private static int ST_LEN_VERY_SHORT = (int) (20 * KOEFF);
	private static int ST_LEN_SHORT = (int) (37 * KOEFF);
	private static int ST_LEN_AVER = (int) (ST_LEN_SHORT * 1.5 * KOEFF);
	private static int ST_LEN_LONG = (int) (ST_LEN_AVER * 1.5 * KOEFF);
	
	
	
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
		
		Point(int x, int y) {
			super(x, y);
		}
		
		Point() {
		}
		
		@Override
		public String toString() {
			return "Point["+x+","+y+"]";
		}
		
		protected int id = OBJ_ID++;
		public boolean mSelected = false;
		public boolean mBig = false;
		protected Point mBasePoint;
		protected HashSet<Point> mDerivedPoints = new HashSet<Point>();
		
		@Override
		public int hashCode() {
			return id; 
		}
		
		@Override
		public boolean equals(Object o) {
			return (o.getClass() == Point.class && id == ((Point)o).id); 
		}
	} 
	
	private HashSet<Point> mPoints = new HashSet<Point>();
	private ArrayList<Edge> mEdges = new ArrayList<Edge>();
	private Point mHeadPoint = new Point(ScreenProps.screenWidth/2, 200);;
	
	Point cent = new Point(mHeadPoint.x, mHeadPoint.y);
	Point head = new Point();
	Point lhand = new Point();
	Point rhand = new Point(); 
	Point pah = new Point();
	Point lleg = new Point();
	Point rleg = new Point();
	Point lbothand = new Point();
	Point rbothand = new Point();
	Point lbotleg = new Point();
	Point rbotleg = new Point();
	
	public Stickman() {
		
		mPoints.add(head);
		mPoints.add(cent);
		mPoints.add(lhand);
		mPoints.add(rhand);
		mPoints.add(lbothand);
		mPoints.add(rbothand);
		mPoints.add(pah);
		mPoints.add(lleg);
		mPoints.add(rleg);
		mPoints.add(lbotleg);
		mPoints.add(rbotleg);
		
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
		
		rleg.mBasePoint = pah;
		lleg.mBasePoint = pah;
		pah.mBasePoint = cent;
		lhand.mBasePoint = cent;
		rhand.mBasePoint = cent;
		head.mBasePoint = cent;
		lbothand.mBasePoint = lhand;
		rbothand.mBasePoint = rhand;
		lbotleg.mBasePoint = lleg;
		rbotleg.mBasePoint = rleg;
		
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
	
	public HashSet<Point> getPoints() {
		return mPoints;
	}
	
	public Point getHead() {
		return mHeadPoint;
	}
	
	public void selectPoint(Point p) {
		for (Point ip : getPoints()) {
			ip.mSelected = false;
		}
		
		p.mSelected = true;
	}
	
	public void setBig(Point p) {
		for (Point ip : getPoints()) {
			ip.mBig = false;
		}
		
		p.mBig = true;
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
		double alpha = getAngle(p.mBasePoint.x, p.mBasePoint.y, x, y);
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
		Edge ed = findEdge(p, p.mBasePoint);
		if (ed == null) {
			return;
		}
		int len = ed.length;
		int a = (int) (Math.sin(alpha) * len);
		int b = (int) (Math.cos(alpha) * len);
		p.set(p.mBasePoint.x + b, p.mBasePoint.y + a);
	}
}
