package com.shtern.beerstat;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class UsernameActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);
        TextView bere = (TextView) findViewById(R.id.bere);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/mfwb.ttf");
        bere.setTypeface(type);
        final EditText agent_ed = (EditText) findViewById(R.id.agentText);
        Button savebutton = (Button) findViewById(R.id.savebutton);
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!agent_ed.getText().toString().isEmpty()) {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    MainActivity.updatePrefs(getApplicationContext(), agent_ed.getText().toString());
                    UsernameActivity.this.finish();
                }else
                    Toast.makeText(getApplicationContext(),"Введите нормальное имя агента, блин!", Toast.LENGTH_LONG).show();
            }
        });
    }


}
