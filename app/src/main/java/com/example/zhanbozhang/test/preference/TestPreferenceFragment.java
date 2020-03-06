package com.example.zhanbozhang.test.preference;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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

        Preference preference = findPreference("test_widget");
        ViewGroup views = (ViewGroup) preference.getView(null, null);
        dumpViewTree(views, "");
        TextView summary = views.findViewById(android.R.id.summary);
    }

    private void dumpViewTree(View view, String tab) {
        Log.i("elifli", tab + view);
        if (isViewGroup(view)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i ++) {
                dumpViewTree(((ViewGroup) view).getChildAt(i), tab + "\t");
            }
        }
    }

    private boolean isViewGroup(View view) {
        return view instanceof ViewGroup;
    }
}
