package com.shtern.beerstat;

import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class OrderActivity extends ActionBarActivity {

    TextView order_restaurant;
    TextView order_date;
    Button cancel_button;
    Button ok_button;
    ListView order_lv;
    OrderAdapter order_adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


        order_lv = (ListView) findViewById(R.id.checkout_lv);
        order_lv.setFadingEdgeLength(0);
        order_lv.setVerticalScrollBarEnabled(false);
        order_lv.setHorizontalScrollBarEnabled(false);
        order_lv.setVerticalFadingEdgeEnabled(false);
        order_lv.setHorizontalFadingEdgeEnabled(false);
        order_lv.setFadingEdgeLength(0);
        order_adapter = new OrderAdapter(getBeerListString(),getApplicationContext());
        order_lv.setAdapter(order_adapter);

        order_restaurant = (TextView) findViewById(R.id.checkout_restaurant);
        order_restaurant.setText(MainActivity.restaurantAuto.getText().toString());

        order_date = (TextView) findViewById(R.id.checkout_date);
        order_date.setText(MainActivity.datetv.getText().toString());

        cancel_button = (Button) findViewById(R.id.checkout_cancel_button);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(0);
                finish();
            }
        });

        ok_button = (Button) findViewById(R.id.checkout_ok_button);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(1);
                finish();
            }
        });
    }

    public ArrayList<String> getBeerListString(){
       ArrayList<String> result = new ArrayList<String>();
        for (int i= 0; i<MainActivity.beerlist.size(); i++)
        {
            if (MainActivity.beerlist.get(i).count>0)
            result.add(MainActivity.beerlist.get(i).name+" "+MainActivity.beerlist.get(i).liters+" - "+MainActivity.beerlist.get(i).count);
        }
        return result;

    }

}
