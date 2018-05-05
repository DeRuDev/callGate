package com.example.android.openGate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.util.TypedValue;


/**
 * Created by Marco on 11/04/18.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private void setPreferenceSummary(android.support.v7.preference.Preference preference, Object value) {
        String stringValue = value.toString();
        String key = preference.getKey();
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            preference.setSummary(stringValue);
        }
    }


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        String location = getArguments().getString("LOCATION");
        location = location.replace(" ", "");

        Context activityContext = getActivity();

        PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(activityContext);
        setPreferenceScreen(preferenceScreen);

        TypedValue themeTypedValue = new TypedValue();
        activityContext.getTheme().resolveAttribute(R.attr.preferenceTheme, themeTypedValue, true);
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(activityContext, themeTypedValue.resourceId);

        // We instance each Preference using our ContextThemeWrapper object
        PreferenceCategory preferenceCategory = new PreferenceCategory(contextThemeWrapper);
        preferenceCategory.setTitle("Category test");

        EditTextPreference editTextPreference = new EditTextPreference(contextThemeWrapper);
        editTextPreference.setKey("edittext" + location);
        editTextPreference.setTitle("Add Beacon UUID");
        editTextPreference.setSummary(getPreferenceScreen().getSharedPreferences().getString("edittext" + location, " no value"));

        CheckBoxPreference checkBoxPreference = new CheckBoxPreference(contextThemeWrapper);
        checkBoxPreference.setTitle("Checkbox test");
        checkBoxPreference.setKey("checkbox" + location);
        checkBoxPreference.setChecked(true);

        ListPreference list = new ListPreference(contextThemeWrapper);
        list.setTitle("Radius");
        list.setEntries(new CharSequence[]{"0:", "15:", "30:"});
        list.setEntryValues(new CharSequence[]{"0", "15", "30"});
        list.setKey("list"+location);

        // It's REALLY IMPORTANT to add Preferences with child Preferences to the Preference Hierarchy first
        // Otherwise, the PreferenceManager will fail to load their keys

        // First we add the category to the root PreferenceScreen
        getPreferenceScreen().addPreference(preferenceCategory);

        // Then their child to it
        preferenceCategory.addPreference(editTextPreference);
        preferenceCategory.addPreference(checkBoxPreference);
        preferenceCategory.addPreference(list);


    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (null != preference) {
            if (!(preference instanceof CheckBoxPreference)) {
                setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String location = getArguments().getString("LOCATION");
        Log.d("SettingsFragment", "location is " + location);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

}
