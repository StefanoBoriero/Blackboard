package dima.it.polimi.blackboard.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.adapters.AchievementListAdapter;
import dima.it.polimi.blackboard.utils.DataGeneratorUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AchievementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AchievementFragment extends Fragment {
    private FirebaseFirestore db;

    public AchievementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment AchievementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AchievementFragment newInstance() {
        AchievementFragment fragment = new AchievementFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_achievement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        View generalRow = view.findViewById(R.id.general_row);
        setupAchievementRow(generalRow,"General");

        View housekeepingRow = view.findViewById(R.id.houseKeeping_row);
        setupAchievementRow(housekeepingRow, "Housekeeping");

        View billingRow = view.findViewById(R.id.billing_row);
        setupAchievementRow(billingRow, "Billing");

        View shoppingRow = view.findViewById(R.id.shopping_row);
        setupAchievementRow(shoppingRow, "Shopping");

        super.onViewCreated(view, savedInstanceState);
    }

    private void setupAchievementRow(View achievementRow, String title){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String userId = user.getUid();
            CollectionReference achievements = db.collection("users").document(userId).collection("achievements");
            Query query = achievements.whereEqualTo("type", title);
            AchievementListAdapter adapter = new AchievementListAdapter(query);
            ((TextView)achievementRow.findViewById(R.id.type)).setText(title);
            RecyclerView recycler = achievementRow.findViewById(R.id.recycler_view);
            recycler.setAdapter(adapter);
            adapter.startListening();
        }
    }
}
