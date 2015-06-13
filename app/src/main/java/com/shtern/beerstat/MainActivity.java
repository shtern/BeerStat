package com.shtern.beerstat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.shtern.beerstat.database.RestaurantDBHelper;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final String appKey = "ymjuuhq2kx6qxag";
    private static final String appSecret = "v21djlj9d1a9t21";
    private static String username = "";
    public static List<BeerItem> beerlist;
    private static final int REQUEST_LINK_TO_DBX = 0;
    private Calendar cal;
    private String ORDER_PATH="";
    private RestaurantDBHelper dbHelper;
    private List<String> restaurantList;
    private Button mLinkButton;
    private DbxAccountManager mDbxAcctMgr;
    private TextView agentname;
    static public BeerAdapter adapter;
    private ListView beerlv;
    public static EditText datetv;
    public static AutoCompleteTextView restaurantAuto;
    private Button okbutton;
    private ImageButton listbutton;
    public SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new RestaurantDBHelper(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        restaurantList = dbHelper.getRestaurants();
        if (restaurantList==null) restaurantList = new ArrayList<String>();
        beerlist =  new ArrayList<BeerItem>();
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE,1);
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                doDropboxTest();
                mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        if (loadPrefs(getApplicationContext())=="" || loadPrefs(getApplicationContext())==null || loadPrefs(getApplicationContext()).isEmpty())
        {
            Intent intent = new Intent(getApplicationContext(),UsernameActivity.class);
            startActivity(intent);
            this.finish();
        }
        datetv = (EditText) findViewById(R.id.dateET);
        datetv.setText(cal.get(Calendar.DAY_OF_MONTH)+"/"+String.valueOf(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR));
        datetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog();
            }
        });

        beerlv = (ListView) findViewById(R.id.beerlv);
        beerlv.setFadingEdgeLength(0);
        beerlv.setVerticalScrollBarEnabled(false);
        beerlv.setHorizontalScrollBarEnabled(false);
        beerlv.setVerticalFadingEdgeEnabled(false);
        beerlv.setHorizontalFadingEdgeEnabled(false);
        beerlv.setFadingEdgeLength(0);
        adapter = new BeerAdapter(getApplicationContext(),this);
        beerlv.setAdapter(adapter);
        agentname = (TextView) findViewById(R.id.agentname);
        agentname.setText("Агент: "+username);
        mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), appKey, appSecret);
        restaurantAuto = (AutoCompleteTextView) findViewById(R.id.restaurantNameAuto);
        restaurantAuto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        restaurantAuto.setAdapter(new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,restaurantList));
        listbutton = (ImageButton) findViewById(R.id.listbutton);
        listbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(restaurantAuto.getWindowToken(), 0);
                restaurantAuto.showDropDown();
            }
        });
        okbutton = (Button) findViewById(R.id.OKbutton);
        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restaurantAuto.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(),"Введите название заведения",Toast.LENGTH_SHORT).show();
                else {
                    if (checkList())
                    {
                        final Intent intent = new Intent(getApplicationContext(),
                                OrderActivity.class);

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("id", "");
                        startActivityForResult(intent, 1);
                        if (!restaurantList.contains(restaurantAuto.getText().toString())) {
                            restaurantList.add(restaurantAuto.getText().toString());
                            dbHelper.createRestaurant(restaurantAuto.getText().toString());
                            restaurantAuto.setAdapter(new ArrayAdapter(MainActivity.this,android.R.layout.simple_dropdown_item_1line,restaurantList));
                        }

                    }
                    else Toast.makeText(getApplicationContext(),"Ваш заказ пуст",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDbxAcctMgr.hasLinkedAccount()) {
            if (beerlist.size()==0)  doDropboxTest();
        } else {
            mDbxAcctMgr.startLink((Activity)this, REQUEST_LINK_TO_DBX);
        }


    }

    public void doDropboxTest() {
        beerlist =  new ArrayList<BeerItem>();
        try {
            final String TEST_FILE_NAME = "form.xls";
            DbxPath testPath = new DbxPath(DbxPath.ROOT, TEST_FILE_NAME);
            File excelfile = new File(Environment.getExternalStorageDirectory().toString()+"/excelfile.xls");

            // Create DbxFileSystem for synchronized file access.
            DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
            int flag=0;

            if (dbxFs.isFile(testPath)) {
               // String resultData;
                DbxFile testFile = dbxFs.open(testPath);
                FileInputStream instream = testFile.getReadStream();
                FileOutputStream stream = new FileOutputStream(excelfile);
                byte[] buffer = new byte[1024];
                int len = instream.read(buffer);
                while (len != -1) {
                    stream.write(buffer, 0, len);
                    len = instream.read(buffer);
                }
                stream.close();
                instream.close();
                testFile.close();

            } else if (dbxFs.isFolder(testPath)) {

            }
            ReadExcel readex = new ReadExcel();
            readex.setInputFile(excelfile);
            readex.read();
        } catch (IOException e) {
        }

    }

    private void exportToDropbox(){
        try {
            final String TEST_FILE_NAME = "order_"+username+"_"+restaurantAuto.getText().toString()+"_"+cal.get(Calendar.DAY_OF_MONTH)+"."+String.valueOf(cal.get(Calendar.MONTH)+1)+"."+cal.get(Calendar.YEAR)+".xls";
            final String DATE_DIR_NAME = "На"+"_"+cal.get(Calendar.DAY_OF_MONTH)+"."+String.valueOf(cal.get(Calendar.MONTH)+1)+"."+cal.get(Calendar.YEAR);
            DbxPath finalPath = new DbxPath(DbxPath.ROOT, username+"/"+DATE_DIR_NAME+"/"+TEST_FILE_NAME);
            File excelfile = new File(ORDER_PATH);

            int flag = 0;
            DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
            List<DbxFileInfo> infos = dbxFs.listFolder(DbxPath.ROOT);
            DbxPath fullPath = new DbxPath(DbxPath.ROOT,username);
            for (DbxFileInfo info : infos)
                if (info.path.toString().equals(username)) flag=1;
            if (flag==0) dbxFs.createFolder(fullPath);


            flag=0;
            infos = dbxFs.listFolder(fullPath);
            for (DbxFileInfo info : infos)
                if (info.path.toString().equals(DATE_DIR_NAME)) flag=1;

            if (flag==0)
            {
                DbxPath datePath = new DbxPath(DbxPath.ROOT,username+"/"+DATE_DIR_NAME);
                dbxFs.createFolder(datePath);
            }

            if (dbxFs.exists(finalPath)) dbxFs.delete(finalPath);
            DbxFile testFile = dbxFs.create(finalPath);

            FileInputStream instream = new FileInputStream(excelfile);
            FileOutputStream stream = testFile.getWriteStream();
            byte[] buffer = new byte[1024];
            int len = instream.read(buffer);
            while (len != -1)
            {
               stream.write(buffer, 0, len);
               len = instream.read(buffer);
            }
            stream.close();
            instream.close();
            testFile.close();


            Toast.makeText(getApplicationContext(),"Успех!",Toast.LENGTH_SHORT).show();
            doDropboxTest();
            restaurantAuto.setText("");
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE,1);
            datetv.setText(cal.get(Calendar.DAY_OF_MONTH)+"/"+String.valueOf(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR));
        } catch (IOException e) {
        }

    }

    public void DateDialog() {

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                datetv.setText(dayOfMonth + "/"
                        + String.valueOf(monthOfYear + 1) + "/" + year);
                cal.set(year,monthOfYear,dayOfMonth);
            }
        };

        DatePickerDialog dpDialog = new DatePickerDialog(this, listener, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dpDialog.show();

    }
    public boolean checkList(){
        int i=0;
        for (BeerItem item : beerlist)
            if (item.count>0) i++;
        if (i>0) return true;
                else return false;
    }
    public static String loadPrefs(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        username = prefs.getString("username","");
        return username;
    }

    public static void updatePrefs(Context context, String username) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("username",username);
        ed.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==1) {
                                     ORDER_PATH =Environment.getExternalStorageDirectory().toString()+ "/order_"+username+"_"+restaurantAuto.getText().toString()+"_"+cal.get(Calendar.DAY_OF_MONTH)+"."+String.valueOf(cal.get(Calendar.MONTH)+1)+"."+cal.get(Calendar.YEAR)+".xls";

                        new WriteExcel().exportToExcel(beerlist, restaurantAuto.getText().toString(), username,ORDER_PATH,cal);
                        exportToDropbox();
        }

    }



}
