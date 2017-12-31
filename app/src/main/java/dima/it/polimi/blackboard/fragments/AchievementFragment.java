package dima.it.polimi.blackboard.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.adapters.AchievementListAdapter;
import dima.it.polimi.blackboard.utils.DataGeneratorUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AchievementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AchievementFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_achievement, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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
        AchievementListAdapter adapter = new AchievementListAdapter(DataGeneratorUtil.generateAchievements(5, title));
        ((TextView)achievementRow.findViewById(R.id.type)).setText(title);
        RecyclerView recycler = achievementRow.findViewById(R.id.recycler_view);
        recycler.setAdapter(adapter);
    }
}
