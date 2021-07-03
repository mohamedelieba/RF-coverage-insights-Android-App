package com.example.rf;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AppActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    static JSONObject firstSimJson = new JSONObject();
    static JSONObject secondSimJson = new JSONObject();
    static String networkInfo = null;
    static String IMSINumber = null;
    static String IMEINumber = null;
    static String isDeviceRoaming = null;
    static String firstSimInfo = null;
    static String secondSimInfo = null;
    static String networkType = null;
    static int cellId = 0;
    static double longitude;
    static double latitude;
    boolean isRoaming = true;
    static int mnc = 0;
    static int mcc = 0;
    static LocationManager locationManager;
    static int signalStrength = 0;
    static String mobilePhoneInformation = null;
    static String strPhoneType = "";
    static String secondNetworkInformation = "";
    static String firstNetworkInformation = "";
    static int firstSignalStrength;
    static int secondSignalStrength;
    static SubscriptionManager subscriptionManager;
    static TelephonyManager tm = null;
    TelephonyManager subscriptionInfo = null;
    SubscriptionManager sm = null;
    String softwareVersion = null;
    String networkCountryISO = null;
    String SIMCountryISO = null;
    String msisdn = null;
    Toolbar toolbar = null;
    List<CellInfo> cellInfo = new ArrayList<>();
    String simInfo = null;
    String deviceInfo = null;
    String operator = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        msisdn = getDefaults("phoneNumber", getApplicationContext());
        try {
            firstSimJson.put("msisdn", msisdn);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            secondSimJson.put("msisdn", msisdn);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        sm = (SubscriptionManager) getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE);
        subscriptionManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("RF App");
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new NetworkFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_network);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            requestLocationUpdate();
        }

        try {
            setParameters(tm, sm, 0);
            setDeviceParameters();
            getSlotCellInfo(0);
            firstSignalStrength = signalStrength;
            firstSimInfo = setSimInfo();
            firstNetworkInformation = setNetworkInfo();
            firstSimJson.put("operator", operator);
            firstSimJson.put("country", networkCountryISO);
            firstSimJson.put("device_model", Build.MANUFACTURER);
            firstSimJson.put("imei", IMEINumber);
            firstSimJson.put("cell_type", networkType);
            firstSimJson.put("cell_id", cellId);
            firstSimJson.put("imsi", IMSINumber);
            firstSimJson.put("mnc", mnc);
            firstSimJson.put("mcc", mcc);
        } catch (NullPointerException e) {
            firstSimInfo = "You don't have a SIM in The First Slot";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            setParameters(tm, sm, 1);
            getSlotCellInfo(1);
            secondSignalStrength = signalStrength;
            secondSimInfo = setSimInfo();
            secondNetworkInformation = setNetworkInfo();
            setDeviceParameters();
            secondSimJson.put("operator", operator);
            secondSimJson.put("country", networkCountryISO);
            secondSimJson.put("device_model", Build.MANUFACTURER);
            secondSimJson.put("imei", IMEINumber);
            secondSimJson.put("cell_type", networkType);
            secondSimJson.put("cell_id", cellId);
            secondSimJson.put("imsi", IMSINumber);
            secondSimJson.put("mnc", mnc);
            secondSimJson.put("mcc", mcc);
        } catch (NullPointerException e) {
            secondSimInfo = "You don't have a SIM in The Second Slot";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getPhoneType();
        setDeviceParameters();
        mobilePhoneInformation = setDeviceInfo();


//        new CallAPI().execute("https://immense-journey-36861.herokuapp.com/measurment/DML/post", json.toString());
    }


    @SuppressLint({"MissingPermission", "NewApi"})
    public void setParameters(TelephonyManager tm, SubscriptionManager sm, int simSlot) {

        subscriptionInfo = tm.createForSubscriptionId(sm.getActiveSubscriptionInfoForSimSlotIndex(simSlot).getSubscriptionId());

        cellInfo = subscriptionInfo.getAllCellInfo();

        try {
            IMSINumber = subscriptionInfo.getSubscriberId();
        } catch (SecurityException ex) {
            IMSINumber = "Blocked By Google for Android 10+";
        }

        operator = subscriptionInfo.getSimOperatorName();

        networkCountryISO = subscriptionInfo.getNetworkCountryIso();
        SIMCountryISO = subscriptionInfo.getSimCountryIso();
        softwareVersion = subscriptionInfo.getDeviceSoftwareVersion();
        isRoaming = tm.isNetworkRoaming();
        isDeviceRoaming = isRoaming ? "YES" : "NO";

    }

    public void setDeviceParameters() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                IMEINumber = tm.getImei();
            }
        } catch (SecurityException ex) {
            IMEINumber = Settings.Secure.getString(
                    this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
    }

    public String setDeviceInfo() {
        deviceInfo = "device details:\n";
        deviceInfo += "\nDevice ID: " + IMEINumber;
        return deviceInfo;
    }

    public static String setNetworkInfo() {
        networkInfo = "";
        networkInfo += "\nRoaming: " + isDeviceRoaming;
        networkInfo += "\nNetwork Type: " + networkType;
        return networkInfo;
    }

    public String setSimInfo() {
        simInfo = "";
        simInfo += "\n operator: " + operator;
        simInfo += "\n Network Country ISO: " + networkCountryISO;
        simInfo += "\n Software Version: " + softwareVersion;
        simInfo += "\n IMSI Number: " + IMSINumber;
        simInfo += "\n MNC: " + mnc;
        simInfo += "\n MCC: " + mcc;


        return simInfo;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_sim: {
                toolbar.setTitle("SIM Card Info");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SimFragment()).commit();
            }
            break;
            case R.id.nav_network: {
                toolbar.setTitle("Network Info");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new NetworkFragment()).commit();
            }
            break;
            case R.id.nav_device: {
                toolbar.setTitle("Device Info");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new DeviceFragment()).commit();
            }
            break;

            case R.id.nav_share:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "mohamed.eliba712@gmail.com");
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share Via"));
                break;
            case R.id.nav_contat_us:
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"mohamed.eliba712@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "RF App Complaint");
                email.putExtra(Intent.EXTRA_TEXT, "Enter your Complaint or Question here!");

                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client :"));
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("MissingPermission")
    public void requestLocationUpdate() {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000 * 60, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull android.location.Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                try {
                    firstSimJson.put("longitude", longitude);
                    firstSimJson.put("latitude", latitude);
                    secondSimJson.put("longitude", longitude);
                    secondSimJson.put("latitude", latitude);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                startService(new Intent(getApplicationContext(), BackgroundService.class));

//                new CallAPI().execute("https://immense-journey-36861.herokuapp.com/measurment/DML/post", json.toString());

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }
        });
    }

    private void enableLocationSettings() {
        Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(locationIntent);
    }


    private void getPhoneType() {
        int phoneType = tm.getPhoneType();

        switch (phoneType) {
            case (TelephonyManager.PHONE_TYPE_CDMA):
                strPhoneType = "CDMA";
                break;
            case (TelephonyManager.PHONE_TYPE_GSM):
                strPhoneType = "GSM";
                break;
            case (TelephonyManager.PHONE_TYPE_SIP):
                strPhoneType = "SIP";
                break;
            case (TelephonyManager.PHONE_TYPE_NONE):
                strPhoneType = "NONE";
                break;
        }
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static CellInfo getSlotCellInfo(int slotIndex) {
        @SuppressLint("MissingPermission")
        ArrayList<CellInfo> allCellInfo = new ArrayList<>(tm.getAllCellInfo());

        @SuppressLint("MissingPermission")
        List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        SubscriptionInfo subscriptionInfo = null;

        for (int i = 0; i < activeSubscriptionInfoList.size(); i++) {
            SubscriptionInfo temp = activeSubscriptionInfoList.get(i);
            if (temp.getSimSlotIndex() == slotIndex) {
                subscriptionInfo = temp;
                break;
            }
        }
        for (int index = 0; index < allCellInfo.size(); index++) {
            CellInfo temp = allCellInfo.get(index);
            String cellType = checkCellType(temp);
            signalStrength = getSignalStrengthLevel(temp);
            if (cellType == "GSM") {
                CellIdentityGsm identity = (((CellInfoGsm) temp).getCellIdentity());
                mnc = identity.getMnc();
                mcc = identity.getMcc();
                cellId = identity.getCid();
            } else if (cellType == "W-CDMA") {
                CellIdentityWcdma identity = (((CellInfoWcdma) temp).getCellIdentity());
                mnc = identity.getMnc();
                mcc = identity.getMcc();
                cellId = identity.getCid();
            } else if (cellType == "LTE") {
                CellIdentityLte identity = (((CellInfoLte) temp).getCellIdentity());
                mnc = identity.getMnc();
                mcc = identity.getMcc();
                cellId = identity.getCi();
            }
            if (mnc == subscriptionInfo.getMnc()) {
                return temp;
            }
        }
        return null;
    }

    private static String checkCellType(CellInfo cellInfo) {

        if (cellInfo instanceof CellInfoWcdma) {
            networkType = "W-CDMA";
            return "W-CDMA";
        }
        if (cellInfo instanceof CellInfoGsm) {
            networkType = "GSM";
            return "GSM";
        }
        if (cellInfo instanceof CellInfoLte) {
            networkType = "LTE";
            return "LTE";
        }
        return null;
    }

    public static int getSignalStrengthLevel(CellInfo cellInfo) {


        if (cellInfo instanceof CellInfoWcdma) {
            return ((CellInfoWcdma) cellInfo).getCellSignalStrength().getDbm();
        }
        if (cellInfo instanceof CellInfoGsm) {
            return ((CellInfoGsm) cellInfo).getCellSignalStrength().getDbm();
        }
        if (cellInfo instanceof CellInfoLte) {
            return ((CellInfoLte) cellInfo).getCellSignalStrength().getDbm();
        }

        return -1;
    }
}
