package com.example.demomap.response;

import java.util.List;

public class RouteResponse {
	private List<RoutesItem> routes;
	private String returnCode;
	private String returnDesc;

	public List<RoutesItem> getRoutes(){
		return routes;
	}

	public String getReturnCode(){
		return returnCode;
	}

	public String getReturnDesc(){
		return returnDesc;
	}
}