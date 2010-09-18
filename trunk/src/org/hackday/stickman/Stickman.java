package org.hackday.stickman;

import java.util.ArrayList;
import java.util.Vector;

import android.graphics.Point;

public class Stickman {
	
	private static int ST_LEN = 50;
	
	class Edge {
		public Point mStart;
		public Point mEnd;
		
		public Edge(Point start, Point end) {
			mStart = start;
			mEnd = end;
		}
	}
	
	private ArrayList<Point> mPoints = new ArrayList<Point>();
	private ArrayList<Edge> mEdges = new ArrayList<Edge>();
	private Point mHeadPoint;
	
	public Stickman() {
		mHeadPoint = new Point(ScreenProps.screenWidth/2, 30);
		
		Point cent = new Point(mHeadPoint.x, mHeadPoint.y);
		Point lhand = new Point(mHeadPoint.x-ST_LEN, mHeadPoint.y);
		Point rhand = new Point(mHeadPoint.x+ST_LEN, mHeadPoint.y);
		Point pah = new Point(mHeadPoint.x, mHeadPoint.y+ST_LEN);
		
		double alpha = Math.PI/4;
		int b = (int) (ST_LEN /  (1 + Math.sin(alpha)*Math.sin(alpha)));
		int a = (int) (b * Math.sin(alpha));
		
		Point lleg = new Point(pah.x+a, pah.y+b);
		Point rleg = new Point(pah.x-a, pah.y+b);
		
		mPoints.add(cent);
		mPoints.add(lhand);
		mPoints.add(rhand);
		mPoints.add(rhand);
		mPoints.add(lleg);
		mPoints.add(rleg);
		
		//edges 
		
		mEdges.add(new Edge(cent, lhand));
		mEdges.add(new Edge(cent, rhand));
		mEdges.add(new Edge(cent, pah));
		mEdges.add(new Edge(pah, lleg));
		mEdges.add(new Edge(pah, rleg));
	}
	
	public ArrayList<Edge> getEdges() {
		return mEdges;
	}
	
	public ArrayList<Point> getPoints() {
		return mPoints;
	}
	
}
