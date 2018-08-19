package com.amm.orderify.bar.helpers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.*;

import java.util.ArrayList;
import java.util.List;

public class TablesRecyclerViewAdapter extends RecyclerView.Adapter<TablesRecyclerViewAdapter.ViewHolder> {

    List<Table> tables;
    Context context;
    public TablesRecyclerViewAdapter(Context context, List<Table> tables){
        this.tables = tables;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bar_table_recyclerview_element, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tableNumberTextView.setText(tables.get(position).id + "");

        for(int i = 0; i < tables.get(position).orders.size(); i++){
            Log.wtf("fae", tables.get(position).orders.get(i).comments + "");
        }
        for(int orderNumber = 0; orderNumber < tables.get(position).orders.size(); orderNumber++)
        {
            View orderElement = LayoutInflater.from(context).inflate(R.layout.bar_order_element, null, false);
            TextView orderNumberTextView = orderElement.findViewById(R.id.OrderNumberTextView);
            TextView orderWaitingTimeTextView = orderElement.findViewById(R.id.OrderWaitingTimeTextView);
            TextView orderPriceTextView = orderElement.findViewById(R.id.OrderPriceTextView);
            TextView commentsTextView = orderElement.findViewById(R.id.CommentsTextView);

            orderNumberTextView.setText(tables.get(position).orders.get(orderNumber).id + "");
            commentsTextView.setText(tables.get(position).orders.get(orderNumber).comments);

            LinearLayout wishesLinearLayout = orderElement.findViewById(R.id.WishesLinearLayout);


            for (int wishNumber = 0; wishNumber < tables.get(position).orders.get(orderNumber).wishes.size(); wishNumber++)
            {
                View wishElement = LayoutInflater.from(context).inflate(R.layout.bar_wish_element, null, false);


                TextView dishNameTextView = wishElement.findViewById(R.id.DishNameTextView);

                dishNameTextView.setText(tables.get(position).orders.get(orderNumber).wishes.get(wishNumber).dish.name);

                LinearLayout addonsLinearLayout = wishElement.findViewById(R.id.AddonsLinearLayout);
                for(int addonNumber = 0; addonNumber < tables.get(position).orders.get(orderNumber).wishes.get(wishNumber).addons.size(); addonNumber++)
                {
                    View addonElement = LayoutInflater.from(context).inflate(R.layout.bar_addon_element, null, false);
                    TextView addonNameTextView = addonElement.findViewById(R.id.AddonNameTextView);
                    addonNameTextView.setText(tables.get(position).orders.get(orderNumber).wishes.get(wishNumber).addons.get(addonNumber).name);
                    addonsLinearLayout.addView(addonElement);
                }
//                GridLayout.LayoutParams params = (GridLayout.LayoutParams) wishElement.getLayoutParams();
//                params.width = (wishesGridLayout.getWidth()/wishesGridLayout.getColumnCount());
//                wishElement.setLayoutParams(params);

                wishesLinearLayout.addView(wishElement);
            }
            holder.ordersLinearLayout.addView(orderElement);
        }

    }

    @Override
    public int getItemCount() {
        return tables.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        TextView tableNumberTextView;
        TextView overallPriceTextView;
        LinearLayout ordersLinearLayout;


        public ViewHolder(View itemView) {
            super(itemView);

            tableNumberTextView = itemView.findViewById(R.id.TableNumberTextView);
            overallPriceTextView = itemView.findViewById(R.id.OverallPriceTextView);
            ordersLinearLayout = itemView.findViewById(R.id.OrdersLinearLayout);

        }
    }
}
