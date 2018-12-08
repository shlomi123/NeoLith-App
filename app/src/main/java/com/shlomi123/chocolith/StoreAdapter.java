package com.shlomi123.chocolith;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

//TODO fix adapter with popup menu
public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {
    private Context mContext;
    private List<Store> mStores;
    private com.shlomi123.chocolith.StoreAdapter.OnItemClickListener mListener;

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
    }

    @Override
    public int getItemCount() {
        return mStores.size();
    }

    public class StoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
        public TextView Name;
        public TextView Address;
        public TextView Phone;
        public TextView Email;
        public TextView Orders;
        public ImageView button;


        public StoreViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

            Name = itemView.findViewById(R.id.textViewStoreName);
            Address = itemView.findViewById(R.id.textViewStoreAddress);
            Phone = itemView.findViewById(R.id.textViewStorePhone);
            Email = itemView.findViewById(R.id.textViewStoreEmail);
            Orders = itemView.findViewById(R.id.textViewStoreOrders);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem viewStore = menu.add(Menu.NONE, 1, 1, "view orders");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "delete");

            viewStore.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {
                        case 1:
                            mListener.onViewStore(position);
                            return true;
                        case 2:
                            mListener.onDeleteStore(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onViewStore(int position);

        void onDeleteStore(int position);
    }

    public void setOnItemClickListener(StoreAdapter.OnItemClickListener listener) {
        mListener = listener;
    }
}
