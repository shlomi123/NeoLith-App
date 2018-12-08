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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

//Product adapter
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Product> mProducts;
    private com.shlomi123.chocolith.ImageAdapter.OnItemClickListener mListener;
    private CircularProgressDrawable circularProgressDrawable;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public ImageAdapter(Context context, List<Product> Products) {
        mContext = context;
        mProducts = Products;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        circularProgressDrawable = new CircularProgressDrawable(mContext);
        circularProgressDrawable.setStrokeWidth(10f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        Product ProductCurrent = mProducts.get(position);
        holder.textViewName.setText(ProductCurrent.getName());
        holder.textViewCost.setText("Cost/Unit: " + ProductCurrent.getCost() + "$");
        holder.textViewUnits.setText("Units/Package: " + ProductCurrent.getUnits_per_package());

        StorageReference storageReference = storage.getReferenceFromUrl(ProductCurrent.getImageUrl());
        Glide.with(mContext)
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .fitCenter()
                .placeholder(circularProgressDrawable)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
        public TextView textViewName;
        public TextView textViewCost;
        public TextView textViewUnits;
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewCost = itemView.findViewById(R.id.text_view_cost);
            textViewUnits = itemView.findViewById(R.id.text_view_units);
            imageView = itemView.findViewById(R.id.image_view_product);
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
            MenuItem edit = menu.add(Menu.NONE, 1, 1, "edit");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "delete");

            edit.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {
                        case 1:
                            mListener.onEditProduct(position);
                            return true;
                        case 2:
                            mListener.onDeleteProduct(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onEditProduct(int position);

        void onDeleteProduct(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
