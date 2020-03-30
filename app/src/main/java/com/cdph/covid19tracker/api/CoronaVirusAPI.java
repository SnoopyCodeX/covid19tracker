package com.cdph.covid19tracker.api;

import android.content.Context;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.cdph.covid19tracker.api.model.CountryModel;
import com.cdph.covid19tracker.api.model.GlobalModel;

public class CoronaVirusAPI
{
	private static final String BASE_URL = "https://coronavirus-19-api.herokuapp.com";
    private static final Gson gson = new Gson();
	private static CoronaVirusAPI.OnRequestListener listener;
	private static Context ctx;
	
	private CoronaVirusAPI(Context ctx)
	{
		this.ctx = ctx;
	}
	
	public CoronaVirusAPI setOnRequestListener(CoronaVirusAPI.OnRequestListener listener)
	{
		this.listener = listener;
		return this;
	}
	
	public CountryModel getDataByCountryName(String countryName)
	{
		return (gson.fromJson(getJsonObject("/countries/" + countryName), CountryModel.class));
	}
	
	public GlobalModel getGlobalData()
	{
		return (gson.fromJson(getJsonObject("/all"), GlobalModel.class));
	}
	
	public ArrayList<CountryModel> getAllDataFromCountries()
	{
		ArrayList<CountryModel> countries = new ArrayList<>();
		JsonArray jsonArray = getJsonArray("/countries");
		
		for(JsonElement jsonElement : jsonArray)
			countries.add(gson.fromJson(jsonElement, CountryModel.class));
			
		return countries;
	}
	
	public static final CoronaVirusAPI getInstance(Context ctx)
	{
		return (new CoronaVirusAPI(ctx));
	}

    private static JsonArray getJsonArray(String endpoint)
	{
        String url = BASE_URL + endpoint;
        return readJsonUrl(url).getAsJsonArray();
    }
	
	private static JsonObject getJsonObject(String endpoint)
	{
        String url = BASE_URL + endpoint;
        return readJsonUrl(url).getAsJsonObject();
    }

    private static JsonElement readJsonUrl(String url)
	{
        return new JsonParser().parse(getPage(url));
    }

    private static String getPage(String url)
	{
        String response = null;
		
		try {
			AsyncTask<String, String, String> request = new RequestHerokuApp().execute(url);
			response = ((request.get() != null) ? request.get().toString() : "[{}]");
		} catch(InterruptedException e) {
			e.printStackTrace();
		} catch(ExecutionException e) {
			e.printStackTrace();
		}
		
		return response;
    }
	
	public static class RequestHerokuApp extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			
			if(listener != null)
				listener.onRequestFinished();
		}
		
		@Override
		protected String doInBackground(String... params)
		{
			String response = "";
			
			try {
				URL url1 = new URL(params[0]);
				URLConnection connection = url1.openConnection();
				connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0");
				connection.connect();
				
				BufferedReader serverResponse = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				response = serverResponse.readLine();
				serverResponse.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
			
			return response;
		}

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			
			if(listener != null)
				listener.onRequestFinished();
		}
	}
	
	public static interface OnRequestListener
	{
		public void onRequestStarted();
		public void onRequestFinished();
	}
}
