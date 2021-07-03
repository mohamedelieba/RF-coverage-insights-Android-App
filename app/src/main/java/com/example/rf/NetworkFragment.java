package com.example.rf;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class NetworkFragment extends Fragment {
    TextView networkData = null;
    TextView secondNetworkData = null;
    TextView signalStrengthTextView = null;
    ProgressBar signalProgress = null;
    ProgressBar secondSignalProgress = null;
    static int firstSignalStrength;
    static int secondSignalStrength;
    TextView secondSignalStrengthTextView;
    TelephonyManager firstSubscriptionInfo;
    TelephonyManager secondSubscriptionInfo;
    List<CellInfo> firstCellInfo = new ArrayList<>();
    List<CellInfo> secondCellInfo = new ArrayList<>();
    private int signalStrength;
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_network, container, false);
        networkData = view.findViewById(R.id.network_text);
        secondNetworkData = view.findViewById(R.id.second_network_text);
        signalProgress = view.findViewById(R.id.signal_progress);
        signalStrengthTextView = view.findViewById(R.id.signal_strength);
        secondSignalProgress = view.findViewById(R.id.second__signal_progress);
        secondSignalStrengthTextView = view.findViewById(R.id.second_signal_strength);
        displayContent();
        return view;
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void displayContent() {
        try {

            AppActivity.getSlotCellInfo(0);
            signalProgress.setMin(-120);
            signalProgress.setMax(-24);
            firstSignalStrength = AppActivity.signalStrength;
            signalProgress.setProgress(firstSignalStrength);
            signalStrengthTextView.setText(firstSignalStrength+ " dbm");
            AppActivity.firstSimJson.put("signal_strength_level", firstSignalStrength);
        }
        catch (NullPointerException ex){
            signalProgress.setVisibility(View.GONE);
            signalStrengthTextView.setText("No SIM");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {

            AppActivity.getSlotCellInfo(1);
            secondSignalProgress.setMin(-120);
            secondSignalProgress.setMax(-24);
            secondSignalStrength = AppActivity.signalStrength;
            secondSignalProgress.setProgress(secondSignalStrength);
            secondSignalStrengthTextView.setText(secondSignalStrength+ " dbm");
            AppActivity.secondSimJson.put("signal_strength_level", secondSignalStrength);
        }
        catch (NullPointerException ex){
            secondSignalProgress.setVisibility(View.GONE);
            secondSignalStrengthTextView.setText("No SIM");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }


        if (AppActivity.firstNetworkInformation.equals("")) {
            networkData.setText("No SIM");
        } else {
            networkData.setText(AppActivity.firstNetworkInformation);
        }
        if (AppActivity.secondNetworkInformation.equals("")) {
            secondNetworkData.setText("No SIM");
        } else {
            secondNetworkData.setText(AppActivity.secondNetworkInformation);
        }
        refresh(100);

    }

    public void refresh(int milliseconds) {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                displayContent();
            }
        };
        handler.postDelayed(runnable, milliseconds);
    }

}
