package com.tanish.currencyconverter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "CurrencyConverterPrefs";

    // Exchange rates relative to USD (base currency)
    // 1 USD = X currency
    private static final Map<String, Double> EXCHANGE_RATES = new HashMap<String, Double>() {{
        put("USD", 1.0);
        put("INR", 83.50);
        put("JPY", 149.50);
        put("EUR", 0.92);
    }};

    private static final Map<String, String> CURRENCY_SYMBOLS = new HashMap<String, String>() {{
        put("USD", "$");
        put("INR", "₹");
        put("JPY", "¥");
        put("EUR", "€");
    }};

    private static final Map<String, String> CURRENCY_NAMES = new HashMap<String, String>() {{
        put("USD", "US Dollar");
        put("INR", "Indian Rupee");
        put("JPY", "Japanese Yen");
        put("EUR", "Euro");
    }};

    private static final String[] CURRENCIES = {"USD", "INR", "JPY", "EUR"};

    private EditText editAmount;
    private Spinner spinnerFromCurrency;
    private Spinner spinnerToCurrency;
    private TextView textResult;
    private TextView textExchangeRate;
    private TextView textCurrencySymbol;
    private ImageButton buttonSwap;
    private ImageButton buttonSettings;

    private ArrayAdapter<String> currencyAdapter;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved theme before setting content view
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SettingsActivity.applySavedTheme(prefs);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupAdapters();
        setupListeners();

        // Set default selections
        spinnerFromCurrency.setSelection(0); // USD
        spinnerToCurrency.setSelection(1); // INR

        // Format for displaying results
        decimalFormat = new DecimalFormat("#,##0.00");
    }

    private void initViews() {
        editAmount = findViewById(R.id.edit_amount);
        spinnerFromCurrency = findViewById(R.id.spinner_from_currency);
        spinnerToCurrency = findViewById(R.id.spinner_to_currency);
        textResult = findViewById(R.id.text_result);
        textExchangeRate = findViewById(R.id.text_exchange_rate);
        textCurrencySymbol = findViewById(R.id.text_currency_symbol);
        buttonSwap = findViewById(R.id.button_swap);
        buttonSettings = findViewById(R.id.button_settings);
    }

    private void setupAdapters() {
        String[] currencyItems = new String[CURRENCIES.length];
        for (int i = 0; i < CURRENCIES.length; i++) {
            currencyItems[i] = CURRENCIES[i] + " - " + CURRENCY_NAMES.get(CURRENCIES[i]);
        }

        currencyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, currencyItems);
        currencyAdapter.setDropDownViewResource(R.layout.spinner_item);

        spinnerFromCurrency.setAdapter(currencyAdapter);
        spinnerToCurrency.setAdapter(currencyAdapter);
    }

    private void setupListeners() {
        // Amount input listener for real-time conversion
        editAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                performConversion();
            }
        });

        // Currency selection listeners
        spinnerFromCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCurrencySymbol();
                performConversion();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerToCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                performConversion();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Swap button
        buttonSwap.setOnClickListener(v -> swapCurrencies());

        // Settings button
        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void updateCurrencySymbol() {
        String fromCurrency = CURRENCIES[spinnerFromCurrency.getSelectedItemPosition()];
        String symbol = CURRENCY_SYMBOLS.get(fromCurrency);
        textCurrencySymbol.setText(symbol);
    }

    private void performConversion() {
        String amountStr = editAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            textResult.setText("0.00");
            textExchangeRate.setText("");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            String fromCurrency = CURRENCIES[spinnerFromCurrency.getSelectedItemPosition()];
            String toCurrency = CURRENCIES[spinnerToCurrency.getSelectedItemPosition()];

            double result = convert(amount, fromCurrency, toCurrency);
            textResult.setText(decimalFormat.format(result));

            // Update exchange rate display
            double rate = getExchangeRate(fromCurrency, toCurrency);
            String rateText = String.format("1 %s = %.4f %s", fromCurrency, rate, toCurrency);
            textExchangeRate.setText(rateText);

        } catch (NumberFormatException e) {
            textResult.setText("0.00");
        }
    }

    private double convert(double amount, String fromCurrency, String toCurrency) {
        double rate = getExchangeRate(fromCurrency, toCurrency);
        return amount * rate;
    }

    private double getExchangeRate(String fromCurrency, String toCurrency) {
        double fromRate = EXCHANGE_RATES.get(fromCurrency);
        double toRate = EXCHANGE_RATES.get(toCurrency);
        return toRate / fromRate;
    }

    private void swapCurrencies() {
        int fromPosition = spinnerFromCurrency.getSelectedItemPosition();
        int toPosition = spinnerToCurrency.getSelectedItemPosition();

        spinnerFromCurrency.setSelection(toPosition);
        spinnerToCurrency.setSelection(fromPosition);
        // Conversion will be triggered automatically by the selection listeners
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recheck theme in case it was changed in settings
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SettingsActivity.applySavedTheme(prefs);
    }
}
