package dima.it.polimi.blackboard.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.model.Detail;

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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Detail detail = details.get(position);


        holder.tView.setText(detail.getContent());
        holder.tView.setCompoundDrawablesWithIntrinsicBounds(detail.getIconResId(), 0, 0, 0);
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tView;

        private ViewHolder(View itemView) {
            super(itemView);
            tView = itemView.findViewById(R.id.detail_content);
        }
    }
}
