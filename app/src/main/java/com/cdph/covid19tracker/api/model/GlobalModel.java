package com.cdph.covid19tracker.api.model;

import com.google.gson.annotations.SerializedName;

public class GlobalModel
{
	@SerializedName("cases")
	private int cases;
	
	@SerializedName("deaths")
	private int deaths;
	
	@SerializedName("recovered")
	private int recovered;
	
	public int getCases()
	{
		return cases;
	}
	
	public int getDeaths()
	{
		return deaths;
	}
	
	public int getRecovered()
	{
		return recovered;
	}
}
