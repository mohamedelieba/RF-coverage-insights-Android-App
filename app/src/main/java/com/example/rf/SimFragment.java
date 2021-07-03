package com.example.rf;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import com.example.rf.R;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

public class SimFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_sim, container, false);
        TextView simData = view.findViewById(R.id.sim_text);
        TextView secondSimText = view.findViewById(R.id.second_sim_text);
        simData.setText(AppActivity.firstSimInfo+"\n");
        secondSimText.setText(AppActivity.secondSimInfo);
        return view;
    }
}
