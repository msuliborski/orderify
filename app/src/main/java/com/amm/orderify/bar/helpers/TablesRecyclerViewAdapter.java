package com.amm.orderify.bar.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.*;


import java.util.ArrayList;
import java.util.List;

public class TablesRecyclerViewAdapter extends RecyclerView.Adapter<TablesRecyclerViewAdapter.ViewHolder> {

    List<Table> tables;
    Context context;
    public TablesRecyclerViewAdapter(Context context, List<Table> tables){
        this.context = context;
        this.tables = tables;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bar_table_recyclerview_element, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Table table = tables.get(position);

        holder.tableNumberTextView.setText(table.number + "");
        holder.overallPriceTextView.setText(table.getTotalPrice() + " zł");
        holder.tableStateTextView.setText(table.state + " - tableState");

        holder.acceptRequestButton.setOnClickListener(v -> {
        });
        holder.expandCollapseButton.setOnClickListener(v -> {
        });
        holder.freezeStateButton.setOnClickListener(v -> {
        });

        for(int orderNumber = 0; orderNumber < table.orders.size(); orderNumber++) {
            Order order = table.orders.get(orderNumber);
            View orderElement = holder.orderElement;

            TextView orderNumberTextView = holder.orderNumberTextView;
            orderNumberTextView.setText(order.id + "");

            TextView orderWaitingTimeTextView = orderElement.findViewById(R.id.OrderWaitingTimeTextView);
            orderWaitingTimeTextView.setText("04:21");

            TextView orderPriceTextView = orderElement.findViewById(R.id.OrderPriceTextView);
            orderPriceTextView.setText(order.getTotalPrice() + " zł");

            TextView orderStateTextView = orderElement.findViewById(R.id.OrderStateTextView);
            orderStateTextView.setText(order.state + " - orderState");

            Button changeOrderStateButton = orderElement.findViewById(R.id.ChangeOrderStateButton);
            changeOrderStateButton.setOnClickListener(v -> {
            });

            TextView commentsTextView = orderElement.findViewById(R.id.CommentsTextView);
            commentsTextView.setText(order.comments);

            LinearLayout wishesLinearLayout = orderElement.findViewById(R.id.WishesLinearLayout);
            for (int wishNumber = 0; wishNumber < order.wishes.size(); wishNumber++) {
                Wish wish = order.wishes.get(wishNumber);
                View wishElement = LayoutInflater.from(context).inflate(R.layout.bar_wish_element, null, false);

                TextView dishNameTextView = wishElement.findViewById(R.id.DishNameTextView);
                dishNameTextView.setText(wish.dish.name);

                LinearLayout addonsLinearLayout = wishElement.findViewById(R.id.AddonsLinearLayout);
                for(int addonNumber = 0; addonNumber < wish.addons.size(); addonNumber++) {
                    Addon addon = wish.addons.get(addonNumber);

                    View addonElement = LayoutInflater.from(context).inflate(R.layout.bar_addon_element, null, false);

                    TextView addonNameTextView = addonElement.findViewById(R.id.AddonNameTextView);
                    addonNameTextView.setText(addon.name);

                    addonsLinearLayout.addView(addonElement);
                }
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
        TextView tableStateTextView;

        Button acceptRequestButton;
        Button expandCollapseButton;
        Button freezeStateButton;

        LinearLayout ordersLinearLayout;

        View orderElement;
        TextView orderNumberTextView;


        public ViewHolder(View itemView) {
            super(itemView);
            tableNumberTextView = itemView.findViewById(R.id.TableNumberTextView);
            overallPriceTextView = itemView.findViewById(R.id.OverallPriceTextView);
            tableStateTextView = itemView.findViewById(R.id.TableStateTextView);

            acceptRequestButton = itemView.findViewById(R.id.AcceptRequestButton);
            expandCollapseButton = itemView.findViewById(R.id.ExpandCollapseButton);
            freezeStateButton = itemView.findViewById(R.id.FreezeStateButton);

            ordersLinearLayout = itemView.findViewById(R.id.OrdersLinearLayout);

            orderElement = LayoutInflater.from(itemView.getContext()).inflate(R.layout.bar_order_element, null, false);
            orderNumberTextView = orderElement.findViewById(R.id.OrderNumberTextView);
        }
    }
}
