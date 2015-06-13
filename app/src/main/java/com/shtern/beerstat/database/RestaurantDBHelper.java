package com.shtern.beerstat.database;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Алексей on 05.01.2015.
 */
public class RestaurantDBHelper extends SQLiteOpenHelper {

public RestaurantDBHelper(Context context) {
        super(context, "restaurantDB", null, 1);
        }

@Override
public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        db.execSQL("create table restaurants (id integer primary key autoincrement, name text, UNIQUE(name));");


        }

@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        }

private String populateModel(Cursor c) {

        int nameColIndex = c.getColumnIndex("name");
        return c.getString(nameColIndex);
        }



public long createRestaurant(String name) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        return getWritableDatabase().insert("restaurants", null, values);
        }



public List<String> getRestaurants() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "select name from restaurants";
        Cursor c = db.rawQuery(query, null);

        List<String> restaurantList = new ArrayList<String>();
        if (c.moveToFirst())
        do restaurantList.add(populateModel(c));
        while (c.moveToNext());
        c.close();
        if (!restaurantList.isEmpty()) {
        return restaurantList;
        }

        return null;
        }


        }
