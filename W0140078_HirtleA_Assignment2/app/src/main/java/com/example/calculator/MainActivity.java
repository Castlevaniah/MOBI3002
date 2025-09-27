package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText editText_Num1, editText_Num2, editText_Answer;
    Button b_Add, b_Subtract, b_Multiply, b_Divide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link UI elements
        editText_Num1 = findViewById(R.id.editText_Num1);
        editText_Num2 = findViewById(R.id.editText_Num2);
        editText_Answer = findViewById(R.id.editText_Answer);
        editText_Answer.setKeyListener(null); // READ ONLY field

        b_Add = findViewById(R.id.b_Add);
        b_Subtract = findViewById(R.id.b_Subtract);
        b_Multiply = findViewById(R.id.b_Multiply);
        b_Divide = findViewById(R.id.b_Divide);

        // Event listeners
        b_Add.setOnClickListener(v -> calculate("+"));
        b_Subtract.setOnClickListener(v -> calculate("-"));
        b_Multiply.setOnClickListener(v -> calculate("*"));
        b_Divide.setOnClickListener(v -> calculate("/"));
    }

    private void calculate(String operator) {
        try {
            String num1Text = editText_Num1.getText().toString();
            String num2Text = editText_Num2.getText().toString();

            if (num1Text.isEmpty() || num2Text.isEmpty()) {
                editText_Answer.setText("✦ ERROR: Missing Input ✦");
                return;
            }

            double d1 = Double.parseDouble(num1Text);
            double d2 = Double.parseDouble(num2Text);
            double answer = 0.0;

            switch (operator) {
                case "+": answer = d1 + d2; break;
                case "-": answer = d1 - d2; break;
                case "*": answer = d1 * d2; break;
                case "/":
                    if (d2 == 0) {
                        editText_Answer.setText("✦ DIVIDE BY ZERO ✦");
                        Toast.makeText(this, "⚡ Retro Error: Division by Zero!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    answer = d1 / d2;
                    break;
            }

            editText_Answer.setText("➤ " + answer);

            // Retro-style Logcat output
            Log.d("NEON_CALC_80s", "⚡ Operator: " + operator + " | Result: " + answer + " ⚡");

        } catch (Exception e) {
            editText_Answer.setText("✦ SYSTEM ERROR ✦");
            Log.e("NEON_CALC_80s", "Glitch in the Matrix: " + e.getMessage());
        }
    }
}
