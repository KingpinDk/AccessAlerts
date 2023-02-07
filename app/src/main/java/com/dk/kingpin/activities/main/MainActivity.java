

package com.dk.kingpin.activities.main;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dk.kingpin.R;
import com.dk.kingpin.activities.log.LogsActivity;
import com.dk.kingpin.databinding.ActivityMainBinding;
import com.dk.kingpin.manager.PreferenceManager;
import com.dk.kingpin.service.DotService;
import com.dk.kingpin.util.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.BuildConfig;


public class MainActivity extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 1802;
    private boolean TRIGGERED_START = false;
    private PreferenceManager sharedPreferenceManager;
    private Intent serviceIntent;
    private ActivityMainBinding mBinding;

    @Override
    protected void onStart() {
        super.onStart();
        if (!sharedPreferenceManager.isServiceEnabled()) {
            mBinding.mainSwitch.setChecked(checkAccessibility());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        sharedPreferenceManager = PreferenceManager.getInstance(getApplication());
        loadFromPrefs();
        init();
        checkAutoStartRequirement();
    }


    private void loadFromPrefs() {
        mBinding.vibrationSwitch.setChecked(sharedPreferenceManager.isVibrationEnabled());
        mBinding.locationSwitch.setChecked(sharedPreferenceManager.isLocationEnabled());
        mBinding.mainSwitch.setChecked(sharedPreferenceManager.isServiceEnabled());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init() {
        mBinding.locationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    new MaterialAlertDialogBuilder(MainActivity.this)
                            .setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_round_location))
                            .setTitle(R.string.requires_locationperm)
                            .setMessage(R.string.requires_locationperm_message)
                            .setNeutralButton(R.string.button_later, (dialog, which) -> mBinding.locationSwitch.setChecked(false))
                            .setPositiveButton(R.string.button_continue, (dialog, which) -> {
                                askPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                            })
                            .show();
                } else {
                    sharedPreferenceManager.setLocationEnabled(true);
                }
            }

        });
        mBinding.vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> sharedPreferenceManager.setVibrationEnabled(isChecked));
        mBinding.mainSwitch.setOnCheckedChangeListener((buttonView, b) -> {
            if (b) {
                checkForAccessibilityAndStart();
                TRIGGERED_START = true;
            } else {
                stopService();
                TRIGGERED_START = false;
            }
        });
        mBinding.logsOption.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LogsActivity.class);
            startActivity(intent);
        });
        mBinding.versionText.setText(getString(R.string.version) + BuildConfig.VERSION_NAME);
    }

    private void checkForAccessibilityAndStart() {
        if (!accessibilityPermission(getApplicationContext(), DotService.class)) {
            mBinding.mainSwitch.setChecked(false);
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.requires_accessibility)
                    .setMessage(R.string.requires_accessibility_message)
                    .setIcon(R.drawable.ic_baseline_accessibility_24)
                    .setPositiveButton(R.string.requires_accessibility_open, (dialog, which) -> startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS")))
                    .setNegativeButton(R.string.cancel, null)
                    .setCancelable(true)
                    .show();
        } else {
            mBinding.mainSwitch.setChecked(true);
            sharedPreferenceManager.setServiceEnabled(true);
            serviceIntent = new Intent(MainActivity.this, DotService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }
    }

    private void stopService() {
        if (isAccessibilityServiceRunning()) {
//            sharedPreferenceManager.setServiceEnabled(false);
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, getString(R.string.close_app_note), Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (TRIGGERED_START) {
//            TRIGGERED_START = false;
//            checkForAccessibilityAndStart();
//        }
//        if (!sharedPreferenceManager.isServiceEnabled()) {
//            mBinding.mainSwitch.setChecked(checkAccessibility());
//        }
//    }

    /**
     * @param context
     * @param cls
     * @return
     */
    public static boolean accessibilityPermission(Context context, Class<?> cls) {
        ComponentName componentName = new ComponentName(context, cls);
        String string = Settings.Secure.getString(context.getContentResolver(), "enabled_accessibility_services");
        if (string == null) {
            return false;
        }
        TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(':');
        simpleStringSplitter.setString(string);
        while (simpleStringSplitter.hasNext()) {
            ComponentName unflattenFromString = ComponentName.unflattenFromString(simpleStringSplitter.next());
            if (unflattenFromString != null && unflattenFromString.equals(componentName)) {
                return true;
            }
        }
        return false;
    }



    /**
     * @return
     */
    private boolean checkAccessibility() {
        AccessibilityManager manager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        return manager.isEnabled();
    }

    /**
     * @return
     */
    private boolean isAccessibilityServiceRunning() {
        String prefString = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return prefString != null && prefString.contains(this.getPackageName() + "/" + DotService.class.getName());
    }


    /**
     * @param message
     */
    private void showSnack(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * @param url
     */
    private void openWeb(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }


    /**
     * Asks permission runtime
     *
     * @param permission
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askPermission(String permission) {
        if (!(ContextCompat.checkSelfPermission(this, permission) == 0)) {
            requestPermissions(new String[]{permission}, 0);
            sharedPreferenceManager.setLocationEnabled(true);
        }
    }

    /**
     * Chinese ROM's kill the app services frequently so AutoStart Permission is required
     */
    private void checkAutoStartRequirement() {
        String manufacturer = android.os.Build.MANUFACTURER;
        if (sharedPreferenceManager.isFirstLaunch()) {
            if ("xiaomi".equalsIgnoreCase(manufacturer)
                    || ("oppo".equalsIgnoreCase(manufacturer))
                    || ("vivo".equalsIgnoreCase(manufacturer))
                    || ("Honor".equalsIgnoreCase(manufacturer))) {
                Utils.showAutoStartDialog(MainActivity.this, manufacturer);
                sharedPreferenceManager.setFirstLaunch();
            }
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}