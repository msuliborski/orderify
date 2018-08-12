package com.amm.orderify.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amm.orderify.R;

import java.util.List;

//public class ListViewInGridAdapter extends BaseAdapter
//{
//    List<String> addonsList = null;
//
//    private LayoutInflater inflater;
//
//
//    ListViewInGridAdapter(Context context, List<String> addonsList)
//    {
//        this.addonsList = addonsList;
//        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }
//
//    @Override
//    public int getCount()
//    {
//        return addonsList.size();
//    }
//
//    @Override
//    public Object getItem(int i)
//    {
//        return addonsList.get(i);
//    }
//
//    @Override
//    public long getItemId(int i)
//    {
//        return 1;
//    }
//
//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup)
//    {
//
//        if (view == null)
//        {
//            view = inflater.inflate(R.layout.expand_addon_list_element, null);
//        }
//
//
//        TextView addonNameTextView = view.findViewById(R.id.AddonNameTextView);
//
//        addonNameTextView.setText(addonsList.get(i));
//
//        return view;
//    }
//}