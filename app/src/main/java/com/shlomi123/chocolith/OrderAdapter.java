package com.shlomi123.chocolith;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context mContext;
    private List<Order> mOrders;
    private com.shlomi123.chocolith.OrderAdapter.OnItemClickListener mListener;
    private CircularProgressDrawable circularProgressDrawable;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public OrderAdapter(Context context, List<Order> orders) {
        mContext = context;
        mOrders = orders;
    }

    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.order_item, parent, false);
        return new OrderAdapter.OrderViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final OrderAdapter.OrderViewHolder holder, int position) {
        circularProgressDrawable = new CircularProgressDrawable(mContext);
        circularProgressDrawable.setStrokeWidth(10f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        final Order OrderCurrent = mOrders.get(position);

        holder.Name.setText(OrderCurrent.get_product());
        holder.Date.setText("Date: " + OrderCurrent.get_date().toString());
        holder.Quantity.setText("Quantity: " + String.valueOf(OrderCurrent.get_quantity()));

        CollectionReference products = db.collection("Products");
        //get image of product
        products.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for (DocumentSnapshot documentSnapshot: task.getResult())
                    {
                        Product product = documentSnapshot.toObject(Product.class);
                        if (product.getName().equals(OrderCurrent.get_product()))
                        {
                            StorageReference storageReference = storage.getReferenceFromUrl(product.getImageUrl());
                            Glide.with(mContext)
                                    .using(new FirebaseImageLoader())
                                    .load(storageReference)
                                    .fitCenter()
                                    .placeholder(circularProgressDrawable)
                                    .into(holder.imageView);
                        }

                    }
                }
                else
                {
                    Toast.makeText(mContext, "Error getting stores", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mOrders.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView Name;
        public TextView Date;
        public TextView Quantity;
        public ImageView imageView;


        public OrderViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            Name = itemView.findViewById(R.id.textViewProductName2);
            Date = itemView.findViewById(R.id.textViewProductOrderDate);
            Quantity = itemView.findViewById(R.id.textViewOrderQuantity);
            imageView = itemView.findViewById(R.id.imageViewProductImage);
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

    public void setOnItemClickListener(OrderAdapter.OnItemClickListener listener) {
        mListener = listener;
    }
}
