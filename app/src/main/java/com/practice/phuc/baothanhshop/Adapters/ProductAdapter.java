package com.practice.phuc.baothanhshop.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practice.phuc.baothanhshop.Models.VProduct;
import com.practice.phuc.baothanhshop.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context mContext;
    private List<VProduct> mProducts;

    public ProductAdapter(Context context, List<VProduct> products) {
        mContext = context;
        mProducts = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_product, viewGroup, false);
        return new ProductViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder viewHolder, int i) {
        viewHolder.tvProductName.setText(mProducts.get(i).getProductName());
        viewHolder.tvQuantityPerUnit.setText(mProducts.get(i).getQuantityPerUnit());
        viewHolder.tvUnitPrice.setText(mProducts.get(i).getUnitPrice() + "");
        viewHolder.tvSize.setText(mProducts.get(i).getSize());
        viewHolder.tvColor.setText(mProducts.get(i).getColor());
        viewHolder.tvMadeIn.setText(mProducts.get(i).getMadeIn());
        viewHolder.tvCategoryName.setText(mProducts.get(i).getCategoryName());
        viewHolder.tvDescription.setText(mProducts.get(i).getDescription());
    }

    @Override
    public int getItemCount() {
        return mProducts == null ? 0 : mProducts.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvQuantityPerUnit, tvUnitPrice, tvSize, tvColor, tvMadeIn,
                tvCategoryName, tvDescription;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvQuantityPerUnit = itemView.findViewById(R.id.tv_quantity_per_unit);
            tvUnitPrice = itemView.findViewById(R.id.tv_unit_price);
            tvSize = itemView.findViewById(R.id.tv_size);
            tvColor = itemView.findViewById(R.id.tv_color);
            tvMadeIn = itemView.findViewById(R.id.tv_made_in);
            tvCategoryName = itemView.findViewById(R.id.tv_category);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }
    }
}
