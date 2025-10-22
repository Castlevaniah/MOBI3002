package com.example.m03_bounce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

// Activity = glue between the panel (UI), DB, custom View, and the Gravity sensor
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // references to my custom view + DB
    private BouncingBallView bbView;
    private BallsDbHelper db;

    // inputs from the panel
    private EditText etName, etX, etY, etDx, etDy;
    private Spinner spColor;
    private int[] colorInts;

    // sensors
    private SensorManager sensorManager;
    private Sensor gravitySensor;    // best
    private Sensor accelSensor;      // fallback

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // show the layout (canvas + control panel)
        setContentView(R.layout.activity_main);

        // custom view reference
        bbView = findViewById(R.id.custView);

        // DB helper
        db = new BallsDbHelper(this);

        // hook up panel fields
        etName = findViewById(R.id.etName);
        etX    = findViewById(R.id.etX);
        etY    = findViewById(R.id.etY);
        etDx   = findViewById(R.id.etDx);
        etDy   = findViewById(R.id.etDy);
        spColor= findViewById(R.id.spColor);

        // color spinner
        String[] colorNames = {"Red","Green","Black","Cyan","Magenta","Yellow","White"};
        colorInts = new int[]{ Color.RED, Color.GREEN, Color.BLACK, Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.WHITE };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colorNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spColor.setAdapter(adapter);

        // buttons
        findViewById(R.id.btnAdd).setOnClickListener(v -> addBallFromUi());
        findViewById(R.id.btnClear).setOnClickListener(v -> clearAll());

        // load persisted balls
        for (BallsDbHelper.StoredBall sb : db.getAllBalls()) {
            bbView.addBall(new Ball(sb.name, sb.color, sb.x, sb.y, sb.dx, sb.dy));
        }

        // sensor setup
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        accelSensor   = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    // read form -> insert into DB -> add to view
    private void addBallFromUi() {
        String name = textOr(etName, "Ball"); // safe text
        float x  = floatOr(etX,  200f);       // safe numbers
        float y  = floatOr(etY,  200f);
        float dx = floatOr(etDx,   4f);
        float dy = floatOr(etDy,   3f);
        int color = colorInts[spColor.getSelectedItemPosition()];

        db.insertBall(name, x, y, dx, dy, color);             // save one row
        bbView.addBall(new Ball(name, color, x, y, dx, dy));  // draw it
    }

    // wipe DB + screen
    private void clearAll() {
        db.deleteAll();
        bbView.clearAll();
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

    // lifecycle: register sensor listener when visible
    @Override
    protected void onResume() {
        super.onResume();
        if (gravitySensor != null) {
            sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_GAME);
        } else if (accelSensor != null) {
            sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    // lifecycle: unregister when backgrounded
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    // sensor callback: send gravity to the view
    @Override
    public void onSensorChanged(SensorEvent event) {
        float gx, gy;
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            gx = event.values[0];  // m/s^2
            gy = event.values[1];
        } else { // accelerometer fallback
            gx = event.values[0];
            gy = event.values[1];
        }
        bbView.setGravity(-gx, gy); // flip X so tilt feels natural
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) { /* not used */ }
}
