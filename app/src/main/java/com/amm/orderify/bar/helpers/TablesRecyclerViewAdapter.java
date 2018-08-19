package com.amm.orderify.bar.helpers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.*;

import java.util.ArrayList;
import java.util.List;

public class TablesRecyclerViewAdapter extends RecyclerView.Adapter<TablesRecyclerViewAdapter.ViewHolder> {

    List<Table> tables;
    TablesRecyclerViewAdapter(List<Table> tables){
        this.tables = tables;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bar_order_element, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView tableNumberTextView =

    }

    @Override
    public int getItemCount() {
        return tables.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
