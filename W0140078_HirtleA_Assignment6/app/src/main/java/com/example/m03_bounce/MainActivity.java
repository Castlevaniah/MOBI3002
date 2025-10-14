package com.example.m03_bounce;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

// Activity = glue between the panel (UI), DB, and the custom View.
public class MainActivity extends AppCompatActivity {

    private BouncingBallView bbView;
    private BallsDbHelper db;

    // inputs from the panel
    private EditText etName, etX, etY, etDx, etDy;
    private Spinner spColor;
    private int[] colorInts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // custom view reference
        bbView = findViewById(R.id.custView);

        // set up DB
        db = new BallsDbHelper(this);

        // grab UI widgets
        etName = findViewById(R.id.etName);
        etX = findViewById(R.id.etX);
        etY = findViewById(R.id.etY);
        etDx = findViewById(R.id.etDx);
        etDy = findViewById(R.id.etDy);
        spColor = findViewById(R.id.spColor);

        // color choices for the spinner
        String[] colorNames = {"Red","Green","Black","Cyan","Magenta","Yellow","White"};
        colorInts = new int[]{ Color.RED, Color.GREEN, Color.BLACK, Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.WHITE };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colorNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spColor.setAdapter(adapter);

        // button actions
        findViewById(R.id.btnAdd).setOnClickListener(v -> addBallFromUi());
        findViewById(R.id.btnClear).setOnClickListener(v -> clearAll());

        // Persistence
        for (BallsDbHelper.StoredBall sb : db.getAllBalls()) {
            Log.d("MainActivity", "LoadFromDB: " + sb.name + " ("+sb.x+","+sb.y+") dx="+sb.dx+" dy="+sb.dy+" color="+sb.color);
            bbView.addBall(new Ball(sb.name, sb.color, sb.x, sb.y, sb.dx, sb.dy));
        }
    }

    // read form -> insert into DB -> add to view
    private void addBallFromUi() {
        String name = textOr(etName, "Ball");
        float x = floatOr(etX, 200f);
        float y = floatOr(etY, 200f);
        float dx = floatOr(etDx, 4f);
        float dy = floatOr(etDy, 3f);
        int color = colorInts[spColor.getSelectedItemPosition()];

        db.insertBall(name, x, y, dx, dy, color);
        bbView.addBall(new Ball(name, color, x, y, dx, dy));
        Log.d("MainActivity", "AddBall: " + name + " x=" + x + " y=" + y + " dx=" + dx + " dy=" + dy + " color=" + color);
    }

    // wipe DB + screen
    private void clearAll() {
        db.deleteAll();
        bbView.clearAll();
        Log.d("MainActivity", "ClearAll: DB and view cleared");
    }

    // helpers to avoid crashes on blank input
    private static String textOr(EditText e, String def) {
        String s = e.getText() == null ? "" : e.getText().toString().trim();
        return s.isEmpty() ? def : s;
    }
    private static float floatOr(EditText e, float def) {
        try { return Float.parseFloat(e.getText().toString().trim()); }
        catch (Exception ex) { return def; }
    }


}
