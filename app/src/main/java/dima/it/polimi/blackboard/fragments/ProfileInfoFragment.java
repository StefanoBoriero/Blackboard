package dima.it.polimi.blackboard.fragments;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import dima.it.polimi.blackboard.R;

import dima.it.polimi.blackboard.activities.PhotoDialogActivity;
import dima.it.polimi.blackboard.adapters.HouseListAdapter;
import dima.it.polimi.blackboard.model.House;
import dima.it.polimi.blackboard.utils.DataGeneratorUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileInfoFragment extends Fragment implements HouseListAdapter.HouseListAdapterListener{

    private ProfileInfoFragment.OnHouseListFragmentInteractionListener mListener;

    public ProfileInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileInfoFragment newInstance() {
        ProfileInfoFragment fragment = new ProfileInfoFragment();
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
        return inflater.inflate(R.layout.fragment_profile_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<House> myHouses = DataGeneratorUtil.generateHouses(5);
        RecyclerView rv = view.findViewById(R.id.recycler_view_house);
        RecyclerView.Adapter adapter = new HouseListAdapter(myHouses,this);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rv.setAdapter(adapter);

        view.findViewById(R.id.menu_dots).setOnClickListener(
                (this::onMenuExpand)
        );

        View button = getView().findViewById(R.id.user_icon);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PhotoDialogActivity.class);
            ActivityOptions options = ActivityOptions.
                    makeScaleUpAnimation(v,0,0,0, 0);
            startActivity(intent, options.toBundle());

        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileInfoFragment.OnHouseListFragmentInteractionListener) {
            mListener = (ProfileInfoFragment.OnHouseListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHouseListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    @Override
    public void onHouseClicked(House house, View view, int clickedPostion) {
        mListener.onItemClick(house, view, clickedPostion);
    }

    public void onMenuExpand(View v){
        PopupMenu popupMenu = new PopupMenu(getContext(),v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_my_houses, popupMenu.getMenu());
        popupMenu.show();
    }

    public interface OnHouseListFragmentInteractionListener {
        void onItemClick(House item, View view, int clickedPosition);
    }
}
