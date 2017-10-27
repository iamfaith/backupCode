package naco_siren.github.a1point3acres.activities.settings_activity;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog.Builder;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import cn.bmob.v3.Bmob;
import naco_siren.github.a1point3acres.BuildConfig;
import naco_siren.github.a1point3acres.R;

public class SettingsActivity extends AppCompatPreferenceActivity implements OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final int ENABLE_DEBUGGER_MODE_COUNTDOWN = 10;
    public static final String LOG_TAG = SettingsActivity.class.getSimpleName();
    public static final int RESULT_LOGOUT = 1;
    private int mBuildVersionClickCount;
    private Toast mToast;

    @TargetApi(11)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            SettingsActivity settingsActivity = (SettingsActivity) getActivity();
            findPreference(getString(R.string.pref_send_anonymous_data_key)).setOnPreferenceChangeListener(settingsActivity);
            findPreference(getString(R.string.pref_logout_key)).setOnPreferenceClickListener(settingsActivity);
            findPreference(getString(R.string.pref_build_version_key)).setOnPreferenceClickListener(settingsActivity);
            findPreference(getString(R.string.pref_build_version_key)).setSummary(BuildConfig.VERSION_NAME);
            findPreference(getString(R.string.pref_check_update_key)).setOnPreferenceClickListener(settingsActivity);
            findPreference(getString(R.string.pref_donation_key)).setOnPreferenceClickListener(settingsActivity);
            findPreference(getString(R.string.pref_feedback_key)).setOnPreferenceClickListener(settingsActivity);
            findPreference(getString(R.string.pref_developer_profile_key)).setOnPreferenceClickListener(settingsActivity);
            findPreference(getString(R.string.pref_developer_github_key)).setOnPreferenceClickListener(settingsActivity);
            findPreference(getString(R.string.pref_developer_mogician_key)).setOnPreferenceClickListener(settingsActivity);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "d68a0f573a01d416a3dd10223c3bbcfc");
        this.mBuildVersionClickCount = 0;
        SharedPreferences mAccountPref = getSharedPreferences(getString(R.string.account_shared_preference), 0);
        mAccountPref.edit().putInt(getString(R.string.account_key_settings_startup_count), mAccountPref.getInt(getString(R.string.account_key_settings_startup_count), 0) + 1).commit();
        setupActionBar();
        getFragmentManager().beginTransaction().replace(16908290, new GeneralPreferenceFragment()).commit();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return super.onOptionsItemSelected(item);
        }
        onBackPressed();
        return true;
    }

    public void logOut() {
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        setResult(1);
        finish();
    }

    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals(getString(R.string.pref_logout_key))) {
            new Builder(this).setTitle(getString(R.string.dialog_logout_confirm_title)).setMessage(getString(R.string.dialog_logout_confirm_content)).setPositiveButton(getString(R.string.dialog_logout_confirm_yes), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (SettingsActivity.this.mToast != null) {
                        SettingsActivity.this.mToast.cancel();
                    }
                    SettingsActivity.this.mToast = Toast.makeText(SettingsActivity.this.getApplicationContext(), SettingsActivity.this.getString(R.string.toast_logout_success), 0);
                    SettingsActivity.this.mToast.show();
                    SettingsActivity.this.logOut();
                }
            }).setNegativeButton(getString(R.string.dialog_logout_confirm_no), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (SettingsActivity.this.mToast != null) {
                        SettingsActivity.this.mToast.cancel();
                    }
                    SettingsActivity.this.mToast = Toast.makeText(SettingsActivity.this.getApplicationContext(), SettingsActivity.this.getString(R.string.toast_logout_cancelled), 0);
                    SettingsActivity.this.mToast.show();
                }
            }).show();
        } else if (key.equals(getString(R.string.pref_build_version_key))) {
            this.mBuildVersionClickCount++;
            if (this.mBuildVersionClickCount >= 3) {
                if (this.mBuildVersionClickCount < 10) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(getString(R.string.pref_build_version_easter_egg_1));
                    stringBuilder.append(10 - this.mBuildVersionClickCount);
                    stringBuilder.append(getString(R.string.pref_build_version_easter_egg_2));
                    if (this.mToast != null) {
                        this.mToast.cancel();
                    }
                    this.mToast = Toast.makeText(getApplicationContext(), stringBuilder.toString(), 0);
                    this.mToast.show();
                } else {
                    if (this.mToast != null) {
                        this.mToast.cancel();
                    }
                    this.mToast = Toast.makeText(getApplicationContext(), getString(R.string.pref_build_version_easter_egg_truth), 1);
                    this.mToast.show();
                }
            }
        } else if (key.equals(getString(R.string.pref_check_update_key))) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(getString(R.string.pref_check_update_intro_1) + "\n");
            stringBuilder.append("\n");
            stringBuilder.append(getString(R.string.pref_check_update_intro_2) + "\n");
            stringBuilder.append("\n");
            stringBuilder.append(getString(R.string.pref_check_update_intro_3) + "\n");
            new Builder(this).setTitle(getString(R.string.pref_check_update_confirm_title)).setMessage(stringBuilder.toString()).setNegativeButton(getString(R.string.pref_check_update_confirm_yes), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://github.com/naco-siren/1Point3Acres_public_release/releases"));
                    if (intent.resolveActivity(SettingsActivity.this.getPackageManager()) != null) {
                        if (SettingsActivity.this.mToast != null) {
                            SettingsActivity.this.mToast.cancel();
                        }
                        SettingsActivity.this.mToast = Toast.makeText(SettingsActivity.this.getApplicationContext(), SettingsActivity.this.getString(R.string.pref_check_update_yes_toast_1), 1);
                        SettingsActivity.this.mToast.show();
                        SettingsActivity.this.startActivity(intent);
                        return;
                    }
                    if (SettingsActivity.this.mToast != null) {
                        SettingsActivity.this.mToast.cancel();
                    }
                    SettingsActivity.this.mToast = Toast.makeText(SettingsActivity.this.getApplicationContext(), SettingsActivity.this.getString(R.string.pref_toast_install_browser), 1);
                    SettingsActivity.this.mToast.show();
                }
            }).setPositiveButton(getString(R.string.pref_check_update_confirm_no), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
        } else if (key.equals(getString(R.string.pref_donation_key))) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(getString(R.string.pref_donation_intro_1) + "\n");
            stringBuilder.append("\n");
            stringBuilder.append(getString(R.string.pref_donation_intro_2) + "\n");
            stringBuilder.append("\n");
            stringBuilder.append(getString(R.string.pref_donation_intro_3) + "\n");
            stringBuilder.append("\n");
            stringBuilder.append(getString(R.string.pref_donation_intro_4) + "\n");
            stringBuilder.append("\n");
            stringBuilder.append(getString(R.string.pref_donation_intro_5) + "\n");
            new Builder(this).setTitle(getString(R.string.pref_donation_confirm_title)).setMessage(stringBuilder.toString()).setNegativeButton(getString(R.string.pref_donation_confirm_yes), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    boolean successfullyLaunchedAlipay;
                    ((ClipboardManager) SettingsActivity.this.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("naco_siren_alipay", "15652797334"));
                    Intent launchIntent = SettingsActivity.this.getPackageManager().getLaunchIntentForPackage("com.eg.android.AlipayGphone");
                    if (launchIntent != null) {
                        try {
                            SettingsActivity.this.startActivity(launchIntent);
                            successfullyLaunchedAlipay = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            successfullyLaunchedAlipay = false;
                        }
                    } else {
                        successfullyLaunchedAlipay = false;
                    }
                    if (SettingsActivity.this.mToast != null) {
                        SettingsActivity.this.mToast.cancel();
                    }
                    SettingsActivity.this.mToast = Toast.makeText(SettingsActivity.this.getApplicationContext(), SettingsActivity.this.getString(R.string.pref_donation_hint_on_positive_feedback), 0);
                    SettingsActivity.this.mToast.show();
                    if (successfullyLaunchedAlipay) {
                        SettingsActivity.this.mToast = Toast.makeText(SettingsActivity.this.getApplicationContext(), SettingsActivity.this.getString(R.string.pref_donation_hint_on_launch_alipay_success), 1);
                    } else {
                        SettingsActivity.this.mToast = Toast.makeText(SettingsActivity.this.getApplicationContext(), SettingsActivity.this.getString(R.string.pref_donation_hint_on_launch_alipay_failure), 1);
                    }
                    SettingsActivity.this.mToast.show();
                }
            }).setPositiveButton(getString(R.string.pref_donation_confirm_no), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
        } else if (key.equals(getString(R.string.pref_feedback_key))) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(getString(R.string.pref_feedback_intro_1) + "\n");
            stringBuilder.append("\n");
            stringBuilder.append(getString(R.string.pref_feedback_intro_2) + "\n");
            stringBuilder.append("\n");
            stringBuilder.append(getString(R.string.pref_feedback_intro_3) + "\n");
            new Builder(this).setTitle(getString(R.string.pref_feedback_confirm_title)).setMessage(stringBuilder.toString()).setNegativeButton(getString(R.string.pref_feedback_confirm_yes), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://github.com/naco-siren/1Point3Acres_public_release/issues"));
                    if (intent.resolveActivity(SettingsActivity.this.getPackageManager()) != null) {
                        if (SettingsActivity.this.mToast != null) {
                            SettingsActivity.this.mToast.cancel();
                        }
                        SettingsActivity.this.mToast = Toast.makeText(SettingsActivity.this.getApplicationContext(), SettingsActivity.this.getString(R.string.pref_feedback_yes_toast_1), 1);
                        SettingsActivity.this.mToast.show();
                        SettingsActivity.this.startActivity(intent);
                        return;
                    }
                    if (SettingsActivity.this.mToast != null) {
                        SettingsActivity.this.mToast.cancel();
                    }
                    SettingsActivity.this.mToast = Toast.makeText(SettingsActivity.this.getApplicationContext(), SettingsActivity.this.getString(R.string.pref_toast_install_browser), 1);
                    SettingsActivity.this.mToast.show();
                }
            }).setPositiveButton(getString(R.string.pref_feedback_confirm_no), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("zhihu://question/20070065"));
                    if (intent.resolveActivity(SettingsActivity.this.getPackageManager()) != null) {
                        SettingsActivity.this.startActivity(intent);
                        if (SettingsActivity.this.mToast != null) {
                            SettingsActivity.this.mToast.cancel();
                        }
                        SettingsActivity.this.mToast = Toast.makeText(SettingsActivity.this.getApplicationContext(), SettingsActivity.this.getString(R.string.pref_feedback_no_toast_1), 1);
                        SettingsActivity.this.mToast.show();
                        return;
                    }
                    intent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.zhihu.com/question/20070065"));
                    if (intent.resolveActivity(SettingsActivity.this.getPackageManager()) != null) {
                        SettingsActivity.this.startActivity(intent);
                        if (SettingsActivity.this.mToast != null) {
                            SettingsActivity.this.mToast.cancel();
                        }
                        SettingsActivity.this.mToast = Toast.makeText(SettingsActivity.this.getApplicationContext(), SettingsActivity.this.getString(R.string.pref_feedback_no_toast_1), 1);
                        SettingsActivity.this.mToast.show();
                        return;
                    }
                    if (SettingsActivity.this.mToast != null) {
                        SettingsActivity.this.mToast.cancel();
                    }
                    SettingsActivity.this.mToast = Toast.makeText(SettingsActivity.this.getApplicationContext(), SettingsActivity.this.getString(R.string.pref_toast_install_browser), 1);
                    SettingsActivity.this.mToast.show();
                }
            }).show();
        } else if (key.equals(getString(R.string.pref_developer_profile_key))) {
            intent = new Intent("android.intent.action.VIEW", Uri.parse("zhihu://people/naco_siren"));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                return true;
            }
            intent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.zhihu.com/people/naco_siren"));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                return true;
            }
            if (this.mToast != null) {
                this.mToast.cancel();
            }
            this.mToast = Toast.makeText(getApplicationContext(), getString(R.string.pref_toast_install_browser), 1);
            this.mToast.show();
        } else if (key.equals(getString(R.string.pref_developer_github_key))) {
            intent = new Intent("android.intent.action.VIEW", Uri.parse("https://github.com/naco-siren"));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                return true;
            }
            if (this.mToast != null) {
                this.mToast.cancel();
            }
            this.mToast = Toast.makeText(getApplicationContext(), getString(R.string.pref_toast_install_browser), 1);
            this.mToast.show();
        } else if (key.equals(getString(R.string.pref_developer_mogician_key))) {
            try {
                getPackageManager().getPackageInfo(getString(R.string.pref_developer_mogician_google_play_package_name), 0);
                intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + getString(R.string.pref_developer_mogician_package_name)));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                    return true;
                }
            } catch (NameNotFoundException e) {
                Toast toast = this.mToast;
                Toast.makeText(this, getString(R.string.pref_developer_mogician_google_play_not_found), 0).show();
                Log.d(LOG_TAG, "Xiaomi Market not found, proceed to other markets.");
                intent = new Intent("android.intent.action.VIEW", Uri.parse("https://github.com/naco-siren/mogicians_manual_public_release/releases"));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                    return true;
                }
                if (this.mToast != null) {
                    this.mToast.cancel();
                }
                this.mToast = Toast.makeText(this, getString(R.string.pref_toast_install_browser), 1);
                this.mToast.show();
                return true;
            }
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();
        if (preference.getKey().equals(getString(R.string.pref_send_anonymous_data_key))) {
            final SwitchPreference switchPreference = (SwitchPreference) preference;
            if (switchPreference.isChecked()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(getString(R.string.pref_send_anonymous_data_warning_1) + "\n");
                stringBuilder.append(getString(R.string.pref_send_anonymous_data_warning_2) + "\n");
                stringBuilder.append("\n");
                stringBuilder.append(getString(R.string.pref_send_anonymous_data_warning_3) + "\n");
                stringBuilder.append(getString(R.string.pref_send_anonymous_data_warning_4) + "\n");
                stringBuilder.append("\n");
                stringBuilder.append(getString(R.string.pref_send_anonymous_data_warning_5) + "\n");
                new Builder(this).setTitle(getString(R.string.pref_send_anonymous_data_confirm_title)).setMessage(stringBuilder.toString()).setPositiveButton(getString(R.string.pref_send_anonymous_data_confirm_yes), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switchPreference.setChecked(false);
                    }
                }).setNegativeButton(getString(R.string.pref_send_anonymous_data_confirm_no), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switchPreference.setChecked(true);
                        if (SettingsActivity.this.mToast != null) {
                            SettingsActivity.this.mToast.cancel();
                        }
                        SettingsActivity.this.mToast = Toast.makeText(SettingsActivity.this.getApplicationContext(), SettingsActivity.this.getString(R.string.pref_toast_appreciation), 0);
                        SettingsActivity.this.mToast.show();
                    }
                }).show();
            }
        } else {
            preference.setSummary(stringValue);
        }
        return true;
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & 15) >= 4;
    }

    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName) || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    public void onBackPressed() {
        setResult(-1);
        super.onBackPressed();
    }
}
