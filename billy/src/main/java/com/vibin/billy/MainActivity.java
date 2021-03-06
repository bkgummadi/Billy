package com.vibin.billy;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.viewpagerindicator.TitlePageIndicator;

/**
 * The main activity. (no pun intended)
 */

public class MainActivity extends ActionBarActivity {

    SystemBarTintManager tintManager;
    CustomFragmentAdapter mAdapter;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        tintManager = new SystemBarTintManager(this);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setTitle(" " + "Billy".toUpperCase());

        setViewpager();

        ((BillyApplication) getApplication()).getActionBarView(getWindow()).addOnLayoutChangeListener(expandedDesktopListener);
    }

    void setViewpager() {
        mAdapter = new CustomFragmentAdapter(getSupportFragmentManager(), this);
        ViewPager mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        TitlePageIndicator mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
    }


    View.OnLayoutChangeListener expandedDesktopListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            int position[] = new int[2];
            view.getLocationOnScreen(position);
            if (position[1] == 0) {
                tintManager.setStatusBarTintEnabled(false);
            } else {
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setTintColor(getResources().getColor(R.color.billy));
                tintManager.setStatusBarAlpha(1.0f);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, Settings.class);
                startActivity(settingsIntent);
                return true;
            case R.id.about:
                AboutDialog ab = AboutDialog.newInstance();
                ab.show(getSupportFragmentManager(), "ab");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).registerOnSharedPreferenceChangeListener(refreshViewpager);
    }

    /**
     * Refresh ViewPager and CustomFragmentAdapter on change of screens preference
     */
    SharedPreferences.OnSharedPreferenceChangeListener refreshViewpager = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (key.equals("screens")) {
                setViewpager();
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    public static class AboutDialog extends DialogFragment {
        View v;

        public AboutDialog() {}

        public static AboutDialog newInstance() {
            AboutDialog frag = new AboutDialog();
            Bundle args = new Bundle();
            frag.setArguments(args);
            return frag;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            v = inflater.inflate(R.layout.dialog_about, container);
            setPlaystoreButton();
            return v;
        }

        /**
         * Open app page in Play Store, if unsuccessful, open URL in browser
         */

        private void setPlaystoreButton() {
            v.findViewById(R.id.playStoreButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                    }
                }
            });
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            return dialog;
        }
    }
}

//TODO serialize arraylist data or try even simpler method
//TODO Use support library
//TODO laggy Libvlc, try low cache
//TODO put xml parsing in async task
//TODO replace default spinner in SongsFragment with Google's swiperefreshlayout
//TODO add handlers to VLC EventHandler
//TODO detect if RTMP url is needed
//TODO Allow reordering
//TODO Infinite loading for Hot 100

//TODO service quits automatically when playing after sometime
//TODO handle 2G/3G devices efficiently API Level 17
//TODO Notification click intent flags
//TODO Implement playlists
//TODO Change color of notification background
//TODO remove share history in ActionBar
//TODO Use RootTools to detect Root and then show preference

//TODO refactor
//TODO Use RemoteController and put full screen lock image for KitKat+ devices