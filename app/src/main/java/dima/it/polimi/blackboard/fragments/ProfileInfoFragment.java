package dima.it.polimi.blackboard.fragments;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import dima.it.polimi.blackboard.R;

import dima.it.polimi.blackboard.activities.PhotoDialogActivity;
import dima.it.polimi.blackboard.adapters.HouseListAdapter;
import dima.it.polimi.blackboard.model.House;
import dima.it.polimi.blackboard.model.User;
import dima.it.polimi.blackboard.utils.DataGeneratorUtil;
import dima.it.polimi.blackboard.utils.GlideApp;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileInfoFragment extends Fragment implements HouseListAdapter.HouseListAdapterListener{

    private ProfileInfoFragment.OnHouseListFragmentInteractionListener mListener;
    private ImageView ivProfile;
    private boolean changedImage;

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
        /*
        if (getArguments() != null) {

        }
        */
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        changedImage = false;
        List<House> myHouses = DataGeneratorUtil.generateHouses(5);
        RecyclerView rv = view.findViewById(R.id.recycler_view_house);
        RecyclerView.Adapter adapter = new HouseListAdapter(myHouses,this);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rv.setAdapter(adapter);

        TextView nameView = view.findViewById(R.id.username);
        TextView mailView = view.findViewById(R.id.email);
        ivProfile = view.findViewById(R.id.user_icon);


        String name = (String)User.getInstance().getPersonal_info().get("name");
        String surname = (String)User.getInstance().getPersonal_info().get("surname");
        String completeName = name + " " + surname;
        nameView.setText(completeName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String mail = user.getEmail();
            mailView.setText(mail);
        }

        view.findViewById(R.id.menu_dots).setOnClickListener(
                (this::onMenuExpand)
        );


        View button = view.findViewById(R.id.user_icon);
        //TODO load image into view
        button.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PhotoDialogActivity.class);
            ActivityOptions options = ActivityOptions.
                    makeScaleUpAnimation(v,0,0,0, 0);
            startActivityForResult(intent,2, options.toBundle());

        });

        loadProfilePicture();
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);


        if(requestCode == 2 && resultCode == 3) {

            loadProfilePicture();
            getActivity().setResult(3);
        }
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
        Context c = getContext();
        if(c != null) {
            PopupMenu popupMenu = new PopupMenu(c, v);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.menu_my_houses, popupMenu.getMenu());
            popupMenu.show();
        }
    }

    public interface OnHouseListFragmentInteractionListener {
        void onItemClick(House item, View view, int clickedPosition);
    }

    private void loadProfilePicture()
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String lastEdit = readSharedPreferenceForCache();
        StorageReference reference = storage.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString() + "/profile.jpg" + lastEdit);
        GlideApp.with(getActivity())
                .load(reference)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        ivProfile.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        ivProfile.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .error(R.drawable.empty_profile_blue_circle)
                .apply(RequestOptions.circleCropTransform())
                .into(ivProfile);
    }

    private String readSharedPreferenceForCache()
    {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        return  sharedPref.getString("imageCaching","0");
    }
}
