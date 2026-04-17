package com.example.carrental.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carrental.R;
import com.example.carrental.models.Car;

import java.util.List;

public class ShowroomCarAdapter extends RecyclerView.Adapter<ShowroomCarAdapter.ViewHolder> {

    private final Context context;
    private final List<Car> carList;

    public ShowroomCarAdapter(Context context, List<Car> carList) {
        this.context = context;
        this.carList = carList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_edit_list_car, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Car car = carList.get(position);
        
        holder.tvCarName.setText(car.getBrand() + " " + car.getModel());
        holder.tvLocation.setText(car.getLocation());
        holder.tvPrice.setText("$" + car.getPriceperday() + "/Day");

        if (car.getImages() != null && !car.getImages().isEmpty() && !car.getImages().get(0).isEmpty()) {
            Glide.with(context)
                    .load(car.getImages().get(0))
                    .placeholder(R.drawable.testing)
                    .error(R.drawable.testing)
                    .into(holder.ivCar);
        } else {
            holder.ivCar.setImageResource(R.drawable.testing);
        }
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCar;
        TextView tvCarName;
        TextView tvLocation;
        TextView tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCar = itemView.findViewById(R.id.iv_car);
            tvCarName = itemView.findViewById(R.id.tv_car_name);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}
