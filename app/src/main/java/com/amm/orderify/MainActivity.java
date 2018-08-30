package com.amm.orderify;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.amm.orderify.bar.TablesActivity;
import com.amm.orderify.client.MenuActivity;
import com.amm.orderify.config.RoleActivity;
import com.amm.orderify.helpers.data.Client;
import com.amm.orderify.helpers.data.Table;
import com.amm.orderify.maintenance.MaintenanceActivity;

import java.util.Timer;
import java.util.TimerTask;

import static com.amm.orderify.helpers.JBDCDriver.*;

public class MainActivity extends AppCompatActivity {


    public static Context context;

    public static int thisTableID;
    public static int thisClientID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_main_activity);


        //==========================================================================================
        /*

        //DATABASE
        -obsługa dań w wielu języka (do obgadania)
        -padlock TTL na czas a nie ilość odczytów (po przerobieniu czasu)

        //APP LOGIC
        -lifecycle nie zakłada wzywanai kelnera z poziomu menu (brak obsługi w TablesActivity)
        -czas na poprawnych timezone'ach (https://www.baeldung.com/java-8-date-time-intro)
        -pop-upy z potwierdzeniami (czy na pewno usunąć danie/czy na pewno wezwać kelnera)
        -wyswietlanie przywijajacych messages
        -edycja messages
        -automatyczne usuwanie zapłaconych dań z SummaryActivity oraz TablesActivity
        -wielki test wszystkich funkcjonalności!
        -sortowanie dynamiczne (TablesActivity i SummaryActivity)


        //DESIGN
        -kolorystyka w barze, bo boli
        -usunięcie przycisku cancel bill request
        -przebudowa podglądu orderu z wyświetlanymi dodatkami do niego w MenuActivity
        -redisign na tablet "wielocalowy"


         */
        //==========================================================================================



        context = this.getApplicationContext();

        InitiateConnection();
        ConnectToDatabase();

        this.startActivity(new Intent(MainActivity.this, RoleActivity.class));

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
                Intent homepage = new Intent(MainActivity.this, RoleActivity.class);
                startActivity(homepage);
            }
        }, 1);

        Button barButton = findViewById(R.id.BarButton);
        barButton.setOnClickListener(e -> {
            this.startActivity(new Intent(MainActivity.this, TablesActivity.class));
        });

        Button clientButton = findViewById(R.id.ClientButton);
        clientButton.setOnClickListener(e -> {
            this.startActivity(new Intent(MainActivity.this, MenuActivity.class));
        });

        Button addButton = findViewById(R.id.AddButton);
        addButton.setOnClickListener(e -> {
            this.startActivity(new Intent(MainActivity.this, MaintenanceActivity.class));
        });
    }
}
