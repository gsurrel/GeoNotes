package org.surrel.geoposts;

import java.util.List;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

public class Preferences extends PreferenceActivity {
	
	public static String perimeter = "1km";
	public static Boolean ital = false,
			germ = false,
			fren = true,
			engl = true,
			comm = true, 
			sp_e = true,
			reco = true;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preferences, target);
    }

    public static class Prefs1Fragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.general_preferences);
        }
    }


    public static class Prefs2Fragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.geopost_preferences);
        }
        
        public void onPause() {
        	super.onPause();
        	engl = ((CheckBoxPreference)getPreferenceScreen().findPreference("English")).isChecked();
            germ = ((CheckBoxPreference)getPreferenceScreen().findPreference("German")).isChecked();
            ital = ((CheckBoxPreference)getPreferenceScreen().findPreference("Italian")).isChecked();
            fren = ((CheckBoxPreference)getPreferenceScreen().findPreference("French")).isChecked();
            comm = ((CheckBoxPreference)getPreferenceScreen().findPreference("Comment")).isChecked();
            reco = ((CheckBoxPreference)getPreferenceScreen().findPreference("Recommendation")).isChecked();
            sp_e = ((CheckBoxPreference)getPreferenceScreen().findPreference("Special_event")).isChecked();
            perimeter = String.valueOf(((ListPreference)getPreferenceScreen().findPreference("geoperimeter")).getEntry());
            Log.e("success", String.valueOf(engl) + " " + String.valueOf(reco) + " " + perimeter);
        }
    }
    
    public static class Prefs3Fragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.user_preferences);
            findPreference("user_id").setSummary(String.valueOf(this.getPreferenceManager().getSharedPreferences().getInt("user_id", -1)));
            findPreference("user_name").setSummary(this.getPreferenceManager().getSharedPreferences().getString("user_name", "NoName"));
            findPreference("user_email").setSummary(this.getPreferenceManager().getSharedPreferences().getString("user_email", "NoMail"));
        }
    } 
}
