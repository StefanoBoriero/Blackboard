package dima.it.polimi.blackboard.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.model.DayResume;

/**
 * Adapter class to show a DayResume inside a RecyclerView
 * Created by Stefano on 30/01/2018.
 */

public class DayResumeAdapter extends RecyclerView.Adapter<DayResumeAdapter.ViewHolder>{
    private List<DayResume> days;

    public DayResumeAdapter(List<DayResume> days){
        this.days = days;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_resume, parent, false);
        return new DayResumeAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DayResume day = days.get(position);

        String completed = DayResume.COMPLETED_MESSAGE + day.getTaskCompleted();
        holder.completed.setText(completed);

        String added = DayResume.ADDED_MESSAGE + day.getAddedTasks();
        holder.added.setText(added);

        String balance = DayResume.BALANCE_MESSAGE + day.getBalanceDiff() + "$";
        holder.balance.setText(balance);

        holder.day.setText(day.getDay());
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView completed;
        private TextView added;
        private TextView balance;
        private TextView day;

        private ViewHolder(View itemView) {
            super(itemView);

            this.completed = itemView.findViewById(R.id.completed_tasks);
            this.added = itemView.findViewById(R.id.added_tasks);
            this.balance = itemView.findViewById(R.id.balance_diff);
            this.day= itemView.findViewById(R.id.day);
        }
    }
}
