package com.lzokks04.myweather.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzokks04.myweather.R;
import com.lzokks04.myweather.bean.CityWeatherBean;
import com.lzokks04.myweather.util.API;
import com.lzokks04.myweather.util.Utils;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by Liu on 2016/8/28.
 */
public class DaliyWeatherAdapter extends RecyclerView.Adapter<DaliyWeatherAdapter.MyHolder>{

    private Context context;
    private CityWeatherBean bean;

    public DaliyWeatherAdapter(Context context, CityWeatherBean bean) {
        this.context = context;
        this.bean = bean;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyHolder holder = new MyHolder(LayoutInflater.from(context).inflate(R.layout.item_dailyweather,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.tvWeatherDate.setText(Utils.getMonthDay(bean.getHeWeatherdataservice().get(0).
                getDaily_forecast().get(position).getDate()));

        holder.tvLWeather.setText(bean.getHeWeatherdataservice().get(0).
                getDaily_forecast().get(position).getCond().getTxt_d());

        Picasso.with(context).load(API.WEATHER_ICON +
                bean.getHeWeatherdataservice().get(0).getDaily_forecast().get(position).getCond().getCode_d()
                + API.ICON_SUFFIX).into(holder.ivLWeather);

        holder.tvLTemp.setText(bean.getHeWeatherdataservice().get(0).
                getDaily_forecast().get(position).getTmp().getMax()+"°");

        holder.tvProb.setText(bean.getHeWeatherdataservice().get(0).
                getDaily_forecast().get(position).getPop()+"%");

        holder.tvNTemp.setText(bean.getHeWeatherdataservice().get(0).
                getDaily_forecast().get(position).getTmp().getMin()+"°");

        Picasso.with(context).load(API.WEATHER_ICON +
                bean.getHeWeatherdataservice().get(0).getDaily_forecast().get(position).getCond().getCode_n()
                + API.ICON_SUFFIX).into(holder.ivNWeather);

        holder.tvNWeather.setText(bean.getHeWeatherdataservice().get(0).
                getDaily_forecast().get(position).getCond().getTxt_n());
    }

    @Override
    public int getItemCount() {
        return bean.getHeWeatherdataservice().get(0).getDaily_forecast().size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        TextView tvWeatherDate;
        TextView tvLWeather;
        ImageView ivLWeather;
        TextView tvLTemp;
        TextView tvProb;
        TextView tvNTemp;
        ImageView ivNWeather;
        TextView tvNWeather;

        public MyHolder(View itemView) {
            super(itemView);
            tvWeatherDate = (TextView) itemView.findViewById(R.id.tv_weather_date);
            tvLWeather = (TextView) itemView.findViewById(R.id.tv_l_weather);
            ivLWeather = (ImageView) itemView.findViewById(R.id.iv_l_weather);
            tvLTemp = (TextView) itemView.findViewById(R.id.tv_l_temp);
            tvProb = (TextView) itemView.findViewById(R.id.tv_prob);
            tvNTemp = (TextView) itemView.findViewById(R.id.tv_n_temp);
            ivNWeather = (ImageView) itemView.findViewById(R.id.iv_n_weather);
            tvNWeather = (TextView) itemView.findViewById(R.id.tv_n_weather);
        }
    }
}
