package com.example.demomap.response;

import java.util.List;

public class StepsItem{
	private double duration;
	private int orientation;
	private String durationText;
	private double distance;
	private StartLocation startLocation;
	private String instruction;
	private String action;
	private String distanceText;
	private EndLocation endLocation;
	private List<PolylineItem> polyline;
	private String roadName;

	public double getDuration(){
		return duration;
	}

	public int getOrientation(){
		return orientation;
	}

	public String getDurationText(){
		return durationText;
	}

	public double getDistance(){
		return distance;
	}

	public StartLocation getStartLocation(){
		return startLocation;
	}

	public String getInstruction(){
		return instruction;
	}

	public String getAction(){
		return action;
	}

	public String getDistanceText(){
		return distanceText;
	}

	public EndLocation getEndLocation(){
		return endLocation;
	}

	public List<PolylineItem> getPolyline(){
		return polyline;
	}

	public String getRoadName(){
		return roadName;
	}
}