package com.example.watcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate( savedInstanceState );

        Intent intent = new Intent( this, Login.class );
        startActivity( intent );
        finish();


    }
}