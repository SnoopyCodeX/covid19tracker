package com.cdph.covid19tracker;

import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;
import java.math.BigInteger;
import java.text.DecimalFormat;

import com.cdph.covid19tracker.util.Utils;

public class ResultActivity extends BaseActivity
{
	private TextView name, cases, deaths, recovered, newCases, newDeaths, summary;
	private ImageView flag;
	
	@Override
	public void onInit(Bundle extra)
	{
		Bundle extras = getIntent().getExtras();
		
		name = (TextView) findViewById(R.id.country_name);
		cases = (TextView) findViewById(R.id.result_cases);
		deaths = (TextView) findViewById(R.id.result_deaths);
		recovered = (TextView) findViewById(R.id.result_recovered);
		newCases = (TextView) findViewById(R.id.result_new_cases);
		newDeaths = (TextView) findViewById(R.id.result_new_deaths);
		summary = (TextView) findViewById(R.id.result_summary);
		flag = (ImageView) findViewById(R.id.country_flag);
		
		name.setText(extras.getString("country_name"));
		cases.setText(Utils.formatNumber(extras.getInt("country_case")));
		deaths.setText(Utils.formatNumber(extras.getInt("country_death")));
		recovered.setText(Utils.formatNumber(extras.getInt("country_recovered")));
		newCases.setText(Utils.formatNumber(extras.getInt("country_tcase")));
		newDeaths.setText(Utils.formatNumber(extras.getInt("country_tdeath")));
		flag.setImageResource(extras.getInt("country_flag"));
		
		String strSummary = "😷Confirmed cases has %s<br>💀Death toll has %s";
		int nTCase = extras.getInt("country_tcase");
		int nTDeath = extras.getInt("country_tdeath");
		
		int nCase = extras.getInt("country_case");
		int nDeath = extras.getInt("country_death");
		
		summary.setText(Html.fromHtml(String.format(strSummary, getPercentage(nTCase, (nCase - nTCase)), getPercentage(nTDeath, (nDeath - nTDeath)))));
	}
	
	@Override
	public int getDefaultTheme()
	{
		return R.style.Theme_AppCompat_NoActionBar;
	}

	@Override
	public int getDefaultView()
	{
		return R.layout.result_main;
	}

	@Override
	public void onResumeActivity()
	{}
	
	private String getPercentage(int increase, int oldNum)
	{
		DecimalFormat format = new DecimalFormat("#.##");
		float percent = ((float) increase) / ((float) oldNum);
		
		String str = (percent > 0 ? "<font color='red'>increased by " : "<font color='green'>decreased by ");
		str = (percent == 0 ? "<font color='green'><strong>not changed</strong></font> as of today" : str + "<strong>" + format.format(percent * 100f) + "%</strong></font> as of today");
		return str;
	}
}
