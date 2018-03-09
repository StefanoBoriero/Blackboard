package dima.it.polimi.blackboard.activities;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


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
import dima.it.polimi.blackboard.adapters.PaymentViewPagerAdapter;
import dima.it.polimi.blackboard.fragments.PaymentListFragment;
import dima.it.polimi.blackboard.fragments.TodoItemListFragment;
import dima.it.polimi.blackboard.model.PaymentItem;
import dima.it.polimi.blackboard.model.TodoItem;
import dima.it.polimi.blackboard.utils.DataGeneratorUtil;

public class BalanceActivity extends AppCompatActivity  implements PaymentListFragment.OnListFragmentInteractionListener, DialogInterface.OnClickListener{


    private CollapsingToolbarLayout collapsingToolbar;
    private List<PaymentItem> items = DataGeneratorUtil.generatePaymentItems(30);
    private FloatingActionButton mFab;
    private CharSequence[] houses;
    private FirebaseFirestore db;
    private int selectedHouse = 0;
    private PaymentListFragment listFragmentPositive;
    private PaymentListFragment listFragmentNegative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        mFab = findViewById(R.id.add_fab);
        mFab.setTransitionName("revealCircular");

        db = FirebaseFirestore.getInstance();
        getHouses();

        displayListFragment();
        collapsingToolbar = findViewById(R.id.balance_toolbar);
        refreshBalanceColor();





    }

    private void displayListFragment(){
        //Todo remove this call


        items = DataGeneratorUtil.generatePaymentItems(15);
        listFragmentPositive = PaymentListFragment.newInstance(1,"positive");
        listFragmentNegative = PaymentListFragment.newInstance(1,"negative");
        listFragmentPositive.setHouse("Sexy");
        listFragmentNegative.setHouse("Sexy");
        ViewPager mViewPager = findViewById(R.id.viewpager);
        PaymentViewPagerAdapter mViewPagerAdapter = new PaymentViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFragment(listFragmentNegative, "Negative");
        mViewPagerAdapter.addFragment(listFragmentPositive, "Positive");
        mViewPager.setAdapter(mViewPagerAdapter);
        /*getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_payment_list_container, listFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();*/

        TabLayout tabLayout =  findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
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

    private List<PaymentItem> getPosItems()
    {
        //TODO change Stefy and simo
        //TODO fetch items from firebase
        List<PaymentItem> posItems = new ArrayList<>();

        for(PaymentItem p : items)
        {
                posItems.add(p);

        }

        return posItems;

    }

    private List<PaymentItem> getNegItems()
    {
        //TODO change Stefy and simo
        //TODO fetch items from firebase
        List<PaymentItem> negItems = new ArrayList<>();
        for(PaymentItem p : items)
        {

                negItems.add(p);

        }

        return negItems;
    }

    public void onRefresh()
    {
        refreshBalanceColor();

    }

    private void refreshBalanceColor()
    {



        List<PaymentItem> posItems = getPosItems();
        List<PaymentItem> negItems = getNegItems();

        double sum  = 0;

        for(PaymentItem p : posItems)
        {
            sum = sum + p.getPrice();
        }




        for(PaymentItem p : negItems)
        {
            sum = sum - p.getPrice();
        }



        collapsingToolbar.setTitle(String.format("%.2f",sum) + "€");

        if(collapsingToolbar.getTitle() != null && !collapsingToolbar.getTitle().toString().contains("-")) {

            collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPositive);
            collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPositive);
        }
        else {
            collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarnegative);
            collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBarNegative);
        }
    }

    public void fabListener(View v){
        Intent intent = new Intent(this, NewPaymentActivity.class);
        intent.putExtra("HouseName",houses[selectedHouse]);

        ActivityOptions options = ActivityOptions.
                makeSceneTransitionAnimation(this, mFab, mFab.getTransitionName());
        startActivity(intent, options.toBundle());
    }


    private void getHouses(){
        //TODO change this user
        DocumentReference user = db.collection("users").document("Serento");
        user.get().addOnCompleteListener((task) -> {
            List<CharSequence> myHouses = new ArrayList<>();
            if (task.isSuccessful()) {
                {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> userParam = document.getData();
                    ArrayList<String> houses = (ArrayList<String>)userParam.get("houses");
                    this.houses = new CharSequence[houses.size()];
                    for(int i=0; i<houses.size(); i++){
                        this.houses[i] = houses.get(i);
                    }
                }

            } else {
                Toast.makeText(this,"Error retrieving houses",Toast.LENGTH_SHORT);
            }
        });
    }


    @Override
    public void onClick(DialogInterface dialog, int selected) {
        this.selectedHouse = selected;
        String currentHouse = (String)this.houses[selectedHouse];
        ((PaymentListFragment)listFragmentPositive).changeHouse(currentHouse);
        ((PaymentListFragment)listFragmentNegative).changeHouse(currentHouse);
        dialog.dismiss();

    }



    public static class ChooseHouseDialog extends DialogFragment{
        public static Dialog.OnClickListener mListener;

        public static BalanceActivity.ChooseHouseDialog newInstance(int selectedHouse, CharSequence[] houses) {
            Bundle args = new Bundle();
            args.putInt("chosen_house", selectedHouse);
            args.putCharSequenceArray("houses", houses);
            BalanceActivity.ChooseHouseDialog fragment = new BalanceActivity.ChooseHouseDialog();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            int selectedHouse = getArguments().getInt("chosen_house");
            CharSequence[] houses = getArguments().getCharSequenceArray("houses");
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.action_choose_house);
            dialog.setSingleChoiceItems(houses, selectedHouse, mListener);
            return dialog.create();
        }
    }
}
