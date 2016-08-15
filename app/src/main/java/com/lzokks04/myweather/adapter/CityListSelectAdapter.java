package com.lzokks04.myweather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lzokks04.myweather.R;
import com.lzokks04.myweather.bean.CityListBean;

import java.util.List;

/**
 * Created by Liu on 2016/8/13.
 */
public class CityListSelectAdapter extends BaseAdapter {

    private Context context;
    private List<CityListBean.CityInfoBean> list;

    public CityListSelectAdapter(Context context, List<CityListBean.CityInfoBean> bean) {
        this.context = context;
        this.list = bean;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_cityname, null);
            holder = new ViewHolder();
            holder.tvProv = (TextView) view.findViewById(R.id.tv_prov);
            holder.tvCity = (TextView) view.findViewById(R.id.tv_city);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        CityListBean.CityInfoBean bean = list.get(i);
        holder.tvProv.setText(bean.getProv());
        holder.tvCity.setText(bean.getCity());
        return view;
    }

    class ViewHolder {
        TextView tvProv;
        TextView tvCity;
    }
}
