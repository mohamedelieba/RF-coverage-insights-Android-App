
package com.example.rf;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private RequestQueue requestQueue;
    private EditText phone;
    private Button btn_submit;
    static String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        checkAndRequestPermissions();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Boolean userStatus = getDefaults("isUserLoggedIn", getApplicationContext());
        if (userStatus.equals(true)) {
            switchToApp();
        } else {
            Toast.makeText(getApplicationContext(), "Enter your number", Toast.LENGTH_LONG).show();
        }

        phone = (EditText) findViewById(R.id.phone);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Please Enter Number",
                            Toast.LENGTH_LONG).show();
                } else {
                    data = phone.getText().toString();
                    setDefaults("phoneNumber", data, getApplicationContext());
                    Submit(data);
                    switchActivities();
                }
            }
        });

    }

    private void keepCurrentActivity() {
        Intent switchActivityIntent = new Intent(this, com.example.rf.MainActivity.class);
        startActivity(switchActivityIntent);
    }

    private void switchToApp() {
        Intent switchActivityIntent = new Intent(this, AppActivity.class);
        startActivity(switchActivityIntent);
    }

    private void switchActivities() {
        Intent switchActivityIntent = new Intent(this, com.example.rf.VerificationActivity.class);
        switchActivityIntent.putExtra("phone", phone.getText().toString());
        startActivity(switchActivityIntent);
    }

    private void Submit(String data) {
        final String savedata = data;
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "https://immense-journey-36861.herokuapp.com/verification/sendCode";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("msisdn", savedata);
            final String requestBody = jsonBody.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static Boolean getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, false);
    }

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
//            return false;
        }
//        return true;
    }
}