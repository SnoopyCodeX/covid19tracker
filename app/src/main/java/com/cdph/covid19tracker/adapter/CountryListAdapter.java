package com.cdph.covid19tracker.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filter.*;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cdph.covid19tracker.R;
import com.cdph.covid19tracker.ResultActivity;
import com.cdph.covid19tracker.model.CountryListModel;
import com.cdph.covid19tracker.util.Utils;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

public class CountryListAdapter extends RecyclerView.Adapter<CountryListAdapter.CountryListViewHolder> implements Filterable
{
	private List<CountryListModel> listModel;
	private List<CountryListModel> listModelFull;
	
	public CountryListAdapter(List<CountryListModel> model)
	{
		listModel = model;
		listModelFull = new ArrayList<>(listModel);
	}

	@NonNull
	@Override
	public CountryListAdapter.CountryListViewHolder onCreateViewHolder(ViewGroup parent, int type)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.country_list_item, parent, false);
		return (new CountryListAdapter.CountryListViewHolder(view));
	}

	@Override
	public void onBindViewHolder(@NonNull final CountryListAdapter.CountryListViewHolder holder, int position)
	{
		final CountryListModel model = listModel.get(position);
		holder.flagIcon.setImageResource(getFlagByName(model.model.getCountry(), holder.context));
		holder.countryName.setText(model.model.getCountry());
		holder.countryCase.setText(Utils.formatNumber(model.model.getCases()));
		holder.countryDeaths.setText(Utils.formatNumber(model.model.getDeaths()));
		holder.countryRecovered.setText(Utils.formatNumber(model.model.getRecovered()));
		
		holder.parent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view)
			{
				Intent result = new Intent(holder.context, ResultActivity.class);
				result.putExtra("country_flag", getFlagByName(model.model.getCountry(), holder.context));
				result.putExtra("country_name", model.model.getCountry());
				result.putExtra("country_case", model.model.getCases());
				result.putExtra("country_tcase", model.model.getTodayCases());
				result.putExtra("country_death", model.model.getDeaths());
				result.putExtra("country_tdeath", model.model.getTodayDeaths());
				result.putExtra("country_recovered", model.model.getRecovered());
				
				/* REMOVED FOR A CERTAIN PURPOSE
				result.putExtra("country_active", model.model.getActive());
				result.putExtra("country_cpom", model.model.getCasesPerOneMillion());
				result.putExtra("country_dpom", model.model.getDeathsPerOneMillion());
				*/
				
				result.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				holder.context.startActivity(result);
			}
		});
	}
	
	private int getFlagByName(String name, Context ctx)
	{
		String _name_ = name.replaceAll("[.]", "").toLowerCase();
		_name_ = _name_.replaceAll("-", "_").toLowerCase();
		_name_ = _name_.replaceAll(" ", "_").toLowerCase();
			
		if(name.equals("Réunion"))
			return R.drawable.reunion;
			
		if(name.equals("Curaçao"))
			return R.drawable.curacao;
			
		Log.d(getClass().getSimpleName() + " - " + (new Date((new Date()).getTime())).toGMTString(), _name_);
		return (ctx.getResources().getIdentifier(_name_, "drawable", ctx.getPackageName()));
	}

	@Override
	public int getItemCount()
	{
		return listModel.size();
	}

	@Override
	public Filter getFilter()
	{
		return filter;
	}
	
	public class CountryListViewHolder extends RecyclerView.ViewHolder
	{
		Context context;
		ImageView flagIcon;
		TextView countryName, countryCase, countryDeaths, countryRecovered;
		CardView parent;
		
		public CountryListViewHolder(View itemView)
		{
			super(itemView);
			
			context = itemView.getContext();
			parent = (CardView) itemView;
			flagIcon = (ImageView) itemView.findViewById(R.id.country_flag);
			countryName = (TextView) itemView.findViewById(R.id.country_name);
			countryCase = (TextView) itemView.findViewById(R.id.country_case);
			countryDeaths = (TextView) itemView.findViewById(R.id.country_deaths);
			countryRecovered = (TextView) itemView.findViewById(R.id.country_recovered);
		}
	}
	
	private Filter filter = new Filter() 
	{
		@Override
		protected Filter.FilterResults performFiltering(CharSequence constraint)
		{
			List<CountryListModel> filteredList = new ArrayList<>();
			
			if(constraint != null && constraint.length() > 0)
			{
				String filterPattern = constraint.toString().toLowerCase().trim();
				for(CountryListModel model : listModelFull)
					if(model.model.getCountry().toLowerCase().contains(filterPattern))
						filteredList.add(model);
			}
			else
				filteredList.addAll(listModelFull);
				
			FilterResults results = new FilterResults();
			results.values = filteredList;
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, Filter.FilterResults result)
		{
			listModel.clear();
			listModel.addAll((List) result.values);
			notifyDataSetChanged();
		}
	};
}
