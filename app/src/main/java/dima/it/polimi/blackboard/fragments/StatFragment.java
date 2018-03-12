package dima.it.polimi.blackboard.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.activities.LoginActivity;
import dima.it.polimi.blackboard.activities.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatFragment extends Fragment {

    private long housekeepingTasks;
    private long billingTasks;
    private long shoppingTasks;
    private Object totalTasks;
    private TextView housekeepingTV ;
    private TextView billingTV ;
    private TextView shoppingTV;
    private TextView totalTV;
    private TextView preferredTV;


    public StatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StatFragment.
     */
    public static StatFragment newInstance() {
        StatFragment fragment = new StatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        return inflater.inflate(R.layout.fragment_account_stat, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        housekeepingTV = getView().findViewById(R.id.number_of_completed_housekeeping_tasks);
        billingTV = getView().findViewById(R.id.number_of_completed_billing_tasks);
        shoppingTV = getView().findViewById(R.id.number_of_completed_shopping_tasks);
        totalTV = getView().findViewById(R.id.number_of_completed_tasks);
        preferredTV = getView().findViewById(R.id.preferred_number);

        //Fetch stats from server and load them in textViews
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null)
        {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").whereEqualTo("auth_id", mAuth.getCurrentUser().getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Map<String, Object> data = (Map)task.getResult().getDocuments().get(0).getData().get("stats");

                                housekeepingTasks = (long)data.get("housekeeping_task");
                                shoppingTasks = (long)data.get("shopping_task");
                                billingTasks = (long)data.get("billing_task");
                                totalTasks = data.get("total_task");

                                setTexts();
                            }
                            else {
                                //TODO add error message
                            }
                        }
                    });
        }
        else
        {
            //TODO add error message
        }
    }

    private void setTexts()
    {
        housekeepingTV.setText(String.valueOf(housekeepingTasks));
        billingTV.setText(String.valueOf(billingTasks));
        shoppingTV.setText(String.valueOf(shoppingTasks));
        totalTV.setText(String.valueOf(totalTasks));

        setmax();

    }

    private void setmax()
    {
        int preferred = Math.max((int)shoppingTasks,Math.max((int)housekeepingTasks,(int)billingTasks));

        if(preferred == housekeepingTasks)
            preferredTV.setText(R.string.houseKeeping);
        else if(preferred == shoppingTasks)
            preferredTV.setText(R.string.shopping);
        else if(preferred == billingTasks)
            preferredTV.setText(R.string.billing);
    }


}
