package com.shlomi123.chocolith;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class DistributorAdapter extends RecyclerView.Adapter<DistributorAdapter.DistributorViewHolder> {
    private Context mContext;
    private List<Distributor> mDistributor;
    private com.shlomi123.chocolith.DistributorAdapter.OnItemClickListener mListener;

    public DistributorAdapter(Context context, List<Distributor> distributors) {
        mContext = context;
        mDistributor = distributors;
    }

    @Override
    public DistributorAdapter.DistributorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.distributor_item, parent, false);
        return new DistributorAdapter.DistributorViewHolder(v);

    }

    @Override
    public void onBindViewHolder(DistributorAdapter.DistributorViewHolder holder, int position) {
        final Distributor distributorCurrent = mDistributor.get(position);
        holder.Name.setText(distributorCurrent.getName());
    }

    @Override
    public int getItemCount() {
        return mDistributor.size();
    }

    public class DistributorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView Name;


        public DistributorViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            //itemView.setOnCreateContextMenuListener(this);

            Name = itemView.findViewById(R.id.textViewDistributorName);
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
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(DistributorAdapter.OnItemClickListener listener) {
        mListener = listener;
    }
}
