package com.example.calculator;  // This must match your app namespace

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Display TextView
    private TextView display;

    // Variables to hold operands and operator
    private double firstNumber = 0;
    private double secondNumber = 0;
    private String operator = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Link XML layout

        // Connect TextView in XML to this variable
        display = findViewById(R.id.display);

        // Number buttons
        int[] numberIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        // Assign click listener to all number buttons
        for (int id : numberIds) {
            Button btn = findViewById(id);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button b = (Button)v;
                    display.append(b.getText()); // Append number to display
                }
            });
        }

        // Operator buttons
        int[] operatorIds = {R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide};

        for (int id : operatorIds) {
            Button btn = findViewById(id);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button b = (Button)v;
                    firstNumber = Double.parseDouble(display.getText().toString()); // Save first number
                    operator = b.getText().toString(); // Save operator (+, -, *, /)
                    display.setText(""); // Clear display for second number
                }
            });
        }

        // Equals button
        Button equals = findViewById(R.id.btnEquals);
        equals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Parse second number
                secondNumber = Double.parseDouble(display.getText().toString());
                double result = 0;

                // Perform calculation based on operator
                switch(operator) {
                    case "+":
                        result = firstNumber + secondNumber;
                        break;
                    case "-":
                        result = firstNumber - secondNumber;
                        break;
                    case "*":
                        result = firstNumber * secondNumber;
                        break;
                    case "/":
                        if (secondNumber != 0)
                            result = firstNumber / secondNumber;
                        else
                            display.setText("Error"); // Handle divide by zero
                        return;
                }

                // Display result
                display.setText(String.valueOf(result));
            }
        });

        // Clear button
        Button clear = findViewById(R.id.btnClear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display.setText(""); // Clear display
                firstNumber = 0;
                secondNumber = 0;
                operator = "";
            }
        });
    }
}
