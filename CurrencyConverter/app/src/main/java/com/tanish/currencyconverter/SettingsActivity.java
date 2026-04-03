package com.tanish.currencyconverter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "CurrencyConverterPrefs";
    private static final String KEY_DARK_MODE = "dark_mode";

    private SwitchMaterial darkModeSwitch;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        initViews();
        loadPreferences();
    }

    private void initViews() {
        ImageButton backButton = findViewById(R.id.button_back);
        darkModeSwitch = findViewById(R.id.switch_dark_mode);

        backButton.setOnClickListener(v -> finish());

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveDarkModePreference(isChecked);
            applyTheme(isChecked);
        });
    }

    private void loadPreferences() {
        boolean isDarkMode = preferences.getBoolean(KEY_DARK_MODE, false);
        darkModeSwitch.setChecked(isDarkMode);
    }

    private void saveDarkModePreference(boolean isDarkMode) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_DARK_MODE, isDarkMode);
        editor.apply();
    }

    private void applyTheme(boolean isDarkMode) {
        int nightMode = isDarkMode
            ? AppCompatDelegate.MODE_NIGHT_YES
            : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }

    public static boolean isDarkModeEnabled(SharedPreferences prefs) {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }

    public static void applySavedTheme(SharedPreferences prefs) {
        boolean isDarkMode = isDarkModeEnabled(prefs);
        int nightMode = isDarkMode
            ? AppCompatDelegate.MODE_NIGHT_YES
            : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }
}
