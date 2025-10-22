package com.example.m03_bounce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

// Activity = panel/UI + DB + custom View + Gravity sensor
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity"; // simple Logcat tag

    // view + db
    private BouncingBallView bbView;
    private BallsDbHelper db;

    // panel fields
    private EditText etName, etX, etY, etDx, etDy;
    private Spinner spColor;
    private int[] colorInts;

    // sensors
    private SensorManager sensorManager;
    private Sensor gravitySensor;    // preferred
    private Sensor accelSensor;      // fallback

    // simple throttled sensor log
    private long lastSensorLogNs = 0;
    private static final long SENSOR_LOG_INTERVAL_NS = 250_000_000L; // 0.25s

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- basic startup log ---
        Log.d(TAG, "onCreate: app starting (pid=" + android.os.Process.myPid() + ")");

        // hook up view + db
        bbView = findViewById(R.id.custView);
        db = new BallsDbHelper(this);

        // hook up panel inputs
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

        // buttons (log clicks)
        findViewById(R.id.btnAdd).setOnClickListener(v -> {
            Log.d(TAG, "btnAdd: clicked");
            addBallFromUi();
        });
        findViewById(R.id.btnClear).setOnClickListener(v -> {
            Log.d(TAG, "btnClear: clicked");
            clearAll();
        });

        // load any saved balls (log each load)
        for (BallsDbHelper.StoredBall sb : db.getAllBalls()) {
            Log.d(TAG, "loadFromDb: name=" + sb.name + " x=" + sb.x + " y=" + sb.y + " dx=" + sb.dx + " dy=" + sb.dy + " color=" + sb.color);
            bbView.addBall(new Ball(sb.name, sb.color, sb.x, sb.y, sb.dx, sb.dy));
        }

        // sensor setup + availability log
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        accelSensor   = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Log.d(TAG, "sensors: gravity=" + (gravitySensor != null) + " accel=" + (accelSensor != null));
    }

    // read form -> insert into DB -> add to view (log exactly what was added)
    private void addBallFromUi() {
        String name = textOr(etName);
        float x  = floatOr(etX,  200f);
        float y  = floatOr(etY,  200f);
        float dx = floatOr(etDx,   4f);
        float dy = floatOr(etDy,   3f);
        int color = colorInts[spColor.getSelectedItemPosition()];

        Log.d(TAG, "addBall: name=" + name + " x=" + x + " y=" + y + " dx=" + dx + " dy=" + dy + " color=" + color);

        db.insertBall(name, x, y, dx, dy, color);
        bbView.addBall(new Ball(name, color, x, y, dx, dy));
    }

    // clear DB + view (log rows deleted)
    private void clearAll() {
        int rows = db.deleteAll();
        Log.d(TAG, "clearAll: dbRowsDeleted=" + rows);
        bbView.clearAll();
    }

    // safe helpers
    private static String textOr(EditText e) {
        String s = e.getText() == null ? "" : e.getText().toString().trim();
        return s.isEmpty() ? "Ball" : s;
    }
    private static float floatOr(EditText e, float def) {
        try { return Float.parseFloat(e.getText().toString().trim()); }
        catch (Exception ex) { return def; }
    }

    // register sensor + log which one
    @Override
    protected void onResume() {
        super.onResume();
        if (gravitySensor != null) {
            sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_GAME);
            Log.d(TAG, "onResume: registered GRAVITY");
        } else if (accelSensor != null) {
            sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_GAME);
            Log.d(TAG, "onResume: registered ACCELEROMETER (fallback)");
        } else {
            Log.w(TAG, "onResume: no gravity/accelerometer available");
        }
    }

    // unregister + log
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        Log.d(TAG, "onPause: unregistered sensors");
    }

    // gravity values -> view; also log them (throttled)
    @Override
    public void onSensorChanged(SensorEvent event) {
        float gx, gy;
        gx = event.values[0];
        gy = event.values[1];

        // throttle logs to ~4 Hz so Logcat stays readable
        long now = System.nanoTime();
        if (now - lastSensorLogNs >= SENSOR_LOG_INTERVAL_NS) {
            lastSensorLogNs = now;
            Log.d(TAG, "sensor: gx=" + gx + " gy=" + gy + " type=" + event.sensor.getType());
        }

        bbView.setGravity(-gx, gy); // flip X so tilt feels natural
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) { /* not used */ }
}
