package com.amm.orderify.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amm.orderify.MenuActivity2;
import com.amm.orderify.R;

import java.util.List;

//public class GridAdapter extends BaseAdapter
//{
//
//    private List<String> categoryNames;
//    private List<List<String>> listViewsLists;
//    private Context context;
//    private LayoutInflater inflater;
//
//    public GridAdapter (Context context, List<String> categoryNames, List<List<String>> listViewsLists)
//    {
//
//        this.categoryNames = categoryNames;
//        this.listViewsLists = listViewsLists;
//        this.context = context;
//        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }
//
//    @Override
//    public int getCount()
//    {
//        return listViewsLists.size();
//    }
//
//    @Override
//    public Object getItem(int i)
//    {
//        return listViewsLists.get(i);
//    }
//
//    @Override
//    public long getItemId(int position)
//    {
//        return 1;
//    }
//
//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup)
//    {
//        View gridView = view;
////
////        if (view == null)
////        {
////            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////            if (inflater != null)
////            {
////                gridView = inflater.inflate(R.layout.expand_grid_element, null);
////            }
////        }
////
////        ListView addonsListView = (ListView) gridView.findViewById(R.id.AddonsListView);
////        TextView categoryNameTextView = (TextView) gridView.findViewById(R.id.CategoryNameTextView);
////
////        addonsListView.setAdapter(new ListViewInGridAdapter(context, listViewsLists.get(i)));
////        categoryNameTextView.setText(categoryNames.get(i));
////
////
//
//        return gridView;
//    }
//}
