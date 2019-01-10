package de.data_experts.lunchbox.android.client;

import de.data_experts.lunchbox.android.client.model.EssensAnbieter;
import de.data_experts.lunchbox.android.client.adapter.MenuItemAdapter;
import de.data_experts.lunchbox.android.client.model.DrawerMenuItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

/**
 * @author mrosenow
 */
public class MainActivity extends FragmentActivity {
    private DrawerLayout menuLayout;
    private ActionBarDrawerToggle menuToggle;
    private TextView textViewDatum;
    private RequestQueue httpRequestQueue;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getActionBar() != null) {
            getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getActionBar().setCustomView(R.layout.main_datum);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_drawer));
        }

        ListView menuList = findViewById(R.id.left_drawer);
        TypedArray array = getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.colorBackground,
        });
        int backgroundColor = array.getColor(0, 0xFF00FF);
        array.recycle();
        menuList.setBackgroundColor(backgroundColor);
        menuLayout = findViewById(R.id.drawer_layout);
        menuLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        final ArrayList<DrawerMenuItem> drawerMenuItems = new ArrayList<>();
        drawerMenuItems.add(new DrawerMenuItem(this, R.string.Heute, R.drawable.ic_action_go_to_today));
        drawerMenuItems.add(new DrawerMenuItem(this, R.string.Einstellungen, R.drawable.ic_action_settings));
        drawerMenuItems.add(new DrawerMenuItem(this, R.string.Info, R.drawable.ic_action_about));
        MenuItemAdapter adapter = new MenuItemAdapter(this, R.layout.menu_main_list_item, drawerMenuItems);

        menuList.setAdapter(adapter);
        menuList.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menuLayout.closeDrawers();
                switch (position) {
                    case 0:
                        viewPager.setCurrentItem(11);
                        break;
                    case 1:
                        startActivity(new Intent(parent.getContext(), SettingsActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(parent.getContext(), InfoActivity.class));
                        break;
                    default:
                }
            }
        });
        menuToggle = new ActionBarDrawerToggle(this, menuLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };

        menuLayout.addDrawerListener(menuToggle);

        // ab hier Essensliste
        httpRequestQueue = Volley.newRequestQueue(this);
        initEssenAnbieter();

        viewPager = findViewById(R.id.viewPagerMain);
        final AppSectionsPagerAdapter viewPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(11);

        View datumView = getActionBar().getCustomView();
        textViewDatum = datumView.findViewById(R.id.textViewDatum);

        viewPager.addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int item) {
                textViewDatum.setText(getDatumString(viewPagerAdapter.getDatum(item)));
            }
        });
        textViewDatum.setText(getDatumString(viewPagerAdapter.getDatum(viewPager.getCurrentItem())));

        // In den Preferences nachschauen, ob bereits eine Stadt ausgewählt wurde.
        // Falls nicht, zum Auswahldialog wechseln. Ansonsten mit der ausgewühlten Stadt weitermachen

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String choosenLocation = sharedPreferences.getString("pref_location_choose", null);
        if (choosenLocation == null || choosenLocation.equalsIgnoreCase("")) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        // Ausgewählte Stadt ium Header anzeigen
        TextView mainHeadStandort = datumView.findViewById(R.id.mainHeadStandort);
        mainHeadStandort.setText(choosenLocation);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (menuToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Gibt das Datum für die Anzeige in der Oberfläche zurück.
     *
     * @param datum ungleich null
     * @return ungleich null
     */
    @NonNull
    private String getDatumString(GregorianCalendar datum) {
        int tag = datum.get(GregorianCalendar.DATE);
        int monat = datum.get(GregorianCalendar.MONTH) + 1;
        int jahr = datum.get(GregorianCalendar.YEAR);
        String tag2 = (tag < 10 ? "0" : "") + tag;
        String monat2 = (monat < 10 ? "0" : "") + monat;
        String wochentag = datum.getDisplayName(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.LONG, Locale.GERMAN);
        return wochentag + ", " + tag2 + "." + monat2 + "." + (jahr - 2000);
    }

    /**
     *
     */
    private void initEssenAnbieter() {
        String url = "http://lunchbox.rori.info:80/api/v1/lunchProvider";
        httpRequestQueue.add(new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    EssensAnbieter.init(response);
                } catch (JSONException e) {
                    // soll so
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }));
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the primary sections of the app.
     */
    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        /**
         * Insgesamt werden 21 Tage abgedeckt. 10 in der Vergangenheit, 10 in
         * der Zukunft und heute. Dabei werden die Wochenenden übersprungen,
         * sodass es eigentlich weniger als 21 Tage sind, die überhaupt
         * angezeigt werden.
         */
        private static final int ITEM_GESAMT = 21;
        /**
         * Von den 21 Tagen ist der heutige Tag in der Mitte.
         */
        private static final int ITEM_HEUTE = 11;

        private final GregorianCalendar heute = new GregorianCalendar();

        AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            if (heute.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                heute.add(Calendar.DATE, -1);
            else if (heute.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                heute.add(Calendar.DATE, 1);
        }

        /**
         * Gibt das Datum für Position item zurück.
         *
         * @param item Eine Zahl zwischen -9 und 11 wahrscheinlich
         * @return ungleich null
         */
        @NonNull
        GregorianCalendar getDatum(int item) {
            // die Wochenenden sollen übersprungen werden
            if (item == ITEM_HEUTE)
                return heute;
            GregorianCalendar retVal = (GregorianCalendar) heute.clone();
            if (item < ITEM_HEUTE) {
                int tageVersatz = ITEM_HEUTE - item;
                for (int i = 0; i < tageVersatz; i++) {
                    retVal.add(Calendar.DATE, -1);
                    if (retVal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                        retVal.add(Calendar.DATE, -2);
                }
            } else {
                int tageVersatz = item - ITEM_HEUTE;
                for (int i = 0; i < tageVersatz; i++) {
                    retVal.add(Calendar.DATE, 1);
                    if (retVal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                        retVal.add(Calendar.DATE, 2);
                }
            }
            return retVal;
        }

        @Override
        public Fragment getItem(int i) {


            MainListViewFragment mainListViewFragment = new MainListViewFragment();
            Bundle b = new Bundle();
            b.putSerializable(MainListViewFragment.ARG_DATUM, getDatum(i));
            mainListViewFragment.setArguments(b);
            return mainListViewFragment;
        }

        @Override
        public int getCount() {
            return ITEM_GESAMT;
        }
    }
}
