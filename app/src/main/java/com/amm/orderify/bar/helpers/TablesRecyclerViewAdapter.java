package com.amm.orderify.bar.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.*;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class TablesRecyclerViewAdapter extends RecyclerView.Adapter<TablesRecyclerViewAdapter.ViewHolder> {

    private List<Table> tables;
    private Context context;
    public TablesRecyclerViewAdapter(Context context, List<Table> tables){
        this.context = context;
        this.tables = tables;
    }

    public void setTables(List<Table> tables){
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

        //states: 1-unfreezed, 2-freezed, 3-wantsHelp =============================================================
        holder.acceptRequestButton.setOnClickListener(v -> {
            if(table.state == 3) {
                table.state = 1;
                try {
                    ExecuteUpdate("UPDATE tables SET state = " + table.state +  " WHERE ID = " + table.id);
                } catch (SQLException ignored) {}
            }
        });

        holder.expandCollapseButton.setOnClickListener(v -> {
            if(holder.ordersLinearLayout.getVisibility() == View.GONE) holder.ordersLinearLayout.setVisibility(View.VISIBLE);
            else holder.ordersLinearLayout.setVisibility(View.GONE);
        });

        holder.freezeStateButton.setOnClickListener(v -> {
            if(table.state == 1) {
                table.state = 2;
                holder.freezeStateButton.setText("Unfreez table");
            } else {
                table.state = 1;
                holder.freezeStateButton.setText("Freez table");
            }
            try {
                ExecuteUpdate("UPDATE tables SET state = " + table.state +  " WHERE ID = " + table.id);
            } catch (SQLException ignored) {}
        });

        for(int orderNumber = 0; orderNumber < table.orders.size(); orderNumber++) {
            Order order = table.orders.get(orderNumber);
            View orderElement = LayoutInflater.from(context).inflate(R.layout.bar_order_element, null, false);

            TextView orderNumberTextView = orderElement.findViewById(R.id.OrderNumberTextView);
            orderNumberTextView.setText(order.id + "");

            TextView orderWaitingTimeTextView = orderElement.findViewById(R.id.OrderWaitingTimeTextView);
            orderWaitingTimeTextView.setText("04:21");

            TextView orderPriceTextView = orderElement.findViewById(R.id.OrderPriceTextView);
            orderPriceTextView.setText(order.getTotalPrice() + " zł");

            TextView orderStateTextView = orderElement.findViewById(R.id.OrderStateTextView);
            orderStateTextView.setText(order.state + " - orderState");

            //states: 1-inPreparation, 2-doneAndDelivered =========================================================================
            Button changeOrderStateButton = orderElement.findViewById(R.id.ChangeOrderStateButton);
            changeOrderStateButton.setOnClickListener(v -> {
                if(order.state == 1) {
                    order.state = 2;
                    changeOrderStateButton.setVisibility(View.GONE);
                    orderStateTextView.setText(order.state + " - orderState");
                }
                try {
                    ExecuteUpdate("UPDATE orders SET state = " + order.state +  " WHERE ID = " + order.id);
                } catch (SQLException ignored) {}
            });

            ImageButton deleteOrderButton = orderElement.findViewById(R.id.DeleteOrderButton);
            deleteOrderButton.setOnClickListener(v -> {
                try {
                    ExecuteUpdate("DELETE FROM orders WHERE ID = " + order.id);
                } catch (SQLException ignored) {}
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tableNumberTextView;
        TextView overallPriceTextView;
        TextView tableStateTextView;

        Button acceptRequestButton;
        Button expandCollapseButton;
        Button freezeStateButton;

        LinearLayout ordersLinearLayout;
        ViewHolder(View itemView) {
            super(itemView);
            tableNumberTextView = itemView.findViewById(R.id.TableNumberTextView);
            overallPriceTextView = itemView.findViewById(R.id.OverallPriceTextView);
            tableStateTextView = itemView.findViewById(R.id.TableStateTextView);

            acceptRequestButton = itemView.findViewById(R.id.AcceptRequestButton);
            expandCollapseButton = itemView.findViewById(R.id.ExpandCollapseButton);
            freezeStateButton = itemView.findViewById(R.id.FreezeStateButton);

            ordersLinearLayout = itemView.findViewById(R.id.OrdersLinearLayout);
        }
    }
}
