package dima.it.polimi.blackboard.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.adapters.AchievementListAdapter;
import dima.it.polimi.blackboard.model.Achievement;
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
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_achievement, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        AchievementListAdapter adapter = new AchievementListAdapter(DataGeneratorUtil.generateAchievements(10));
        recyclerView.setAdapter(adapter);


        super.onViewCreated(view, savedInstanceState);
    }
}
