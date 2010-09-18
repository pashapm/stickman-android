package org.hackday.stickman;

import java.util.ArrayList;
import java.util.HashSet;

import android.util.Log;

public class Stickman {
	
	private static int ST_LEN = 100;
	private static int ST_LEN_SHORT = ST_LEN / 3;
	
	class Edge {
		public Point mStart;
		public Point mEnd;
		
		public Edge(Point start, Point end) {
			mStart = start;
			mEnd = end;
			Log.d("!!! Edge", ""+getLength());
		}
		
		public double getLength() {
			return Math.hypot(mStart.x - mEnd.x, mStart.y - mEnd.y);
		}
	}
	
	class Point extends android.graphics.Point {
		
		Point(int x, int y) {
			super(x, y);
		}
		
		@Override
		public String toString() {
			return "Point["+x+","+y+"]";
		}
		
		public boolean mSelected = false;
		public boolean mBig = false;
		protected Point mBasePoint;
		protected HashSet<Point> mDerivedPoints = new HashSet<Point>();
	} 
	
	private HashSet<Point> mPoints = new HashSet<Point>();
	private ArrayList<Edge> mEdges = new ArrayList<Edge>();
	private Point mHeadPoint;
	
	public Stickman() {
		mHeadPoint = new Point(ScreenProps.screenWidth/2, 200);
		
		Point head = new Point(mHeadPoint.x, mHeadPoint.y-ST_LEN/3);
		Point cent = new Point(mHeadPoint.x, mHeadPoint.y);
		Point lhand = new Point(mHeadPoint.x-ST_LEN, mHeadPoint.y);
		Point rhand = new Point(mHeadPoint.x+ST_LEN, mHeadPoint.y);
		Point pah = new Point(mHeadPoint.x, mHeadPoint.y+ST_LEN);
		
		double alpha = Math.PI/4;
		int b = (int) (ST_LEN /  Math.sqrt(1 + Math.sin(alpha)*Math.sin(alpha)));
		int a = (int) (b * Math.sin(alpha));
		
		Point lleg = new Point(pah.x+a, pah.y+b);
		Point rleg = new Point(pah.x-a, pah.y+b);
		
		mPoints.add(head);
		mPoints.add(cent);
		mPoints.add(lhand);
		mPoints.add(rhand);
		mPoints.add(pah);
		mPoints.add(lleg);
		mPoints.add(rleg);
		
		//edges 
		mEdges.add(new Edge(head, cent));
		mEdges.add(new Edge(cent, lhand));
		mEdges.add(new Edge(cent, rhand));
		mEdges.add(new Edge(cent, pah));
		mEdges.add(new Edge(pah, lleg));
		mEdges.add(new Edge(pah, rleg));
		
		rleg.mBasePoint = pah;
		lleg.mBasePoint = pah;
		pah.mBasePoint = cent;
		lhand.mBasePoint = cent;
		rhand.mBasePoint = cent;
		head.mBasePoint = cent;
		
		setBig(head);
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
		int len = p.mBig ? ST_LEN_SHORT : ST_LEN;
		int a = (int) (Math.sin(alpha) * len);
		int b = (int) (Math.cos(alpha) * len);
		p.set(p.mBasePoint.x + b, p.mBasePoint.y + a);
	}
}
