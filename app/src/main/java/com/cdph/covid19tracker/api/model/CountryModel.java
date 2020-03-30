package com.cdph.covid19tracker.api.model;

import com.google.gson.annotations.SerializedName;

public class CountryModel
{
	@SerializedName("country")
	private String country;
	
	@SerializedName("cases")
	private int cases;
	
	@SerializedName("todayCases")
	private int todayCases;
	
	@SerializedName("deaths")
	private int deaths;
	
	@SerializedName("todayDeaths")
	private int todayDeaths;
	
	@SerializedName("recovered")
	private int recovered;
	
	@SerializedName("active")
	private int active;
	
	@SerializedName("critical")
	private int critical;
	
	@SerializedName("casesPerOneMillion")
	private int casesPerOneMillion;
	
	@SerializedName("deathsPerOneMillion")
	private int deathsPerOneMillion;
	
	public String getCountry()
	{
		return country;
	}
	
	public int getCases()
	{
		return cases;
	}
	
	public int getTodayCases()
	{
		return todayCases;
	}
	
	public int getDeaths()
	{
		return deaths;
	}
	
	public int getTodayDeaths()
	{
		return todayDeaths;
	}
	
	public int getRecovered()
	{
		return recovered;
	}
	
	public int getActive()
	{
		return active;
	}
	
	public int getCritical()
	{
		return critical;
	}
	
	public int getCasesPerOneMillion()
	{
		return casesPerOneMillion;
	}
	
	public int getDeathsPerOneMillion()
	{
		return deathsPerOneMillion;
	}
}
