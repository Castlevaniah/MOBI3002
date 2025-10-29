package com.example.lasercolour;  // our app's package (unique name for this project)

import android.net.Uri;            // Uri holds the location of the picked image
import android.os.Bundle;          // Bundle is used to pass state data to activities
import android.view.View;          // View base class (for click handlers etc.)
import android.widget.ImageView;   // ImageView shows the background picture

import androidx.activity.result.ActivityResultLauncher;                    // new activity-result API (better than startActivityForResult)
import androidx.activity.result.contract.ActivityResultContracts;          // gives us built-in contracts like GetContent()
import androidx.appcompat.app.AlertDialog;                                 // pop-up dialog for color choices
import androidx.appcompat.app.AppCompatActivity;                           // base class for activities with ActionBar/AppCompat

import com.google.android.material.button.MaterialButton;                  // Material Design button widget

public class MainActivity extends AppCompatActivity {

    private ImageView backgroundImage;  // where we display the imported picture
    private DrawingView drawingView;    // our custom view where the user draws on top

    // SAF (Storage Access Framework) image picker using an implicit intent.
    // ActivityResultLauncher<String> lets us launch a "get content" picker and receive a result.
    // We tell it we want content of type "image/*" later when we call launch().
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onImagePicked);
    // ^ when the user picks an image, Android calls onImagePicked(uri)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                 // normal Android setup call
        setContentView(R.layout.activity_main);             // connect this Activity to activity_main.xml layout

        // find views from the layout so we can use them in code
        backgroundImage = findViewById(R.id.backgroundImage);  // the ImageView behind the canvas
        drawingView     = findViewById(R.id.drawingView);      // the drawing surface (custom view)

        // buttons at the bottom: Upload, Colour, Clear
        MaterialButton btnUpload = findViewById(R.id.btnUpload);
        MaterialButton btnColor  = findViewById(R.id.btnColor);
        MaterialButton btnClear  = findViewById(R.id.btnClear);

        // UPLOAD button: open gallery/file picker (this uses an implicit intent under the hood)
        btnUpload.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        // ^ "image/*" means accept any image type (png, jpg, etc.). No storage permission needed with SAF.

        // COLOUR button: open a simple dialog to choose brush color
        btnColor.setOnClickListener(this::showColorPicker);

        // CLEAR button: wipe the current strokes (but we leave the background image alone)
        btnClear.setOnClickListener(v -> drawingView.clear());
    }

    // Called automatically after the user chooses an image in the picker
    private void onImagePicked(Uri uri) {
        if (uri != null) {                              // make sure something was picked
            backgroundImage.setImageURI(uri);           // show the picture in the background ImageView
            // Optional: drawingView.clear();           // uncomment if you want to clear strokes on new image
        }
    }

    // Pop up a basic color picker using AlertDialog with a preset palette
    private void showColorPicker(View v) {
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
                .setItems(names, (d, which) ->                        // when user taps an item...
                        drawingView.setStrokeColor(colors[which]))     // ...set brush color on our DrawingView
                .show();                                               // display the dialog
    }
}
