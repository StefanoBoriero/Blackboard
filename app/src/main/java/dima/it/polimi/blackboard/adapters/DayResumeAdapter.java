package dima.it.polimi.blackboard.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_resume, parent, false);
        return new DayResumeAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DayResume day = days.get(position);

        String completed = DayResume.COMPLETED_MESSAGE + day.getCompletedItems();
        holder.completed.setText(completed);

        String added = DayResume.ADDED_MESSAGE + day.getCreatedItems();
        holder.added.setText(added);

        String balance = DayResume.BALANCE_MESSAGE + day.getBalanceDiff() + "$";
        holder.balance.setText(balance);

        DateFormat df = new SimpleDateFormat("EEE, MMM dd", Locale.US);
        holder.day.setText(df.format(day.getDay()));
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
