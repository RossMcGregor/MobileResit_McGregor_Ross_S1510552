package com.example.mobiletest;
//Mobile Resit Ross McGregor S1510552

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class WeatherFeedView
        extends RecyclerView.Adapter<WeatherFeedView.ViewHolder> {

    private List<WeatherDisplayModel> mWeatherDisplayModels;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View WeatherView;

        public ViewHolder(View v) {
            super(v);
            WeatherView = v;
        }
    }

    public WeatherFeedView(List<WeatherDisplayModel> weatherDisplayModels) {
        mWeatherDisplayModels = weatherDisplayModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weatherdisplayfeed, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final WeatherDisplayModel weatherDisplayModel = mWeatherDisplayModels.get(position);
        ((TextView)holder.WeatherView.findViewById(R.id.titleText)).setText(weatherDisplayModel.title);
        ((TextView)holder.WeatherView.findViewById(R.id.descriptionText))
                .setText(weatherDisplayModel.description);
        ((TextView)holder.WeatherView.findViewById(R.id.linkText)).setText(weatherDisplayModel.link);
    }

    @Override
    public int getItemCount() {
        return mWeatherDisplayModels.size();
    }
}
