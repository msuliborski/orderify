package com.amm.orderify.client.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayout;
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

import static com.amm.orderify.client.MenuActivity.updateOrderList;
import static com.amm.orderify.client.MenuActivity.wishes;

public class MenuRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    Context context;
    List<Object> dishCategories;

    List<Addon> clickedAddons = new ArrayList<>();
    public MenuRecyclerViewAdapter(Context context, List<Object> dishCategories){
        this.dishCategories = dishCategories;
        this.context = context;
    }
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_list_element, parent, false);
            return new ViewHolderDish(view);
        } else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_list_header, parent, false);
            return new ViewHolderHeader(view);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder unknownHolder, int position) {
        if (unknownHolder instanceof ViewHolderHeader) {
            ((ViewHolderHeader) unknownHolder).HeaderTextView.setText(((DishCategory)dishCategories.get(position)).name);
        } else if (unknownHolder instanceof ViewHolderDish) {
            final Dish dish = (Dish)dishCategories.get(position);
            ViewHolderDish holder = (ViewHolderDish)unknownHolder;
            holder.nameTextView.setText(dish.name);
            holder.priceTextView.setText(dish.price + "");

            holder.addonCategoriesGridLayout.removeAllViews();

            for (int addonCategoriesNumber = 0; addonCategoriesNumber < ((Dish)dishCategories.get(position)).addonCategories.size(); addonCategoriesNumber++) {
                AddonCategory addonCategory = ((Dish)dishCategories.get(position)).addonCategories.get(addonCategoriesNumber);

                View addonCategoryElement = LayoutInflater.from(context).inflate(R.layout.expand_grid_element, null);

                TextView CategoryNameTextView = addonCategoryElement.findViewById(R.id.CategoryNameTextView);
                CategoryNameTextView.setText(addonCategory.name);

                LinearLayout AddonsLinearLayout = addonCategoryElement.findViewById(R.id.AddonsLinearLayout);
                for (int addonNumber = 0; addonNumber < addonCategory.addons.size(); addonNumber++) {
                    Addon addon = addonCategory.addons.get(addonNumber);
                    View addonElement = LayoutInflater.from(context).inflate(R.layout.expand_addon_list_element, null);
                    TextView AddonNameTextView = addonElement.findViewById(R.id.AddonNameTextView);
                    AddonNameTextView.setText(addon.name);
                    ImageView CheckboxCheckImage = addonElement.findViewById(R.id.CheckboxCheckImage);
                    if (addonCategory.multiChoice){
                        addonElement.setOnClickListener(e -> {
                            if(CheckboxCheckImage.getVisibility() == View.INVISIBLE) {
                                CheckboxCheckImage.setVisibility(View.VISIBLE);
                                clickedAddons.add(addon);
                            } else {
                                CheckboxCheckImage.setVisibility(View.INVISIBLE);
                                clickedAddons.remove(addon);
                            }
                        });
                    } else {
                        if (addonNumber == 0 && addonCategory.addons.size() > 1){ //czy działa?
                            CheckboxCheckImage.setVisibility(View.VISIBLE);
                            clickedAddons.add(addon); }
                        addonElement.setOnClickListener(e -> {
                            final int childCount = AddonsLinearLayout.getChildCount();
                            for (int ii = 0; ii < childCount; ii++) {
                                View vv = AddonsLinearLayout.getChildAt(ii);
                                ImageView iv = vv.findViewById(R.id.CheckboxCheckImage);
                                iv.setVisibility(View.INVISIBLE);
                                clickedAddons.remove(addonCategory.addons.get(ii));
                            }
                            CheckboxCheckImage.setVisibility(View.VISIBLE);
                            clickedAddons.add(addon);
                        });
                    }
                    AddonsLinearLayout.addView(addonElement);
                }
                holder.addonCategoriesGridLayout.addView(addonCategoryElement);

                android.support.v7.widget.AppCompatImageView addToOrderButton = holder.addToOrderButton;
                addToOrderButton.setOnClickListener(e -> {
                    Wish newWish = new Wish(dish, 1, clickedAddons);
                    for(int wishI = 0; wishI < wishes.size(); wishI++){
                        if (checkIfTheSame(wishes.get(wishI), newWish)) {
                            wishes.get(wishI).amount++; break;}
                        if (wishI == wishes.size()-1) {
                            wishes.add(newWish); break;}
                    }
                    if (wishes.size() == 0) wishes.add(newWish);
                    updateOrderList();
                    holder.menuExpand.setVisibility(View.GONE);
                    clickedAddons = new ArrayList<>();
                });

            }

        }
    }

    @Override
    public int getItemCount() {
        return dishCategories.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if (dishCategories.get(position) instanceof DishCategory)
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    public static class ViewHolderHeader extends RecyclerView.ViewHolder {
        TextView HeaderTextView;
        ViewHolderHeader(View itemView) {
            super(itemView);
            HeaderTextView = itemView.findViewById(R.id.HeaderTextView);
        }
    }
    public static class ViewHolderDish extends RecyclerView.ViewHolder {
        TextView priceTextView;
        TextView nameTextView;
        ConstraintLayout menuExpand;
        GridLayout addonCategoriesGridLayout;
        android.support.v7.widget.AppCompatImageView addToOrderButton;

        ViewHolderDish(View itemView) {
            super(itemView);
            priceTextView = itemView.findViewById(R.id.PriceTextView);
            nameTextView = itemView.findViewById(R.id.NameTextView);
            menuExpand = itemView.findViewById(R.id.MenuExpand);
            addToOrderButton = itemView.findViewById(R.id.AddToOrderButton);
            addonCategoriesGridLayout = itemView.findViewById(R.id.AddonCategoriesGridLayout);
        }
    }
    boolean checkIfTheSame(Wish w1, Wish w2){
        try {
            for (int i = 0; i < w1.addons.size(); i++) if (!(w1.addons.get(i).id == w2.addons.get(i).id)) return false;
            for (int i = 0; i < w2.addons.size(); i++) if (!(w1.addons.get(i).id == w2.addons.get(i).id)) return false;
            if (!(w1.dish.id == w2.dish.id)) return false;
            if (!(w1.addons.size() == w2.addons.size())) return false;
        }
        catch(Exception e){
            return false;
        }

        return true;
    }
}
