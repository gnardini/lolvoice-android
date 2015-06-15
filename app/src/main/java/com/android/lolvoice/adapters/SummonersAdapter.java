package com.android.lolvoice.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.lolvoice.models.SummonerInfo;

import java.util.List;

public class SummonersAdapter extends BaseAdapter {

    private Context mContext;
    private List<SummonerInfo> mSummoners;

    public SummonersAdapter(Context context, List<SummonerInfo> summoners) {
        mContext = context;
        mSummoners = summoners;
    }

    @Override
    public int getCount() {
        return mSummoners.size();
    }

    @Override
    public Object getItem(int position) {
        return mSummoners.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mSummoners.get(position).getSummonerId();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder v;
        if (view == null || view.getTag() == null) {
            view = inflater.inflate(com.android.lolvoice.R.layout.adapter_summoners, parent, false);
            if (view == null) return null;
            v = newViewHolder(view);
            view.setTag(v);
        } else {
            v = (ViewHolder) view.getTag();
        }
        SummonerInfo summoner = mSummoners.get(position);
        populate(summoner, v);
        setListeners(summoner, v);
        return view;
    }

    private void populate(SummonerInfo summoner, ViewHolder v) {
        v.mSummonerName.setText(summoner.getName());
    }

    private void setListeners(SummonerInfo summoner, ViewHolder v) {
    }

    private class ViewHolder {
        TextView mSummonerName;
    }

    private ViewHolder newViewHolder(View view) {
        ViewHolder v = new ViewHolder();
        v.mSummonerName = (TextView) view.findViewById(com.android.lolvoice.R.id.adapter_summoner_name);
        return v;
    }
}
