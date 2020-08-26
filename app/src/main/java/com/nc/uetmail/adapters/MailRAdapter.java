package com.nc.uetmail.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nc.uetmail.R;
import com.nc.uetmail.models.Product;

import java.util.ArrayList;

public class MailRAdapter extends RecyclerView.Adapter<MailRAdapter.MailViewHolder> {
    private ArrayList<Product> listProduct;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public MailRAdapter(Context mContext, ArrayList<Product> listProduct) {
        this.mContext = mContext;
        this.listProduct = listProduct;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public MailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.row_mail_list, viewGroup, false);
        return new MailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MailViewHolder holder, int pos) {
        Product product = listProduct.get(pos);
        holder.name.setText(product.getName());
        holder.price.setText(product.getPrice()+"");
        holder.productID.setText(product.getProductID()+"");
    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    class MailViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView price;
        private TextView productID;

        public MailViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.nameproduct);
            price = (TextView) itemView.findViewById(R.id.priceproduct);
            productID = (TextView) itemView.findViewById(R.id.idproduct);
        }
    }
}
