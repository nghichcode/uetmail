package com.nc.uetmail.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nc.uetmail.R;
import com.nc.uetmail.models.Product;

import java.util.ArrayList;

public class ListMailAdapter extends BaseAdapter {
    final ArrayList<Product> listProduct;

    public ListMailAdapter(ArrayList<Product> listProduct){
        this.listProduct = listProduct;
    }

    @Override
    public int getCount() {
        return listProduct.size();
    }

    @Override
    public Object getItem(int position) {
        return listProduct.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listProduct.get(position).getProductID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewProduct;
        if (convertView == null) {
            viewProduct = View.inflate(parent.getContext(), R.layout.row_mail_list, null);
        } else viewProduct = convertView;
        Product product = (Product) getItem(position);

        ((TextView) viewProduct.findViewById(R.id.idproduct)).setText(String.format("ID = %d", product.getProductID()));
        ((TextView) viewProduct.findViewById(R.id.nameproduct)).setText(String.format("Tên SP : %s", product.getName()));
        ((TextView) viewProduct.findViewById(R.id.priceproduct)).setText(String.format("Giá %d", product.getPrice()));

        return viewProduct;
    }
}
