package com.cdph.covid19tracker;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.*;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutActivity extends BaseActivity implements View.OnClickListener
{
	private WindowManager manager;
	private LinearLayout parent;
	private ImageView dev, fbpage;
	private TextView version;
	
	@Override
	public void onInit(Bundle extras)
	{
		version = (TextView) findViewById(R.id.app_version);
		dev = (ImageView) findViewById(R.id.about_dev);
		fbpage = (ImageView) findViewById(R.id.about_fb);
		
		dev.setOnClickListener(this);
		fbpage.setOnClickListener(this);
		
		try {
			version.setText(String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName));
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View view)
	{
		int id = view.getId();
		
		if(manager != null)
		{
			manager.removeView(parent);
			manager = null;
		}
		
		switch(id)
		{
			case R.id.about_dev:
				showDialog();
			break;
			
			case R.id.about_fb:
				Intent page = new Intent(Intent.ACTION_VIEW, Uri.parse("https://fb.me/cdphdevelopers"));
				startActivity(page);
			break;
			
			case R.id.about_popup_fb:
				Intent fb = new Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/johnroy.calimlim"));
				startActivity(fb);
			break;
			
			case R.id.about_popup_git:
				Intent git = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/SnoopyCodeX"));
				startActivity(git);
			break;
			
			case R.id.about_popup_yt:
				Intent yt = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/channel/UCC65iAfGIHvMCi1vV-I8OSQ"));
				startActivity(yt);
			break;
		}
	}
	
	@Override
	public int getDefaultTheme()
	{
		return R.style.Theme_AppCompat_NoActionBar;
	}

	@Override
	public int getDefaultView()
	{
		return R.layout.about_main;
	}

	@Override
	public void onResumeActivity()
	{}

	@Override
	public void onBackPressed()
	{
		if(manager != null)
		{
			manager.removeView(parent);
			manager = null;
		}
		else
			super.onBackPressed();
	}
	
	private void showDialog()
	{
		parent = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.about_dev_dialog, null);
		ImageView git = (ImageView) parent.findViewById(R.id.about_popup_git);
		ImageView fb = (ImageView) parent.findViewById(R.id.about_popup_fb);
		ImageView yt = (ImageView) parent.findViewById(R.id.about_popup_yt);
		
		parent.setOnClickListener(this);
		git.setOnClickListener(this);
		fb.setOnClickListener(this);
		yt.setOnClickListener(this);
		
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.FILL_PARENT, 
					WindowManager.LayoutParams.FILL_PARENT,
					WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, 
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, 
					PixelFormat.TRANSLUCENT);
		
		params.gravity = Gravity.CENTER;
		manager = (WindowManager) getSystemService(WINDOW_SERVICE);
		manager.addView(parent, params);
	}
}
