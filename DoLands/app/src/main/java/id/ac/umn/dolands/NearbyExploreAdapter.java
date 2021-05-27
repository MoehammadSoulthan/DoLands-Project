package id.ac.umn.dolands;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;

public class NearbyExploreAdapter extends RecyclerView.Adapter<NearbyExploreAdapter.NearbyExploreViewHolder>{
    LinkedList<NearbyDetailModel> details;
    ClickedItem clickedItem;

    public void setData(LinkedList<NearbyDetailModel> nearbyDetailModels) {
        this.details = nearbyDetailModels;
        notifyDataSetChanged();
    }

    public NearbyExploreAdapter(ClickedItem clickedItem) {
        this.clickedItem = clickedItem;
    }

    @NonNull
    @Override
    public NearbyExploreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nearby_explore_item, parent, false);
        return new NearbyExploreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyExploreViewHolder holder, int position) {
//        holder.attractImg.setImageDrawable(details.get(position).getPreview().getSource());
        NearbyDetailModel nearbyDetailModel = details.get(position);

        try {
            Picasso.get()
                    .load(details.get(position).getPreview().getSource())
                    .placeholder(R.drawable.disney1)
                    .into(holder.attractImg);
        }
        catch (NullPointerException e) {
            Picasso.get()
                    .load(R.drawable.disney1)
                    .into(holder.attractImg);
        }

        holder.attractName.setText(details.get(position).getName());
        holder.attractImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedItem.ClickedImage(nearbyDetailModel);
            }
        });
    }

    public interface ClickedItem {
        public void ClickedImage(NearbyDetailModel nearbyDetailModel);
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    public static class NearbyExploreViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView attractImg;
        TextView attractName;

        public NearbyExploreViewHolder(@NonNull View itemView) {
            super(itemView);

            attractImg = itemView.findViewById(R.id.attractImg);
            attractName = itemView.findViewById(R.id.attractName);
        }
    }
}
