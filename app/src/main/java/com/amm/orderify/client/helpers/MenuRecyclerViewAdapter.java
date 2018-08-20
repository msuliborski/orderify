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
            //inflate your layout and pass it to view holder
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_list_header, parent, false);
            return new ViewHolderDish(view);
        } else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_list_header, parent, false);
            return new ViewHolderHeader(view);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderHeader)
        {
            ((ViewHolderHeader) holder).HeaderTextView.setText(((DishCategory)dishCategories.get(position)).name);
        }
        else if (holder instanceof ViewHolderDish)
        {
            ((ViewHolderDish) holder).NameTextView.setText(((Dish)dishCategories.get(position)).name);
            ((ViewHolderDish) holder).PriceTextView.setText(((Dish)dishCategories.get(position)).price + "");


                for (int addonCategoriesNumber = 0; addonCategoriesNumber < ((Dish)dishCategories.get(position)).addonCategories.size(); addonCategoriesNumber++)
                {
                    AddonCategory addonCategory = ((Dish)dishCategories.get(position)).addonCategories.get(addonCategoriesNumber);
                    View addonCategoryElement = LayoutInflater.from(context).inflate(R.layout.expand_grid_element, null);
                    TextView CategoryNameTextView = addonCategoryElement.findViewById(R.id.CategoryNameTextView);

                    CategoryNameTextView.setText(addonCategory.name); //cat name

                    LinearLayout AddonsLinearLayout = addonCategoryElement.findViewById(R.id.AddonsLinearLayout);
                    for (int addonNumber = 0; addonNumber < addonCategory.addons.size(); addonNumber++)
                    {
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
                    ((ViewHolderDish) holder).MenuExpand.addView(addonCategoryElement);
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

        public ViewHolderHeader(View itemView) {
            super(itemView);
            HeaderTextView = itemView.findViewById(R.id.HeaderTextView);

        }
    }
    public static class ViewHolderDish extends RecyclerView.ViewHolder {
        TextView PriceTextView;
        TextView NameTextView;
        ConstraintLayout MenuExpand;

        public ViewHolderDish(View itemView) {
            super(itemView);
            PriceTextView = itemView.findViewById(R.id.PriceTextView);
            NameTextView = itemView.findViewById(R.id.NameTextView);
            MenuExpand = itemView.findViewById(R.id.MenuExpand);

        }
    }
}
