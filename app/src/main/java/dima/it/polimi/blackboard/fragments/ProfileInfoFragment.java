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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dima.it.polimi.blackboard.R;

import dima.it.polimi.blackboard.activities.AddHouseDialogActivity;
import dima.it.polimi.blackboard.activities.PhotoDialogActivity;
import dima.it.polimi.blackboard.adapters.HouseListAdapter;
import dima.it.polimi.blackboard.model.House;
import dima.it.polimi.blackboard.model.PaymentItem;
import dima.it.polimi.blackboard.model.User;
import dima.it.polimi.blackboard.utils.DataGeneratorUtil;
import dima.it.polimi.blackboard.utils.GlideApp;
import dima.it.polimi.blackboard.utils.UserDecoder;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileInfoFragment extends Fragment implements HouseListAdapter.HouseListAdapterListener{

    private ProfileInfoFragment.OnHouseListFragmentInteractionListener mListener;
    private ImageView ivProfile;
    private FirebaseFirestore db;
    private List<House> houses;
    private RecyclerView rv;
    private ListenerRegistration myListener;

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
        db = FirebaseFirestore.getInstance();

        rv = view.findViewById(R.id.recycler_view_house);
        newGetHouses();


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
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent intent = new Intent(getActivity(), AddHouseDialogActivity.class);
                    startActivity(intent);
                    return true;
                }
            });
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
        StorageReference reference = storage.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString() + "/profile" + lastEdit);
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

    //retrieve the last update to the photo profile, so we can get the URL
    private String readSharedPreferenceForCache()
    {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);

        String imageCaching =  sharedPref.getString("imageCaching","0");
        //this means cache has been cleaned, we need to retrieve the value
        if(imageCaching == "0")
        {
            DocumentReference userReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            userReference.get().addOnCompleteListener((task) -> {
                List<CharSequence> myHouses = new ArrayList<>();
                if (task.isSuccessful()) {
                    {
                        DocumentSnapshot document = task.getResult();
                        Map<String, Object> userParam = document.getData();
                        String lastEdit = (String)userParam.get("lastEdit");
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("imageCaching", lastEdit);
                        editor.commit();
                    }

                } else {
                    Toast.makeText(getActivity(),"Failed in retrieving profile image",Toast.LENGTH_SHORT);
                }
            });
        }
        return  sharedPref.getString("imageCaching","0");
    }



    private void newGetHouses()
    {
       CollectionReference user = db.collection("houses");
       houses = new ArrayList<>();

        myListener = user.addSnapshotListener( (querySnapshot, error) ->
        {
            if (error != null) {
                return;
            }

            for(DocumentChange dc: querySnapshot.getDocumentChanges()){
                String name = (String)dc.getDocument().getData().get("name");
                String id = dc.getDocument().getId();
                if(dc.getType() == DocumentChange.Type.ADDED){
                    Map<String,Object> houseData = dc.getDocument().getData();
                    Map<String,Object> roommates = (Map<String, Object>) houseData.get("roommates");
                    if(roommates != null)
                    {
                    ArrayList<String> roomMatesList = (ArrayList<String>) roommates.get("roommates");
                    if(roomMatesList.contains(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())) {
                        House newHouse = new House(name, id);
                        UserDecoder.getInstance().populateFromHouse(id);
                        houses.add(newHouse);
                        RecyclerView.Adapter adapter = new HouseListAdapter(houses, ProfileInfoFragment.this);
                        rv.setLayoutManager(new GridLayoutManager(getContext(), 3));
                        rv.setAdapter(adapter);
                    }
                    }
                }
                else if(dc.getType() == DocumentChange.Type.MODIFIED)
                {
                    Map<String,Object> houseData = dc.getDocument().getData();
                    Map<String,Object> roommates = (Map<String, Object>) houseData.get("roommates");
                    if(roommates != null)
                    {
                        ArrayList<String> roomMatesList = (ArrayList<String>) roommates.get("roommates");
                        if(!roomMatesList.contains(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())) {
                            boolean found = false;
                            int i = 0;
                            for(; i< houses.size() && found == false;i++)
                            {
                                if(houses.get(i).getId().equals(id))
                                    found = true;
                            }
                            if(found)
                                houses.remove(i-1);
                            RecyclerView.Adapter adapter = new HouseListAdapter(houses, ProfileInfoFragment.this);
                            rv.setLayoutManager(new GridLayoutManager(getContext(), 3));
                            rv.setAdapter(adapter);
                        }
                    }
                }
            }

        });
    }

}
