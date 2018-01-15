package dima.it.polimi.blackboard.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.model.House;

/**
 * Adapter for display houses in a Recycler View
 * Created by Stefano on 14/01/2018.
 */

public class HouseListAdapter extends RecyclerView.Adapter<HouseListAdapter.ViewHolder> {
    private List<House> myHouses;

    public HouseListAdapter(List<House> houses){
        myHouses = houses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.house, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        House house = myHouses.get(position);
        holder.houseName.setText(house.getName());
    }

    @Override
    public int getItemCount() {
        return myHouses.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{
        ImageView houseImage;
        TextView houseName;

        private ViewHolder(View itemView) {
            super(itemView);
            houseName = itemView.findViewById(R.id.house_name);
            houseImage = itemView.findViewById(R.id.house_icon);
        }
    }
}