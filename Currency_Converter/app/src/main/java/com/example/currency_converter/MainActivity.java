package com.example.currency_converter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private EditText amountInput;
    private Spinner fromSpinner, toSpinner;
    private TextView resultText;
    private final HashMap<String, Double> rates = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved theme first
        if (ThemeStorage.loadTheme(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Rates relative to 1 USD
        rates.put("USD", 1.0);
        rates.put("INR", 93.20);   // was 83.0
        rates.put("JPY", 159.40);  // was 150.0
        rates.put("EUR", 0.8673);  // was 0.92

        amountInput = findViewById(R.id.amountInput);
        fromSpinner = findViewById(R.id.fromSpinner);
        toSpinner = findViewById(R.id.toSpinner);
        resultText = findViewById(R.id.resultText);
        Button convertButton = findViewById(R.id.convertButton);
        Button settingsButton = findViewById(R.id.settingsButton);

        String[] currencies = {"USD", "INR", "JPY", "EUR"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, currencies);
        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);

        convertButton.setOnClickListener(v -> performConversion());

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void performConversion() {
        String from = fromSpinner.getSelectedItem().toString();
        String to = toSpinner.getSelectedItem().toString();
        String input = amountInput.getText().toString();

        if (input.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(input);
        double inUSD = amount / rates.get(from);
        double finalAmount = inUSD * rates.get(to);

        resultText.setText(String.format("Result: %.2f %s", finalAmount, to));
    }
}