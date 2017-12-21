package dima.it.polimi.blackboard.activities;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.fragments.PaymentListFragment;
import dima.it.polimi.blackboard.fragments.TodoItemListFragment;
import dima.it.polimi.blackboard.model.PaymentItem;
import dima.it.polimi.blackboard.model.TodoItem;
import dima.it.polimi.blackboard.utils.DataGeneratorUtil;

public class BalanceActivity extends AppCompatActivity  implements PaymentListFragment.OnListFragmentInteractionListener{

    private PaymentListFragment listFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        displayListFragment();
    }

    private void displayListFragment(){
        //Todo remove this call
        List<PaymentItem> items = DataGeneratorUtil.generatePaymentItems(30);
        listFragment = PaymentListFragment.newInstance(1, items);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_list_container, listFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }


    @Override
    public void onItemClick(PaymentItem item, View view) {

    }
}
