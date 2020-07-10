package com.example.demomap.response;

import java.util.List;

public class PathsItem{
	private double duration;
	private String durationText;
	private String durationInTrafficText;
	private double durationInTraffic;
	private double distance;
	private StartLocation startLocation;
	private String startAddress;
	private String distanceText;
	private List<StepsItem> steps;
	private EndLocation endLocation;
	private String endAddress;

	public double getDuration(){
		return duration;
	}

	public String getDurationText(){
		return durationText;
	}

	public String getDurationInTrafficText(){
		return durationInTrafficText;
	}

	public double getDurationInTraffic(){
		return durationInTraffic;
	}

	public double getDistance(){
		return distance;
	}

	public StartLocation getStartLocation(){
		return startLocation;
	}

	public String getStartAddress(){
		return startAddress;
	}

	public String getDistanceText(){
		return distanceText;
	}

	public List<StepsItem> getSteps(){
		return steps;
	}

	public EndLocation getEndLocation(){
		return endLocation;
	}

	public String getEndAddress(){
		return endAddress;
	}
}