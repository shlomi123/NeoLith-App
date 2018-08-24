package com.shlomi123.chocolith;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {
    private Context mContext;
    private List<Store> mStores;
    //private com.shlomi123.chocolith.StoreAdapter.OnItemClickListener mListener;

    public StoreAdapter(Context context, List<Store> stores) {
        mContext = context;
        mStores = stores;
    }

    @Override
    public StoreAdapter.StoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.store_item, parent, false);
        return new StoreAdapter.StoreViewHolder(v);

    }

    @Override
    public void onBindViewHolder(StoreAdapter.StoreViewHolder holder, int position) {
        final Store StoreCurrent = mStores.get(position);
        holder.Name.setText(StoreCurrent.get_name());
        holder.Email.setText("Email: " + StoreCurrent.get_email());
        holder.Phone.setText("Phone: " + String.valueOf(StoreCurrent.get_phone()));
        holder.Address.setText("Address: " + StoreCurrent.get_address());
        if (StoreCurrent.getOrders() != null)
        {
            holder.Orders.setText("Order: " + String.valueOf(StoreCurrent.getOrders().size()));
        }
        else
        {
            holder.Orders.setText("Order: 0");
        }

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StoreCurrent.getOrders() != null)
                {
                    Intent intent = new Intent(mContext, ADMIN_VIEW_STORE_ORDERS.class);
                    intent.putExtra("NAME", StoreCurrent.get_name());
                    mContext.startActivity(intent);
                }
                else
                {
                    Toast.makeText(mContext, "No Orders", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStores.size();
    }

    public class StoreViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/{
        public TextView Name;
        public TextView Address;
        public TextView Phone;
        public TextView Email;
        public TextView Orders;
        public Button button;


        public StoreViewHolder(View itemView) {
            super(itemView);

            //itemView.setOnClickListener(this);
            Name = itemView.findViewById(R.id.textViewStoreName);
            Address = itemView.findViewById(R.id.textViewStoreAddress);
            Phone = itemView.findViewById(R.id.textViewStorePhone);
            Email = itemView.findViewById(R.id.textViewStoreEmail);
            Orders = itemView.findViewById(R.id.textViewStoreOrders);
            button = itemView.findViewById(R.id.buttonViewStoreOrders);


        }

        /*@Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }*/
    }

    /*public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(StoreAdapter.OnItemClickListener listener) {
        mListener = listener;
    }*/
}
