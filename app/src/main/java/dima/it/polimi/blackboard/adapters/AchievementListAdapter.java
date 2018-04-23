package dima.it.polimi.blackboard.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.Query;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.model.Achievement;

/**
 * Adapter to display achievement items
 * Created by Stefano on 30/12/2017.
 */

public class AchievementListAdapter extends FirestoreAdapter<AchievementListAdapter.ViewHolder> {

    /*public AchievementListAdapter(List<Achievement> list){
        achievements = list;
    }
    */
    public AchievementListAdapter(Query query){
        super(query);
    }
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.achievement, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Achievement achievement = getSnapshot(position).toObject(Achievement.class);

        holder.title.setText(achievement.getTitle());
        holder.description.setText(achievement.getDescription());
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView description;

        private ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
        }
    }
}
