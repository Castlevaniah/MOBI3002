package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    EditText editText_Num1, editText_Num2, editText_Answer;
    Button b_Add, b_Subtract, b_Multiply, b_Divide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link to IDs
        editText_Num1 = findViewById(R.id.editText_Num1);
        editText_Num2 = findViewById(R.id.editText_Num2);
        editText_Answer = findViewById(R.id.editText_Answer);
        editText_Answer.setKeyListener(null); // READ ONLY


        b_Add = findViewById(R.id.b_Add);
        b_Subtract = findViewById(R.id.b_Subtract);
        b_Multiply = findViewById(R.id.b_Multiply);
        b_Divide = findViewById(R.id.b_Divide);

        b_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate("+");
            }

        });

        b_Subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate("-");
            }
        });

        b_Multiply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate("*");
            }
        });

        b_Divide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate("/");
            }
        });
    }

    private void calculate(String operator) {
        double d1 = Double.parseDouble(editText_Num1.getText().toString());
        double d2 = Double.parseDouble(editText_Num2.getText().toString());
        double answer = 0.0;

        switch (operator) {
            case "+": answer = d1 + d2; break;
            case "-": answer = d1 - d2; break;
            case "*": answer = d1 * d2; break;
            case "/": answer = d1 / d2; break;
        }

        editText_Answer.setText(String.valueOf(answer));

        Log.d("Assignment 2 Part 2", "Logcat Message!");
    }
}
