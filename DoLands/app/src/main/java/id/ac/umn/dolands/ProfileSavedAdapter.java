package id.ac.umn.dolands;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfileSavedAdapter extends RecyclerView.Adapter<ProfileSavedAdapter.ProfileSavedViewHolder> {
    List<SavedLocationModel> savedLocationModelList;
    ClickedItem clickedItem;

    public void setData(List<SavedLocationModel> savedLocationModelList) {
        this.savedLocationModelList = savedLocationModelList;
        notifyDataSetChanged();
    }

    public ProfileSavedAdapter(ProfileSavedAdapter.ClickedItem clickedItem) {
        this.clickedItem = clickedItem;
    }

    @NonNull
    @Override
    public ProfileSavedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nearby_explore_item, parent, false);
        return new ProfileSavedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileSavedViewHolder holder, int position) {
        //        holder.attractImg.setImageDrawable(details.get(position).getPreview().getSource());
        SavedLocationModel savedLocationModel = savedLocationModelList.get(position);

        if(!savedLocationModelList.get(position).getImage().isEmpty()) {
            Picasso.get()
                    .load(savedLocationModelList.get(position).getImage())
                    .placeholder(R.drawable.disney1)
                    .into(holder.attractImg);
        }
        else {
            Picasso.get()
                    .load(R.drawable.disney1)
                    .into(holder.attractImg);
        }

        holder.attractName.setText(savedLocationModelList.get(position).getName());
        holder.attractImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedItem.ClickedImage(savedLocationModel);
            }
        });
    }

    public interface ClickedItem {
        public void ClickedImage(SavedLocationModel savedLocationModel);
    }

    @Override
    public int getItemCount() { return savedLocationModelList.size(); }

    public static class ProfileSavedViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView attractImg;
        TextView attractName;

        public ProfileSavedViewHolder(@NonNull View itemView) {
            super(itemView);

            attractImg = itemView.findViewById(R.id.attractImg);
            attractName = itemView.findViewById(R.id.attractName);
        }
    }
}
