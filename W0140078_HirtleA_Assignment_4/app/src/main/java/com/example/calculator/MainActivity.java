package com.example.calculator;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

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

        // Number buttons (these are still <Button> with text 0..9)
        int[] numberButtons = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        View.OnClickListener numberClick = view -> {
            Button b = (Button) view; // number buttons are Buttons with text
            currentInput += b.getText().toString();
            inputField.setText(currentInput);

            Log.d("CalculatorApp", "Number pressed: " + b.getText().toString() +
                    " | Current input: " + currentInput);
        };

        for (int id : numberButtons) {
            View v = findViewById(id);
            if (v != null) v.setOnClickListener(numberClick);
        }

        // Operator buttons (now ImageButtons with icons — no text to read!)
        int[] operatorButtons = { R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide };

        // Map view id -> operator symbol used in the switch
        Map<Integer, String> opMap = new HashMap<>();
        opMap.put(R.id.btnPlus, "+");
        opMap.put(R.id.btnMinus, "−");     // U+2212
        opMap.put(R.id.btnMultiply, "×");  // U+00D7
        opMap.put(R.id.btnDivide, "÷");    // U+00F7

        View.OnClickListener opClick = view -> {
            // If user typed something, prefer that; else use currentInput
            String text = inputField.getText() != null ? inputField.getText().toString() : "";
            if (!text.isEmpty()) currentInput = text;

            if (!currentInput.isEmpty()) {
                try {
                    firstNumber = Double.parseDouble(currentInput);
                } catch (NumberFormatException e) {
                    Log.e("CalculatorApp", "Invalid first number: " + currentInput, e);
                    inputField.setText("Error");
                    currentInput = "";
                    operator = "";
                    return;
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                operator = opMap.getOrDefault(view.getId(), "");
            }
            currentInput = "";
            inputField.setText("");

            Log.d("CalculatorApp", "Operator selected: " + operator +
                    " | First number: " + firstNumber);
        };

        for (int id : operatorButtons) {
            View v = findViewById(id);
            if (v != null) v.setOnClickListener(opClick);
        }

        // Equals button
        Button btnEqual = findViewById(R.id.btnEqual);
        btnEqual.setOnClickListener(view -> {
            String text = inputField.getText() != null ? inputField.getText().toString() : "";
            if (!text.isEmpty()) currentInput = text;
            if (currentInput.isEmpty() || operator.isEmpty()) return;

            double secondNumber;
            try {
                secondNumber = Double.parseDouble(currentInput);
            } catch (NumberFormatException e) {
                Log.e("CalculatorApp", "Invalid second number: " + currentInput, e);
                inputField.setText("Error");
                return;
            }

            double result;
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
                default:
                    // No operator selected
                    return;
            }

            inputField.setText(String.valueOf(result));
            currentInput = String.valueOf(result);
            operator = ""; // reset so the next number starts fresh

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
        outState.putString("displayText", inputField.getText() != null ? inputField.getText().toString() : "");
    }
}
