package com.example.carrental.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carrental.R;
import com.example.carrental.models.Booking;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookings;
    private OnCancelClickListener cancelListener;
    private boolean isPartnerView = false;

    public interface OnCancelClickListener {
        void onCancelClick(Booking booking, int position);
    }

    public BookingAdapter(List<Booking> bookings, OnCancelClickListener listener) {
        this.bookings = bookings;
        this.cancelListener = listener;
    }

    public void setPartnerView(boolean partnerView) {
        isPartnerView = partnerView;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.bind(booking, position);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public void setBookings(List<Booking> newBookings) {
        this.bookings = newBookings;
        notifyDataSetChanged();
    }

    public void removeBooking(int position) {
        if (position >= 0 && position < bookings.size()) {
            bookings.remove(position);
            notifyItemRemoved(position);
        }
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCarImage;
        TextView tvCarName, tvAmount, tvStatus, tvPickup, tvReturn, tvDriver;
        MaterialButton btnCancel;

        BookingViewHolder(View itemView) {
            super(itemView);
            ivCarImage = itemView.findViewById(R.id.iv_booking_car_image);
            tvCarName = itemView.findViewById(R.id.tv_booking_car_name);
            tvAmount = itemView.findViewById(R.id.tv_booking_amount);
            tvStatus = itemView.findViewById(R.id.tv_booking_status);
            tvPickup = itemView.findViewById(R.id.tv_booking_pickup);
            tvReturn = itemView.findViewById(R.id.tv_booking_return);
            tvDriver = itemView.findViewById(R.id.tv_booking_driver);
            btnCancel = itemView.findViewById(R.id.btn_cancel_booking);
        }

        void bind(Booking booking, int position) {
            // Car info
            if (booking.getCar() != null) {
                tvCarName.setText(booking.getCar().getBrand() + " " + booking.getCar().getModel());
                if (booking.getCar().getImages() != null && !booking.getCar().getImages().isEmpty()) {
                    Glide.with(itemView.getContext())
                            .load(booking.getCar().getImages().get(0))
                            .placeholder(R.drawable.car_image)
                            .into(ivCarImage);
                }
            } else {
                tvCarName.setText("Car #" + booking.getCarId());
            }

            tvAmount.setText("PKR " + (int) booking.getTotalAmount());

            // Status badge with color
            String status = booking.getStatus() != null ? booking.getStatus() : "PENDING";
            tvStatus.setText(status);
            GradientDrawable statusBg = new GradientDrawable();
            statusBg.setCornerRadius(20f);
            switch (status) {
                case "APPROVED":
                    statusBg.setColor(Color.parseColor("#16A34A"));
                    break;
                case "REJECTED":
                    statusBg.setColor(Color.parseColor("#DC2626"));
                    break;
                case "COMPLETED":
                    statusBg.setColor(Color.parseColor("#2563EB"));
                    break;
                case "CANCELLED":
                    statusBg.setColor(Color.parseColor("#6B7280"));
                    break;
                default: // PENDING
                    statusBg.setColor(Color.parseColor("#F59E0B"));
                    break;
            }
            tvStatus.setBackground(statusBg);

            // Format dates
            tvPickup.setText(formatDate(booking.getPickupDate()));
            tvReturn.setText(formatDate(booking.getReturnDate()));
            tvDriver.setText(booking.isWithDriver() ? "Yes" : "No");

            // Action button
            if (isPartnerView) {
                btnCancel.setText("Manage Booking");
                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setOnClickListener(v -> {
                    if (cancelListener != null) {
                        cancelListener.onCancelClick(booking, position);
                    }
                });
            } else if ("PENDING".equals(status)) {
                btnCancel.setText("Cancel Booking");
                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setOnClickListener(v -> {
                    if (cancelListener != null) {
                        cancelListener.onCancelClick(booking, position);
                    }
                });
            } else {
                btnCancel.setVisibility(View.GONE);
            }
            
            // Make whole card clickable for management too
            if (isPartnerView) {
                itemView.setOnClickListener(v -> {
                    if (cancelListener != null) {
                        cancelListener.onCancelClick(booking, position);
                    }
                });
            }
        }

        private String formatDate(String dateStr) {
            if (dateStr == null) return "N/A";
            try {
                // Try ISO format first
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = isoFormat.parse(dateStr.substring(0, Math.min(10, dateStr.length())));
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                return displayFormat.format(date);
            } catch (ParseException e) {
                return dateStr;
            }
        }
    }
}
