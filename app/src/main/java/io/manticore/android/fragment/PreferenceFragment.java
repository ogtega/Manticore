package io.manticore.android.fragment;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import io.manticore.android.R;

public class PreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }
}
