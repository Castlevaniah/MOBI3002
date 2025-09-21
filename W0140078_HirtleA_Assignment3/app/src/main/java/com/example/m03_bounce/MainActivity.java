package com.example.m03_bounce;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

// Found tutorial to do put buttons over view here:
// https://code.tutsplus.com/tutorials/android-sdk-creating-custom-views--mobile-14548

public class MainActivity extends AppCompatActivity {


    // bbView is our bouncing ball view
    private BouncingBallView bbView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the view object so we can reference it later
        bbView = (BouncingBallView) findViewById(R.id.custView);
    }

    // button action

    public void onRussButtonClick(View v) {

        Log.d("MainActivity  BUTTON", "User tapped the  button ... MAIN");

        // let the view do something
        bbView.RussButtonPressed();

    }
}