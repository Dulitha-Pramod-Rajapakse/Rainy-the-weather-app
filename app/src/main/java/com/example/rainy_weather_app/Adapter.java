package com.example.rainy_weather_app;

import android.content.Context;
import android.view.LayoutInflater;
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

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private Context context;
    private ArrayList<RVModel> RVModelArrayList;

    public Adapter(Context context, ArrayList<RVModel> RVModelArrayList) {
        this.context = context;
        this.RVModelArrayList = RVModelArrayList;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        RVModel model = RVModelArrayList.get(position);
        holder.temperature.setText(model.getTemperature() + "°c");
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.condition);
        holder.wind.setText(model.getWindSpeed() + "Km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try{
            Date t =input.parse(model.getTime());
            holder.time.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return RVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView wind,temperature,time;
        private ImageView condition;

        public ViewHolder(@NonNull View itemView, TextView wind, TextView temperature, TextView time, ImageView condition) {
            super(itemView);
            this.wind = wind;
            this.temperature = temperature;
            this.time = time;
            this.condition = condition;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            wind = itemView.findViewById(R.id.windSpeed_rv);
            temperature = itemView.findViewById(R.id.temperature_rv);
            time = itemView.findViewById(R.id.time_rv);
            condition = itemView.findViewById(R.id.condition_rv);
        }
    }
}
