package com.example.lasercolour;  // our app's package (unique name for this project)

import android.net.Uri;            // Uri holds the location of the picked image
import android.os.Bundle;          // Bundle is used to pass state data to activities
import android.util.Log;           // <-- Logcat
import android.view.View;          // View base class (for click handlers etc.)
import android.widget.ImageView;   // ImageView shows the background picture

import androidx.activity.result.ActivityResultLauncher;                    // new activity-result API (better than startActivityForResult)
import androidx.activity.result.contract.ActivityResultContracts;          // gives us built-in contracts like GetContent()
import androidx.appcompat.app.AlertDialog;                                 // pop-up dialog for color choices
import androidx.appcompat.app.AppCompatActivity;                           // base class for activities with ActionBar/AppCompat

import com.google.android.material.button.MaterialButton;                  // Material Design button widget

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LaserColour"; // tag to filter in Logcat

    private ImageView backgroundImage;  // where we display the imported picture
    private DrawingView drawingView;    // our custom view where the user draws on top

    // SAF image picker using an implicit intent (GetContent).
    // When user picks, Android calls onImagePicked(uri).
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onImagePicked);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                 // normal Android setup call
        Log.d(TAG, "onCreate: activity starting");
        setContentView(R.layout.activity_main);             // connect this Activity to activity_main.xml layout

        // find views from the layout so we can use them in code
        backgroundImage = findViewById(R.id.backgroundImage);  // the ImageView behind the canvas
        drawingView     = findViewById(R.id.drawingView);      // the drawing surface (custom view)
        Log.d(TAG, "onCreate: views bound (ImageView + DrawingView)");

        // buttons at the bottom: Upload, Colour, Clear
        MaterialButton btnUpload = findViewById(R.id.btnUpload);
        MaterialButton btnColor  = findViewById(R.id.btnColor);
        MaterialButton btnClear  = findViewById(R.id.btnClear);

        // UPLOAD button: open gallery/file picker (implicit intent under the hood)
        btnUpload.setOnClickListener(v -> {
            Log.d(TAG, "Upload tapped: launching image picker (GetContent image/*)");
            try {
                pickImageLauncher.launch("image/*");
                Log.i(TAG, "Image picker launched");
            } catch (Exception e) {
                Log.e(TAG, "Failed to launch image picker", e);
            }
        });

        // COLOUR button: open a simple dialog to choose brush color
        btnColor.setOnClickListener(this::showColorPicker);

        // CLEAR button: wipe the current strokes (background image stays)
        btnClear.setOnClickListener(v -> {
            Log.d(TAG, "Clear tapped");
            drawingView.clear();
            Log.i(TAG, "Canvas cleared (strokes removed, background kept)");
        });
    }

    @Override protected void onStart()  { super.onStart();  Log.d(TAG, "onStart"); }
    @Override protected void onResume() { super.onResume(); Log.d(TAG, "onResume"); }
    @Override protected void onPause()  { Log.d(TAG, "onPause");  super.onPause(); }
    @Override protected void onStop()   { Log.d(TAG, "onStop");   super.onStop(); }
    @Override protected void onDestroy(){ Log.d(TAG, "onDestroy");super.onDestroy(); }

    // Called automatically after the user chooses an image in the picker
    private void onImagePicked(Uri uri) {
        if (uri != null) {                              // make sure something was picked
            Log.i(TAG, "Image picked: " + uri);
            backgroundImage.setImageURI(uri);           // show the picture in the background ImageView
            // drawingView.clear();                     // optional: clear strokes on new image
        } else {
            Log.w(TAG, "Image pick cancelled or returned null URI");
        }
    }

    // Pop up a basic color picker using AlertDialog with a preset palette
    private void showColorPicker(View v) {
        Log.d(TAG, "Colour tapped: showing color picker dialog");

        // Simple ARGB color list for our brush (0xAARRGGBB). AA=alpha, RR=red, GG=green, BB=blue.
        final int[] colors = new int[] {
                0xFFFFFFFF, // white
                0xFFFF0000, // red
                0xFF00FF00, // green
                0xFF00FFFF, // cyan
                0xFF00A2FF, // light blue
                0xFFFF00FF, // magenta
                0xFFFFFF00, // yellow
                0xFF000000  // black
        };

        // Labels that match the colors above (same order)
        final CharSequence[] names = new CharSequence[] {
                "White","Red","Green","Cyan","Light Blue","Magenta","Yellow","Black"
        };

        // Build and show the dialog
        new AlertDialog.Builder(this)
                .setTitle("Pick Brush Color")                         // dialog title
                .setItems(names, (d, which) -> {                      // when user taps an item...
                    drawingView.setStrokeColor(colors[which]);        // ...set brush color on our DrawingView
                    Log.i(TAG, "Brush colour set to: " + names[which]);
                })
                .show();                                              // display the dialog
    }
}
