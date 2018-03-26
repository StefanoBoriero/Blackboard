package dima.it.polimi.blackboard.activities;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.Toast;


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
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.adapters.HouseListAdapter;
import dima.it.polimi.blackboard.adapters.PaymentViewPagerAdapter;
import dima.it.polimi.blackboard.fragments.PaymentListFragment;
import dima.it.polimi.blackboard.fragments.ProfileInfoFragment;
import dima.it.polimi.blackboard.fragments.TodoItemListFragment;
import dima.it.polimi.blackboard.model.House;
import dima.it.polimi.blackboard.model.PaymentItem;
import dima.it.polimi.blackboard.model.TodoItem;
import dima.it.polimi.blackboard.utils.DataGeneratorUtil;
import dima.it.polimi.blackboard.utils.UserDecoder;

public class BalanceActivity extends AppCompatActivity  implements PaymentListFragment.OnListFragmentInteractionListener, DialogInterface.OnClickListener{


    private static  CollapsingToolbarLayout collapsingToolbar;
    private List<PaymentItem> items = DataGeneratorUtil.generatePaymentItems(30);
    private FloatingActionButton mFab;
    private ArrayList<House> houses;
    private FirebaseFirestore db;
    private PaymentListFragment listFragmentPositive;
    private PaymentListFragment listFragmentNegative;
    private ViewPager mViewPager;
    private PaymentViewPagerAdapter mViewPagerAdapter;
    private ListenerRegistration myListener;
    public static int selectedHouse = 0;
    public static boolean isChangingHouse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        mFab = findViewById(R.id.add_fab);
        mFab.setTransitionName("revealCircular");

        db = FirebaseFirestore.getInstance();
        newGetHouses();



        collapsingToolbar = findViewById(R.id.balance_toolbar);
        refreshBalanceColor();
        isChangingHouse = false;



    }

    private void displayListFragment(){
        //Todo remove this call


        mViewPager = findViewById(R.id.viewpager);
        mViewPagerAdapter = new PaymentViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.startUpdate(mViewPager);
        listFragmentNegative = (PaymentListFragment) mViewPagerAdapter.instantiateItem(mViewPager,0);
        listFragmentPositive = (PaymentListFragment) mViewPagerAdapter.instantiateItem(mViewPager,1);

        listFragmentPositive.setType("positive");
        listFragmentNegative.setType("negative");


        if(houses.size()> 0) {
            listFragmentPositive.setHouse(houses.get(selectedHouse).getId().toString());
            listFragmentNegative.setHouse(houses.get(selectedHouse).getId().toString());
        }
        mViewPagerAdapter.finishUpdate(mViewPager);
        mViewPager.setAdapter(mViewPagerAdapter);
        TabLayout tabLayout =  findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);










        /*getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_payment_list_container, listFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();*/


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_account, menu);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        //TODO create string
        getSupportActionBar().setTitle("Balance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getMenuInflater().inflate(R.menu.menu_choose_house, menu);
        return true;
    }

    public void onChooseHouse(MenuItem menuItem){
        BalanceActivity.ChooseHouseDialog.mListener = this;
        DialogFragment houseListDialog = BalanceActivity.ChooseHouseDialog.newInstance(selectedHouse, houses);
        houseListDialog.show(getFragmentManager(), "dialog");
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(BalanceActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onRefresh()
    {
        refreshBalanceColor();
        listFragmentPositive.updateData();
        listFragmentNegative.updateData();
    }

    public static void refreshBalanceColor(double newPayment)
    {

        if(collapsingToolbar.getTitle() == null)
            collapsingToolbar.setTitle(String.format("%.2f",newPayment) + "€");
        else
        {
            double oldBalance = Double.parseDouble(collapsingToolbar.getTitle().toString().replace("€","").replace(",","."));
            double newBalance = oldBalance + newPayment;
            collapsingToolbar.setTitle(String.format("%.2f",newBalance) + "€");
        }




        if(collapsingToolbar.getTitle() != null && !collapsingToolbar.getTitle().toString().contains("-")) {

            collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPositive);
            collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPositive);
        }
        else {
            collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarnegative);
            collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBarNegative);
        }
    }

    public static void refreshBalanceColor() {
        collapsingToolbar.setTitle("0.00€");
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPositive);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPositive);
    }


    public void fabListener(View v){
        Intent intent = new Intent(this, NewPaymentActivity.class);
        intent.putExtra("HouseName",houses.get(selectedHouse).getId());

        ActivityOptions options = ActivityOptions.
                makeSceneTransitionAnimation(this, mFab, mFab.getTransitionName());
        startActivity(intent, options.toBundle());
    }


    private void getHouses(){
        DocumentReference user = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        user.get().addOnCompleteListener((task) -> {
            List<CharSequence> myHouses = new ArrayList<>();
            if (task.isSuccessful()) {
                {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> userParam = document.getData();
                    ArrayList<String> housesIds = (ArrayList<String>)userParam.get("houses");
                    houses = new ArrayList<>();
                    for(int i=0; i<housesIds.size(); i++){
                        String houseId = housesIds.get(i);
                        db.collection("houses").document(houseId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    String houseName = (String)task.getResult().getData().get("name");
                                    houses.add(new House(houseName,houseId));

                                    if(houses.size() == housesIds.size())
                                    {
                                        displayListFragment();
                                    }
                                }

                            }
                        });
                    }
                }

            } else {
                Toast.makeText(this,"Error retrieving houses",Toast.LENGTH_SHORT);
            }
        });
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
                if(dc.getType() == DocumentChange.Type.ADDED){
                    String name = (String)dc.getDocument().getData().get("name");
                    String id = dc.getDocument().getId();
                    Map<String,Object> houseData = dc.getDocument().getData();
                    Map<String,Object> roommates = (Map<String, Object>) houseData.get("roommates");
                    ArrayList<String> roomMatesList = (ArrayList<String>) roommates.get("roommates");
                    if(roomMatesList.contains(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())) {
                        House newHouse = new House(name, id);
                        UserDecoder.getInstance().populateFromHouse(id);
                        houses.add(newHouse);

                    }
                }
            }
            displayListFragment();
        });
    }




    @Override
    public void onClick(DialogInterface dialog, int selected) {
        this.isChangingHouse = false;
        this.selectedHouse = selected;
        String currentHouse = (String)this.houses.get(selectedHouse).getId();
        ((PaymentListFragment)listFragmentPositive).changeHouse(currentHouse);
        ((PaymentListFragment)listFragmentNegative).changeHouse(currentHouse);
        collapsingToolbar.setTitle("0.00€");
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPositive);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPositive);
        dialog.dismiss();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("house", selectedHouse);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.selectedHouse = savedInstanceState.getInt("house");
    }



    public static class ChooseHouseDialog extends DialogFragment{
        public static Dialog.OnClickListener mListener;

        public static BalanceActivity.ChooseHouseDialog newInstance(int selectedHouse, ArrayList<House> houses) {
            Bundle args = new Bundle();
            CharSequence[] houseChars = new CharSequence[houses.size()];
            for(int i = 0; i < houses.size(); i++)
            {
                houseChars[i] = houses.get(i).getName();
            }
            args.putCharSequenceArray("houses", houseChars);
            BalanceActivity.ChooseHouseDialog fragment = new BalanceActivity.ChooseHouseDialog();
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {

            int selectedHouse = BalanceActivity.selectedHouse;
            CharSequence[] houses = getArguments().getCharSequenceArray("houses");
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.action_choose_house);
            isChangingHouse = true;


            dialog.setSingleChoiceItems(houses, selectedHouse, mListener);
            return dialog.create();
        }

        @Override
        public void onCancel(final DialogInterface arg0)
        {
            isChangingHouse = false;
        }


    }
}
