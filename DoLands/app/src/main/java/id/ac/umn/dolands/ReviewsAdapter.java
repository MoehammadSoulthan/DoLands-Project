package id.ac.umn.dolands;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {
    List<ReviewsModel> reviewsModelList;

    public ReviewsAdapter(List<ReviewsModel> reviewsModelList) {
        this.reviewsModelList = reviewsModelList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_review_item, parent, false);
        return new ReviewsAdapter.ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsViewHolder holder, int position) {
        ReviewsModel reviewsModel = reviewsModelList.get(position);

        if(!reviewsModelList.get(position).getImageUrl().equals("")) {
//            Glide.with(this)
//                    .load(reviewsModelList.get(position).getImageUrl())
//                    .into(holder.imageUserReview);
            Picasso.get()
                    .load(reviewsModelList.get(position).getImageUrl())
                    .placeholder(R.drawable.profilephoto)
                    .into(holder.imageUserReview);
        }

        holder.usernameReview.setText(reviewsModelList.get(position).getUsername());
        holder.text_review.setText(reviewsModelList.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return reviewsModelList.size();
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageUserReview;
        TextView usernameReview, text_review;

        public ReviewsViewHolder(@NonNull View itemView) {
            super(itemView);

            imageUserReview = itemView.findViewById(R.id.imageUserReview);
            usernameReview = itemView.findViewById(R.id.usernameReview);
            text_review = itemView.findViewById(R.id.text_review);
        }
    }
}
