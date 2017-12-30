package dima.it.polimi.blackboard.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.adapters.PaymentViewPagerAdapter;
import dima.it.polimi.blackboard.fragments.PaymentListFragment;
import dima.it.polimi.blackboard.fragments.TodoItemListFragment;
import dima.it.polimi.blackboard.model.PaymentItem;
import dima.it.polimi.blackboard.model.TodoItem;
import dima.it.polimi.blackboard.utils.DataGeneratorUtil;

public class BalanceActivity extends AppCompatActivity  implements PaymentListFragment.OnListFragmentInteractionListener{

    private PaymentListFragment listFragmentPositive;
    private PaymentListFragment listFragmentNegative;
    private CollapsingToolbarLayout collapsingToolbar;
    private List<PaymentItem> items = DataGeneratorUtil.generatePaymentItems(30);
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        mFab = findViewById(R.id.add_fab);
        mFab.setTransitionName("revealCircular");

        displayListFragment();
        collapsingToolbar = findViewById(R.id.balance_toolbar);
        refreshBalanceColor();


    }

    private void displayListFragment(){
        //Todo remove this call

        items = DataGeneratorUtil.generatePaymentItems(15);
        listFragmentPositive = PaymentListFragment.newInstance(1, getPosItems());
        listFragmentNegative = PaymentListFragment.newInstance(1,getNegItems());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
        PaymentViewPagerAdapter mViewPagerAdapter = new PaymentViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFragment(listFragmentNegative, "Negative");
        mViewPagerAdapter.addFragment(listFragmentPositive, "Positive");
        mViewPager.setAdapter(mViewPagerAdapter);
        /*getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_payment_list_container, listFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();*/

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    private List<PaymentItem> getPosItems()
    {
        //TODO change Stefy and simo
        //TODO fetch items from firebase
        List<PaymentItem> posItems = new ArrayList<PaymentItem>();
        for(PaymentItem p : items)
        {
            if(p.getReceiver().equals("Stefy & Simo") && !p.getEmitter().equals("Stefy & Simo") )
            {
                posItems.add(p);
            }
        }

        return posItems;
    }

    private List<PaymentItem> getNegItems()
    {
        //TODO change Stefy and simo
        //TODO fetch items from firebase
        List<PaymentItem> negItems = new ArrayList<PaymentItem>();
        for(PaymentItem p : items)
        {
            if(p.getEmitter().equals("Stefy & Simo") && !p.getReceiver().equals("Stefy & Simo"))
            {
                negItems.add(p);
            }
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

        if(!collapsingToolbar.getTitle().toString().contains("-")) {

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


        ActivityOptions options = ActivityOptions.
                makeSceneTransitionAnimation(this, mFab, mFab.getTransitionName());
        startActivity(intent, options.toBundle());
    }


}
