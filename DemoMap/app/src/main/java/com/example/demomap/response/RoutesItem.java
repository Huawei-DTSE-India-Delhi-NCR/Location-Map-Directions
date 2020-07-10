package com.example.demomap.response;

import java.util.List;

public class RoutesItem{
	private List<PathsItem> paths;
	private Bounds bounds;

	public List<PathsItem> getPaths(){
		return paths;
	}

	public Bounds getBounds(){
		return bounds;
	}
}