package com.example.dbballintegrationtest;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

// This is a "launcher" activity used only to test the DB, then open MainActivity
public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1) Run the DB logging test on two different DB files
        DbLoggingHarness.run(this, "balls_test_A.db");
        DbLoggingHarness.run(this, "balls_test_B.db");

        // Commented out for DB Only Mode
      /*  // 2) After the proof logs are done, start the real app screen (MainActivity)
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
      */
        // 3) Close this activity so the Back button won't return here
        finish();
    }
}
