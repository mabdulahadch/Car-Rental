package com.example.carrental.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.carrental.R;
import com.example.carrental.activities.CarDetailActivity;
import com.example.carrental.databinding.ItemCarBinding;
import com.example.carrental.models.Car;
import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private List<Car> carList;

    public CarAdapter(List<Car> carList) {
        this.carList = carList;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCarBinding binding = ItemCarBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CarViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = carList.get(position);
        holder.binding.tvCarName.setText(car.getBrand() + " " + car.getModel());
        holder.binding.tvPrice.setText("PKR" + (int)car.getPriceperday() + "/Day");
        holder.binding.tvRating.setText(String.valueOf(car.getRating()));
        holder.binding.tvLocation.setText(car.getLocation());

        if (car.getImages() != null && !car.getImages().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(car.getImages().get(0))
                    .placeholder(R.drawable.car_image)
                    .into(holder.binding.ivCar);
        } else {
            holder.binding.ivCar.setImageResource(R.drawable.car_image);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CarDetailActivity.class);
            intent.putExtra("CAR_ID", car.getId());
            v.getContext().startActivity(intent);
        });
        
        holder.binding.btnBook.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CarDetailActivity.class);
            intent.putExtra("CAR_ID", car.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    public void setCars(List<Car> cars) {
        this.carList = cars;
        notifyDataSetChanged();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        ItemCarBinding binding;

        public CarViewHolder(ItemCarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
