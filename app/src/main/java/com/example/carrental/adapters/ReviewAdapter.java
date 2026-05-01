package com.example.carrental.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carrental.R;
import com.example.carrental.models.Review;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviews;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void setReviews(List<Review> newReviews) {
        this.reviews = newReviews;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView ivReviewer;
        TextView tvName, tvDate, tvRating, tvComment;

        ReviewViewHolder(View itemView) {
            super(itemView);
            ivReviewer = itemView.findViewById(R.id.iv_reviewer);
            tvName = itemView.findViewById(R.id.tv_reviewer_name);
            tvDate = itemView.findViewById(R.id.tv_review_date);
            tvRating = itemView.findViewById(R.id.tv_review_rating);
            tvComment = itemView.findViewById(R.id.tv_review_comment);
        }

        void bind(Review review) {
            tvName.setText(review.getUserName() != null ? review.getUserName() : "User");
            tvRating.setText(String.valueOf(review.getRating()));
            tvComment.setText(review.getComment() != null ? review.getComment() : "");
            tvDate.setText(formatDate(review.getCreatedAt()));

            if (review.getUserImage() != null && !review.getUserImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(review.getUserImage())
                        .placeholder(R.drawable.user)
                        .circleCrop()
                        .into(ivReviewer);
            }
        }

        private String formatDate(String dateStr) {
            if (dateStr == null) return "";
            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = isoFormat.parse(dateStr.substring(0, Math.min(10, dateStr.length())));
                SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                return displayFormat.format(date);
            } catch (ParseException e) {
                return dateStr;
            }
        }
    }
}
