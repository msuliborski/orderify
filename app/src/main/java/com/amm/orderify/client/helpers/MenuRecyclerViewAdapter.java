package com.amm.orderify.client.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amm.orderify.R;
import com.amm.orderify.helpers.data.*;

import java.util.ArrayList;
import java.util.List;

public class MenuRecyclerViewAdapter extends RecyclerView.Adapter<MenuRecyclerViewAdapter.ViewHolder> {

    Context context;
    List<DishCategory> dishCategories;
    List<Addon> clickedAddons = new ArrayList<>();
    public MenuRecyclerViewAdapter(Context context, List<DishCategory> dishCategories){
        this.dishCategories = dishCategories;
        this.context = context;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_list_header, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.HeaderTextView.setText(dishCategories.get(position).name);
        DishCategory dishCategory = dishCategories.get(position);
        for(int dishNumber = 0; dishNumber < dishCategory.dishes.size(); dishNumber++) {
            Dish dish = dishCategory.dishes.get(dishNumber);
            View dishElement = LayoutInflater.from(context).inflate(R.layout.menu_list_element, null, false);
            TextView NameTextView = dishElement.findViewById(R.id.NameTextView);
            TextView PriceTextView = dishElement.findViewById(R.id.PriceTextView);
            NameTextView.setText(dish.name);
            PriceTextView.setText(dish.price+"");

            ConstraintLayout addonCategoriesConstraintLayout = dishElement.findViewById(R.id.MenuExpand);
            for (int addonCategoriesNumber = 0; addonCategoriesNumber < dish.addonCategories.size(); addonCategoriesNumber++) {
                AddonCategory addonCategory = dish.addonCategories.get(addonCategoriesNumber);
                View addonCategoryElement = LayoutInflater.from(context).inflate(R.layout.expand_grid_element, null);
                TextView CategoryNameTextView = addonCategoryElement.findViewById(R.id.CategoryNameTextView);

                CategoryNameTextView.setText(addonCategory.name); //cat name

                LinearLayout AddonsLinearLayout = addonCategoryElement.findViewById(R.id.AddonsLinearLayout);
                for (int addonNumber = 0; addonNumber < addonCategory.addons.size(); addonNumber++ ) {
                    Addon addon = addonCategory.addons.get(addonNumber);
                    View addonElement = LayoutInflater.from(context).inflate(R.layout.expand_addon_list_element, null);
                    TextView AddonNameTextView = addonElement.findViewById(R.id.AddonNameTextView);
                    AddonNameTextView.setText(addon.name);
//                    ImageView CheckboxCheckImage = addonElement.findViewById(R.id.CheckboxCheckImage);
//                    if (addonCategory.multiChoice){
//                        addonElement.setOnClickListener(e -> {
//                            if(CheckboxCheckImage.getVisibility() == View.INVISIBLE) {
//                                CheckboxCheckImage.setVisibility(View.VISIBLE);
//                                clickedAddons.add(addon);
//                            } else {
//                                CheckboxCheckImage.setVisibility(View.INVISIBLE);
//                                clickedAddons.remove(addon);
//                            }
//                        });
//                    } else {
//                        if (addonNumber == 0 && addonCategory.addons.size() > 1){ //czy działa?
//                            CheckboxCheckImage.setVisibility(View.VISIBLE);
//                            clickedAddons.add(addon); }
//
//                        addonElement.setOnClickListener(e -> {
//                            final int childCount = AddonsLinearLayout.getChildCount();
//                            for (int ii = 0; ii < childCount; ii++) {
//                                View vv = AddonsLinearLayout.getChildAt(ii);
//                                ImageView iv = vv.findViewById(R.id.CheckboxCheckImage);
//                                iv.setVisibility(View.INVISIBLE);
//                                clickedAddons.remove(addonCategory.addons.get(ii));
//                            }
//                            CheckboxCheckImage.setVisibility(View.VISIBLE);
//                            clickedAddons.add(addon);
//                        });
//                    }
                    AddonsLinearLayout.addView(addonElement);
                }
                addonCategoriesConstraintLayout.addView(addonCategoryElement);
            }
            holder.DishesLinearLayout.addView(dishElement);
        }


    }

    @Override
    public int getItemCount() {
        return dishCategories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView HeaderTextView;
        LinearLayout DishesLinearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            HeaderTextView = itemView.findViewById(R.id.HeaderTextView);
            DishesLinearLayout = itemView.findViewById(R.id.DishesLinearLayout);

        }
    }
}
