package com.example.calculator;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView display;
    private String currentInput = "";
    private double firstNumber = 0;
    private String operator = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);

        // Number buttons
        int[] numberButtons = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};

        for (int id : numberButtons) {
            Button btn = findViewById(id);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button b = (Button) view;
                    currentInput += b.getText().toString();
                    display.setText(currentInput);

                    // Log number pressed
                    Log.d("CalculatorApp", "Number pressed: " + b.getText().toString() +
                            " | Current input: " + currentInput);
                }
            });
        }

        // Operator buttons
        int[] operatorButtons = {R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide};
        for (int id : operatorButtons) {
            Button btn = findViewById(id);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firstNumber = Double.parseDouble(currentInput);
                    operator = ((Button) view).getText().toString();
                    currentInput = "";
                    display.setText("");

                    // Log operator pressed
                    Log.d("CalculatorApp", "Operator selected: " + operator +
                            " | First number: " + firstNumber);
                }
            });
        }

        // Equals button
        Button btnEquals = findViewById(R.id.btnEquals);
        btnEquals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double secondNumber = Double.parseDouble(currentInput);
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
                            Log.e("CalculatorApp", "Division by zero attempted!");
                            return;
                        }
                        break;
                }

                display.setText(String.valueOf(result));
                currentInput = String.valueOf(result);

                // Log calculation
                Log.i("CalculatorApp", "Calculation performed: " +
                        firstNumber + " " + operator + " " + secondNumber + " = " + result);
            }
        });

        // Clear button
        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentInput = "";
                firstNumber = 0;
                operator = "";
                display.setText("");

                // Log clear action
                Log.w("CalculatorApp", "Calculator cleared");
            }
        });
    }
}
