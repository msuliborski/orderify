package com.amm.orderify.bar;

        import android.os.Handler;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.widget.Button;

        import com.amm.orderify.R;
        import com.amm.orderify.bar.helpers.TablesRecyclerViewAdapter;
        import com.amm.orderify.helpers.data.*;

        import java.sql.ResultSet;
        import java.sql.SQLException;
        import java.sql.Statement;
        import java.util.ArrayList;
        import java.util.List;

        import static com.amm.orderify.helpers.JBDCDriver.*;

public class TablesActivity extends AppCompatActivity
{
    TablesRecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    List<Table> tables = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bar_tables_activity);

        recyclerView = findViewById(R.id.TablesRecycleView);

        tables = getFreshData();
        adapter = new TablesRecyclerViewAdapter(this, tables);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        Button refreshButton = findViewById(R.id.RefreshButton);
        refreshButton.setOnClickListener(v -> {
            updateAdapter();
        });

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable(){
//            public void run(){
//                updateAdapter();
//                handler.postDelayed(this, 1000);
//            }}, 1000);
    }

    private void updateAdapter(){
        if(!tables.equals(getFreshData())){
            adapter.setTables(tables);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private List<Table> getFreshData(){
        try {
            List<Table> tables = new ArrayList<>();
            List<Order> orders = new ArrayList<>();
            List<Wish> wishes = new ArrayList<>();
            List<Addon> addons = new ArrayList<>();
            Statement tablesS = getConnection().createStatement();
            ResultSet tablesRS = tablesS.executeQuery("SELECT * FROM tables");
            while (tablesRS.next()) {
                Statement ordersS = getConnection().createStatement();
                ResultSet ordersRS = ordersS.executeQuery("SELECT * FROM orders " +
                        "WHERE tableID = " + tablesRS.getInt("ID"));
                while (ordersRS.next()) {
                    Statement wishesS = getConnection().createStatement();
                    ResultSet wishesRS = wishesS.executeQuery("SELECT wishes.ID, dishID, name, price, amount, orderID FROM wishes\n" +
                            "JOIN dishes ON dishes.ID = wishes.dishID\n" +
                            "WHERE orderID = " + ordersRS.getInt("ID"));
                    while (wishesRS.next()) {
                        Statement addonsS = getConnection().createStatement();
                        ResultSet addonsRS = addonsS.executeQuery("SELECT addonID, name, price FROM addonsToWishes\n" +
                                "JOIN addons ON addons.ID = addonsToWishes.addonID\n" +
                                "WHERE wishID = " + wishesRS.getInt("ID"));
                        while (addonsRS.next()) {
                            addons.add(new Addon(addonsRS.getInt("addonID"), addonsRS.getString("name"), addonsRS.getFloat("price")));
                        }
                        Dish dish = new Dish(wishesRS.getInt("dishID"), wishesRS.getString("name"), wishesRS.getFloat("price"), null, null, null);
                        wishes.add(new Wish(dish, wishesRS.getInt("amount"), addons));
                        addons = new ArrayList<>();
                    }
                    orders.add(new Order(ordersRS.getInt("ID"), null, null, ordersRS.getInt("tableID"), ordersRS.getString("comments"), ordersRS.getInt("state"), wishes));
                    wishes = new ArrayList<>();
                }
                tables.add(new Table(tablesRS.getInt("ID"), tablesRS.getInt("number"), tablesRS.getString("description"), tablesRS.getInt("state"), orders));
                orders = new ArrayList<>();
            }
            return tables;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }
}
