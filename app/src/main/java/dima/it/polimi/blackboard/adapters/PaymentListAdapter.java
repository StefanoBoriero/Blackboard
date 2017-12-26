package dima.it.polimi.blackboard.adapters;

/**
 * Created by simone on 21/12/2017.
 */


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.model.PaymentItem;
import dima.it.polimi.blackboard.model.TodoItem;


public class PaymentListAdapter extends RecyclerView.Adapter<PaymentListAdapter.PaymentViewHolder> {
    private Context mContext;
    private List<PaymentItem> paymentItems;
    private PaymentListAdapterListener mListener;
    private ViewGroup parent;

    public PaymentListAdapter(Context context, List<PaymentItem> paymentItems, PaymentListAdapterListener listener){
        this.mContext = context;
        List<PaymentItem> filteredItems = new ArrayList<>();
        for (PaymentItem item : paymentItems) {
            if(item.getPrice() != 0)
            {
                filteredItems.add(item);
            }
        }
        this.paymentItems = filteredItems;
        this.mListener = listener;
    }

    @Override
    public PaymentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payment_item_row, parent, false);
        return new PaymentViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(final PaymentViewHolder holder, int position) {
        final PaymentItem paymentItem = paymentItems.get(position);


       // holder.todoItemName.setText(paymentItem.getName());
        //TODO change all this
        if(paymentItem.getEmitter().equals("Stefy & Simo") ) {
            holder.todoItemName.setText("To: " + paymentItem.getReceiver());
            holder.positiveNegativeIconContainer.setBackground(mContext.getResources().getDrawable(R.drawable.grey_euro));
        }
        else if(paymentItem.getReceiver().equals("Stefy & Simo")) {
            holder.todoItemName.setText("From: " + paymentItem.getEmitter());
            holder.positiveNegativeIconContainer.setBackground(mContext.getResources().getDrawable(R.drawable.grey_euro));
        }

        holder.todoItemPrice.setText("Amount: " + String.format("%.2f", paymentItem.getPrice()) +" €");


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentItems.size();
    }

    public PaymentItem getItem(int position){
        return paymentItems.get(position);
    }
    public void removeItem(int position){
        paymentItems.remove(position);
        notifyItemRemoved(position);
    }

    public void insertItem(PaymentItem item, int position){
        paymentItems.add(position, item);
        notifyItemInserted(position);
    }



    public interface PaymentListAdapterListener {

    }

    public class PaymentViewHolder extends RecyclerView.ViewHolder{


        private TextView todoItemName;
        private TextView todoItemPrice;
        private LinearLayout todoItemContainer;
        private RelativeLayout positiveNegativeIconContainer;

        public RelativeLayout viewForeground;
        public RelativeLayout viewBackground;

        public PaymentViewHolder(View itemView) {
            super(itemView);
            viewForeground = itemView.findViewById(R.id.foreground_row);
            viewBackground = itemView.findViewById(R.id.background_row);
            todoItemName = itemView.findViewById(R.id.payment_info);
            todoItemPrice = itemView.findViewById(R.id.amount_info);
            todoItemContainer = itemView.findViewById(R.id.payment_item_container);
            positiveNegativeIconContainer = itemView.findViewById(R.id.positive_negative_icon_container);
        }
    }
}
