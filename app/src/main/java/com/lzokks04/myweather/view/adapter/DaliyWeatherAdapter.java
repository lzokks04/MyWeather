package com.lzokks04.myweather.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzokks04.myweather.R;
import com.lzokks04.myweather.model.bean.CityWeatherBean;
import com.lzokks04.myweather.model.bean.DailyWeather;
import com.lzokks04.myweather.util.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sephiroth.android.library.picasso.Picasso;

/**
 * RecyclerView的Adapter
 * Created by Liu on 2016/8/28.
 */
public class DaliyWeatherAdapter extends RecyclerView.Adapter<DaliyWeatherAdapter.MyHolder> {

    private Context context;
    private CityWeatherBean cityWeatherBean;
    private List<DailyWeather> dailyWeather;

    public DaliyWeatherAdapter(Context context, CityWeatherBean bean, List<DailyWeather> dailyWeather) {
        this.context = context;
        this.cityWeatherBean = bean;
        this.dailyWeather = dailyWeather;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyHolder holder = new MyHolder(LayoutInflater.from(context).inflate(R.layout.item_dailyweather, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        setText(holder, position);

    }

    /**
     * 设置文字
     *
     * @param holder
     * @param position
     */
    private void setText(MyHolder holder, int position) {
        if (cityWeatherBean != null && dailyWeather == null) {
            holder.tvWeatherDate.setText(Utils.getMonthDay(cityWeatherBean.getHeWeatherdataservice().get(0).
                    getDaily_forecast().get(position).getDate()));

            holder.tvLWeather.setText(cityWeatherBean.getHeWeatherdataservice().get(0).
                    getDaily_forecast().get(position).getCond().getTxt_d());

            Picasso.with(context).load(Utils.getWeatherIcon(cityWeatherBean.getHeWeatherdataservice()
                    .get(0).getDaily_forecast().get(position).getCond().getCode_d())).into(holder.ivLWeather);

            holder.tvLTemp.setText(cityWeatherBean.getHeWeatherdataservice().get(0).
                    getDaily_forecast().get(position).getTmp().getMax() + "°");

            holder.tvProb.setText(cityWeatherBean.getHeWeatherdataservice().get(0).
                    getDaily_forecast().get(position).getPop() + "%");

            holder.tvNTemp.setText(cityWeatherBean.getHeWeatherdataservice().get(0).
                    getDaily_forecast().get(position).getTmp().getMin() + "°");

            Picasso.with(context).load(Utils.getWeatherIcon(cityWeatherBean.getHeWeatherdataservice()
                    .get(0).getDaily_forecast().get(position).getCond().getCode_n())).into(holder.ivNWeather);

            holder.tvNWeather.setText(cityWeatherBean.getHeWeatherdataservice().get(0).
                    getDaily_forecast().get(position).getCond().getTxt_n());
        } else if (cityWeatherBean == null && dailyWeather != null) {
            holder.tvWeatherDate.setText(dailyWeather.get(position).getDate());
            holder.tvLWeather.setText(dailyWeather.get(position).getlWeather());
            Picasso.with(context).load(Utils.getWeatherIcon(
                    dailyWeather.get(position).getlCode())).into(holder.ivLWeather);
            holder.tvLTemp.setText(dailyWeather.get(position).getlTemp());
            holder.tvProb.setText(dailyWeather.get(position).getProb());
            holder.tvNTemp.setText(dailyWeather.get(position).getnTemp());
            holder.tvNWeather.setText(dailyWeather.get(position).getnWeather());
            Picasso.with(context).load
                    (Utils.getWeatherIcon(dailyWeather.get(position).getnCode())).into(holder.ivNWeather);
        }
    }

    @Override
    public int getItemCount() {
        if (cityWeatherBean != null && dailyWeather == null) {
            return cityWeatherBean.getHeWeatherdataservice().get(0).getDaily_forecast().size();
        } else if (cityWeatherBean == null && dailyWeather != null) {
            return dailyWeather.size();
        }
        return 0;
    }

    /**
     * viewholder
     */
    class MyHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_weather_date)
        TextView tvWeatherDate;
        @BindView(R.id.tv_l_weather)
        TextView tvLWeather;
        @BindView(R.id.iv_l_weather)
        ImageView ivLWeather;
        @BindView(R.id.tv_l_temp)
        TextView tvLTemp;
        @BindView(R.id.tv_n_temp)
        TextView tvNTemp;
        @BindView(R.id.iv_n_weather)
        ImageView ivNWeather;
        @BindView(R.id.tv_n_weather)
        TextView tvNWeather;
        @BindView(R.id.tv_prob)
        TextView tvProb;

        public MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
