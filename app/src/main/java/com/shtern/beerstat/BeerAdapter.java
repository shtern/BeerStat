package com.shtern.beerstat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Алексей on 04.01.2015.
 */
public class BeerAdapter extends BaseAdapter {
    private Context mContext;
    private Activity mActivity;
    public BeerAdapter(Context context,Activity parentActivity) {
        mContext = context;
        mActivity = parentActivity;
        //mList = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        //return TaskManager.getNotActAlarmCount();
        return MainActivity.beerlist.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        //return TaskManager.taskList.get(position);
        return MainActivity.beerlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup parent) {
        final BeerItem entry = MainActivity.beerlist.get(pos);

            // inflating list view layout if null
            //if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.beeritem, null);
            //}
        RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.beeritem_layout);
        for (int i=0; i<MainActivity.colorMatchList.size();i++)
        {
            if (MainActivity.colorMatchList.get(i)!=null)
                if (MainActivity.colorMatchList.get(i).colorstring!=null && MainActivity.colorMatchList.get(i).beername!=null)
            if (entry.name.contains(MainActivity.colorMatchList.get(i).beername.toString()))
            {
                Log.d("MATCH",MainActivity.colorMatchList.get(i).beername +" in "+entry.name);
                relativeLayout.setBackgroundColor(Color.parseColor(MainActivity.colorMatchList.get(i).colorstring));
                break;
            }
        }
        TextView name = (TextView) convertView.findViewById(R.id.beername);
        //name.setText(entry.getName());
        name.setText(String.valueOf(entry.name));
        name.setTextColor(Color.BLACK);
        final TextView number = (TextView) convertView.findViewById(R.id.beernum);
        number.setText(String.valueOf(entry.number));
        number.setTextColor(Color.BLACK);
        TextView liter = (TextView) convertView.findViewById(R.id.beerliter);
        liter.setText(String.valueOf(entry.liters));
        liter.setTextColor(Color.BLACK);
        final EditText number_et = (EditText) convertView.findViewById(R.id.beercolnumber);
        number_et.setText(String.valueOf(entry.count));


        number_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               LayoutInflater inflater = LayoutInflater.from(mContext);
               final RelativeLayout npView = (RelativeLayout) inflater.inflate(R.layout.number_picker_dialog_layout, null);
                //final NumberPicker np = (NumberPicker) npView.findViewById(R.id.picker);
                final EditText numberet =(EditText) npView.findViewById(R.id.numberet);
//                np.setMinValue(0);
//                np.setMaxValue(100);
//                np.setValue(Integer.parseInt(number_et.getText().toString()));
                numberet.setText(number_et.getText().toString());
                numberet.selectAll();
               final AlertDialog coldialog  = new AlertDialog.Builder(mActivity)
                        .setTitle("Выберите количество:")
                        .setView(npView)
                        .setPositiveButton("OK",null)
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                        dialog.dismiss();
                                    }
                                })
                        .create();

                coldialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = coldialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // TODO Do something
                                        if (numberet.getText().toString().isEmpty())
                                            Toast.makeText(mContext, "Введите число", Toast.LENGTH_SHORT).show();
                                        else {
                                            entry.count = Integer.parseInt(numberet.getText().toString());
                                            notifyDataSetChanged();
                                            coldialog.dismiss();
                                        }
                            }
                        });
                    }
                });
                coldialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                coldialog.show();
                numberet.requestFocus();
            }
        });

        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

}