package dima.it.polimi.blackboard.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.model.Detail;
import dima.it.polimi.blackboard.utils.UserDecoder;

/**
 * This adapter handles item details in order to show them in a Recycler view
 * Created by Stefano on 15/01/2018.
 */

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {
    private List<Detail> details;

    public DetailAdapter(List<Detail> detailList){
        details = detailList;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Detail detail = details.get(position);

        String title = detail.getTitle();
        if(title.equals("suggestedTo")){
            String userSuggested = UserDecoder.getInstance().getNameFromId(detail.getContent());
            holder.title.setText(detail.getTitle());
            holder.tView.setText(userSuggested);
        }
        else{
            holder.title.setText(detail.getTitle());
            holder.tView.setText(detail.getContent());
        }

        //Todo get image from Firebase Storage
        holder.icon.setImageResource(detail.getIconResId());
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tView;
        private TextView title;
        private ImageView icon;

        private ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            tView = itemView.findViewById(R.id.detail_content);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}
