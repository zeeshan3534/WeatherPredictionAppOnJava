package com.ice.weather;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.Viewholder> {
    private Context context;
    private ArrayList<weatherRVModal> weatherRVModalArrayList;

    public WeatherRVAdapter(Context context, ArrayList<weatherRVModal> weatherRVModalArrayList) {
        this.context = context;
        this.weatherRVModalArrayList = weatherRVModalArrayList;
    }

    @NonNull
    @Override
    public WeatherRVAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVAdapter.Viewholder holder, int position) {

        weatherRVModal modal = weatherRVModalArrayList.get(position);
        holder.temperatureTV.setText(modal.getTemperature()+"°C");
        Picasso.get().load("http:".concat(modal.getIcon())).into(holder.conditionTV);
        holder.windTV.setText(modal.getWindSpeed()+"km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date t = input.parse(modal.getTime());
            holder.timeTV.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class Viewholder  extends RecyclerView.ViewHolder{
        private TextView windTV,temperatureTV,timeTV;
        private ImageView conditionTV;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            windTV= itemView.findViewById(R.id.idTVWindspeed);
            temperatureTV= itemView.findViewById(R.id.idTVTemperature);
            timeTV= itemView.findViewById(R.id.idTVTime);
            conditionTV= itemView.findViewById(R.id.idTVCondition);
        }
    }
}
