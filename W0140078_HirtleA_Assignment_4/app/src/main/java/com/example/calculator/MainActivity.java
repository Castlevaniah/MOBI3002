package com.example.calculator;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText inputField;
    private String currentInput = "";
    private double firstNumber = 0;
    private String operator = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputField = findViewById(R.id.inputField);

        // Restore state if activity restarted
        if (savedInstanceState != null) {
            currentInput = savedInstanceState.getString("currentInput", "");
            firstNumber = savedInstanceState.getDouble("firstNumber", 0);
            operator = savedInstanceState.getString("operator", "");
            inputField.setText(savedInstanceState.getString("displayText", ""));
        }

        // Number buttons
        int[] numberButtons = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        for (int id : numberButtons) {
            Button btn = findViewById(id);
            btn.setOnClickListener(view -> {
                Button b = (Button) view;
                currentInput += b.getText().toString();
                inputField.setText(currentInput);

                Log.d("CalculatorApp", "Number pressed: " + b.getText().toString() +
                        " | Current input: " + currentInput);
            });
        }

        // Operator buttons
        int[] operatorButtons = {
                R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide
        };

        for (int id : operatorButtons) {
            Button btn = findViewById(id);
            btn.setOnClickListener(view -> {
                if (!inputField.getText().toString().isEmpty()) {
                    currentInput = inputField.getText().toString();
                }
                if (!currentInput.isEmpty()) {
                    firstNumber = Double.parseDouble(currentInput);
                }
                operator = ((Button) view).getText().toString();
                currentInput = "";
                inputField.setText("");

                Log.d("CalculatorApp", "Operator selected: " + operator +
                        " | First number: " + firstNumber);
            });
        }

        // Equals button
        Button btnEqual = findViewById(R.id.btnEqual);
        btnEqual.setOnClickListener(view -> {
            if (!inputField.getText().toString().isEmpty()) {
                currentInput = inputField.getText().toString();
            }
            if (currentInput.isEmpty()) return;

            double secondNumber = Double.parseDouble(currentInput);
            double result = 0;

            switch (operator) {
                case "+":
                    result = firstNumber + secondNumber;
                    break;
                case "−":
                    result = firstNumber - secondNumber;
                    break;
                case "×":
                    result = firstNumber * secondNumber;
                    break;
                case "÷":
                    if (secondNumber != 0) {
                        result = firstNumber / secondNumber;
                    } else {
                        inputField.setText("Error");
                        Log.e("CalculatorApp", "Division by zero attempted!");
                        return;
                    }
                    break;
            }

            inputField.setText(String.valueOf(result));
            currentInput = String.valueOf(result);

            Log.i("CalculatorApp", "Calculation: " +
                    firstNumber + " " + operator + " " + secondNumber + " = " + result);
        });

        // Clear button
        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(view -> {
            currentInput = "";
            firstNumber = 0;
            operator = "";
            inputField.setText("");

            Log.w("CalculatorApp", "Calculator cleared");
        });
    }

    // Save state for rotation / restart
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentInput", currentInput);
        outState.putDouble("firstNumber", firstNumber);
        outState.putString("operator", operator);
        outState.putString("displayText", inputField.getText().toString());
    }
}
