package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView display;
    private double firstNumber = 0;
    private double secondNumber = 0;
    private String operator = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);

        // Number buttons
        int[] numberIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        for (int id : numberIds) {
            Button btn = findViewById(id);
            btn.setOnClickListener(v -> {
                display.append(((Button) v).getText());
            });
        }

        // Operator buttons
        int[] operatorIds = {
                R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide
        };

        for (int id : operatorIds) {
            Button btn = findViewById(id);
            btn.setOnClickListener(v -> {
                if (!display.getText().toString().isEmpty()) {
                    firstNumber = Double.parseDouble(display.getText().toString());
                    operator = ((Button) v).getText().toString();
                    display.setText("");
                }
            });
        }

        // Equals button
        Button equals = findViewById(R.id.btnEquals);
        equals.setOnClickListener(v -> {
            if (!display.getText().toString().isEmpty() && !operator.isEmpty()) {
                secondNumber = Double.parseDouble(display.getText().toString());
                double result = 0;

                switch (operator) {
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
                        if (secondNumber != 0) {
                            result = firstNumber / secondNumber;
                        } else {
                            display.setText("Error");
                            return;
                        }
                        break;
                }

                display.setText(String.valueOf(result));
                firstNumber = result; // allow chaining calculations
                operator = "";
            }
        });

        // Clear button
        Button clear = findViewById(R.id.btnClear);
        clear.setOnClickListener(v -> {
            display.setText("");
            firstNumber = 0;
            secondNumber = 0;
            operator = "";
        });
    }
}
