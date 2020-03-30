package com.cdph.covid19tracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public abstract class BaseActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setTheme(getDefaultTheme());
		super.onCreate(savedInstanceState);
		setContentView(getDefaultView());
		
		onInit(savedInstanceState);
	}

	@Override
	protected void onResume()
	{
		onResumeActivity();
		super.onResume();
	}
	
	public abstract int getDefaultTheme();
	public abstract int getDefaultView();
	public abstract void onInit(Bundle extras);
	public abstract void onResumeActivity();
}
