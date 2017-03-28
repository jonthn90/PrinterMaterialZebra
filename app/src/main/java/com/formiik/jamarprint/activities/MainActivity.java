package com.formiik.jamarprint.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.formiik.jamarprint.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FloatingActionButton searchFAB;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchFAB = (FloatingActionButton) findViewById(R.id.fab_search);

        searchFAB.setOnClickListener(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar_m);
        toolbar.setTitle("JAMAR");
        setSupportActionBar(toolbar);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.fab_search:

                searchPrinter();

                break;
        }
    }

    private void searchPrinter(){
        Intent intent = new Intent(getApplicationContext(), ActivitySearchPrinter.class);
        startActivity(intent);
    }
}
