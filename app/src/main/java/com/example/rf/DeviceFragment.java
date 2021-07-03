package com.example.rf;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DeviceFragment extends Fragment {
    String deviceBrand;
    String deviceModel;
    TextView deviceBrandTextView = null;
    TextView deviceModelTextView = null;
    String androidVersion;
    String kernelVersion;
    String sdkVersion;
    String cpuType;
    TextView cpuTypeTextView = null;
    TextView androidVersionTextView = null;
    TextView kernelVersionTextView = null;
    TextView sdkVersionTextView = null;
    TextView IMEITextView = null;
    TextView phoneTypeTextView = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        deviceBrandTextView = view.findViewById(R.id.brand);
        deviceModelTextView = view.findViewById(R.id.model);
        androidVersionTextView = view.findViewById(R.id.version);
        kernelVersionTextView = view.findViewById(R.id.kernel);
        sdkVersionTextView = view.findViewById(R.id.sdk);
        IMEITextView = view.findViewById(R.id.imei);
        cpuTypeTextView = view.findViewById(R.id.cpu);
        phoneTypeTextView = view.findViewById(R.id.device_type);
        deviceBrand = Build.MANUFACTURER;
        deviceModel = Build.MODEL;
        androidVersion = Build.VERSION.RELEASE;
        kernelVersion = System.getProperty("os.version");
        sdkVersion = Build.VERSION.SDK;
        cpuType = Build.CPU_ABI;
        deviceBrandTextView.setText(deviceBrand);
        deviceModelTextView.setText(deviceModel);
        androidVersionTextView.setText(androidVersion);
        kernelVersionTextView.setText(kernelVersion);
        sdkVersionTextView.setText(sdkVersion);
        IMEITextView.setText(AppActivity.IMEINumber);
        cpuTypeTextView.setText(cpuType);
        phoneTypeTextView.setText(AppActivity.strPhoneType);
        return view;
    }
}
