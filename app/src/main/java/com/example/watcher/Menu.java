package com.example.watcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Menu extends AppCompatActivity {

    private CardView cv_supervisiones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_menu );

        cv_supervisiones = findViewById(R.id.cv_supervisiones);

        cv_supervisiones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Menu.this, Supervisiones.class);
                startActivity(intent);
            }
        });



    }
}