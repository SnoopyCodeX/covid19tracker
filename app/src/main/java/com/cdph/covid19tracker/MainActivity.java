package com.cdph.covid19tracker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.Snackbar;
import com.cdph.covid19tracker.api.CoronaVirusAPI;
import com.cdph.covid19tracker.api.model.CountryModel;
import com.cdph.covid19tracker.api.model.GlobalModel;
import com.cdph.covid19tracker.adapter.CountryListAdapter;
import com.cdph.covid19tracker.model.CountryListModel;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, InternetConnectivityListener, CoronaVirusAPI.OnRequestListener
{
	private ProgressDialog loaderProgress;
	private SharedPreferences prefs;
	private SwipeRefreshLayout swipe;
	private LinearLayoutManager layoutManager;
	private LinearLayout mainCoord, loader;
	private RecyclerView countryList;
	private EditText searchView;
	private TextView noNet;
	private InternetAvailabilityChecker net;
	private CountryListAdapter adapter;
	private Toolbar toolbar;
	
	private boolean backPressedOnce = false;
	
    @Override
    public void onInit(Bundle savedInstanceState)
    {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		swipe = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
		loader = (LinearLayout) findViewById(R.id.loader);
		mainCoord = (LinearLayout) findViewById(R.id.coordinatorLayoutMain);
		countryList = (RecyclerView) findViewById(R.id.country_list);
		searchView = (EditText) findViewById(R.id.searchview);
		noNet = (TextView) findViewById(R.id.no_internet);
		layoutManager = new LinearLayoutManager(this);
		
		swipe.setOnRefreshListener(this);
		mainCoord.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
		mainCoord.setFocusableInTouchMode(true);
		
		noNet.setText("No internet connection");
		countryList.setLayoutManager(layoutManager);
		
		countryList.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView view, int dx, int dy)
			{
				super.onScrolled(view, dx, dy);
				swipe.setEnabled((layoutManager.findFirstCompletelyVisibleItemPosition() == 0));
			}
		});
		
		searchView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence str, int p2, int p3, int p4)
			{}

			@Override
			public void onTextChanged(CharSequence str, int p2, int p3, int p4)
			{
				if(adapter != null)
					adapter.getFilter().filter(str);
			}

			@Override
			public void afterTextChanged(Editable edit)
			{}
		});
    }

	@Override
	public void onResumeActivity()
	{
		InternetAvailabilityChecker.init(this);
		net = InternetAvailabilityChecker.getInstance();
		net.addInternetConnectivityListener(this);
		
		boolean isOverlayPermitted = prefs.getBoolean("isOverlayPermitted", false);
		if(!isOverlayPermitted)
		{
			AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setMessage("Overlay permission is not yet permitted, please allow this app to have the overlay permission by going to the settings.");
			dialog.setTitle("Permission Request");
			dialog.setButton(AlertDialog.BUTTON1, "Allow", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dlg, int btn)
				{
					dlg.dismiss();
					requestOverlayPermission();
				}
			});
			dialog.setButton(AlertDialog.BUTTON2, "Deny", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dlg, int btn)
				{
					dlg.dismiss();
					finish();
				}
			});
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}
	}

	@Override
	public void onRefresh()
	{
		boolean isConnected = net.getCurrentInternetAvailabilityStatus();
		if(!isConnected)
		{
			loader.setVisibility(View.GONE);
			noNet.setVisibility((isConnected) ? View.GONE : View.VISIBLE);
			countryList.setVisibility((isConnected) ? View.VISIBLE : View.GONE);
			return;
		}
		
		loadDataFromServer();
	}

	@Override
	public void onBackPressed()
	{
		if(!searchView.getText().toString().isEmpty())
		{
			searchView.setText("");
			searchView.clearFocus();
			mainCoord.setFocusableInTouchMode(true);
			mainCoord.requestFocus();
		}
		else
			isDoubleBackPressed();
	}
	
	@Override
	public int getDefaultTheme()
	{
		return R.style.Theme_AppCompat_NoActionBar;
	}

	@Override
	public int getDefaultView()
	{
		return R.layout.main;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean ret = false;
		int id = item.getItemId();
		
		switch(id)
		{
			case R.id.cdph:
				Intent cdph = new Intent(Intent.ACTION_VIEW);
				cdph.setData(Uri.parse("https://fb.me/cdphdevelopers"));
				startActivity(cdph);
				ret = true;
			break;
			
			case R.id.about:
				startActivity(new Intent(this, AboutActivity.class));
				ret = true;
			break;
		}
		
		return ret;
	}

	/* THIS WAS REMOVED FOR A CERTAIN PURPOSE
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		if(requestCode == PERMISSION_REQUEST_CODE)
		{
			for(int i = 0; i < grantResults.length; i++)
			{
				String perm = permissions[i];
				int grantRes = grantResults[i];
				
				switch(perm)
				{
					case Manifest.permission.INTERNET:
					case Manifest.permission.SYSTEM_ALERT_WINDOW:
						if(grantRes == PackageManager.PERMISSION_DENIED)
							
					break;
				}
			}
		}
	}
	

	private void checkPermission()
	{
		ArrayList<String> perms = new ArrayList<>();
			
		if(ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
			perms.add(Manifest.permission.INTERNET);
			
		if(ActivityCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED)
			perms.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
				
		if(perms.size() > 0)
		{
			String[] strPerms = new String[perms.size()];
			strPerms = perms.toArray(strPerms);
			ActivityCompat.requestPermissions(this, strPerms, PERMISSION_REQUEST_CODE);
		}
	}
	*/
	
	private void requestOverlayPermission()
	{
		Intent perm = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
		perm.setData(Uri.parse("package:" + getPackageName()));
		startActivityForResult(perm, 1248);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == 1248)
			if(Settings.canDrawOverlays(this))
			{
				prefs.edit().putBoolean("isOverlayPermitted", true).commit();
				loadDataFromServer();
			}
			else
				onResumeActivity();
	}

	@Override
	public void onInternetConnectivityChanged(boolean isConnected)
	{
		loader.setVisibility(View.GONE);
		noNet.setVisibility((isConnected) ? View.GONE : View.VISIBLE);
		countryList.setVisibility((isConnected) ? View.VISIBLE : View.GONE);
		swipe.setEnabled(!isConnected);
		
		if(!isConnected)
			Snackbar.make(mainCoord, "Disconnected!", Snackbar.LENGTH_SHORT).show();
			
		boolean isOverlayPermitted = prefs.getBoolean("isOverlayPermitted", false);
		if(adapter == null && isConnected && isOverlayPermitted)
			loadDataFromServer();
	}

	@Override
	public void onRequestStarted()
	{
		loader.setVisibility(View.VISIBLE);
		noNet.setVisibility(View.GONE);
		countryList.setVisibility(View.GONE);
		
		if(!swipe.isRefreshing())
		{
			loaderProgress = new ProgressDialog(this);
			loaderProgress.setMessage("Fetching data, please wait...");
			loaderProgress.setIndeterminate(true);
			loaderProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			loaderProgress.setCancelable(false);
			loaderProgress.setCanceledOnTouchOutside(false);
			loaderProgress.show();
		}
	}

	@Override
	public void onRequestFinished()
	{
		swipe.setRefreshing(false);
		loader.setVisibility(View.GONE);
		noNet.setVisibility(View.GONE);
		countryList.setVisibility(View.VISIBLE);
		countryList.setAdapter(adapter);
		
		if(loaderProgress != null)
			loaderProgress.dismiss();
		loaderProgress = null;
	}
	
	private void loadDataFromServer()
	{
		if(adapter != null && !swipe.isRefreshing())
			return;
		
		runOnUiThread(new Runnable() {
			@Override
			public void run()
			{
				CoronaVirusAPI api = CoronaVirusAPI.getInstance(MainActivity.this).setOnRequestListener(MainActivity.this);
				ArrayList<CountryModel> countries = api.getAllDataFromCountries();
				ArrayList<CountryListModel> models = new ArrayList<>();

				for(CountryModel country : countries)
					models.add(new CountryListModel(country));

				adapter = new CountryListAdapter(models);
			}
		});
	}
	
	private void isDoubleBackPressed()
	{
		Thread timer = new Thread(new Runnable() {
			@Override
			public void run()
			{
				try {
					Thread.sleep(3500);
					backPressedOnce = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		if(!backPressedOnce)
		{
			timer.start();
			backPressedOnce = true;
			Snackbar.make(mainCoord, "Press back once more to exit", Snackbar.LENGTH_SHORT).show();
			return;
		}
		
		if(backPressedOnce)
		{
			timer = null;
			backPressedOnce = false;
			finish();
		}
	}
}
