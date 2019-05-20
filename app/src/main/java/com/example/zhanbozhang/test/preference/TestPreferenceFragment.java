package com.example.zhanbozhang.test.preference;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.example.zhanbozhang.test.R;
import com.example.zhanbozhang.test.widget.CustomSwitchPreference;

public class TestPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.test);

        CustomSwitchPreference switchPreference = (CustomSwitchPreference) findPreference("test_switch");
        switchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            return false;
        });
    }
}
