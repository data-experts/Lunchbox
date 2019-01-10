package de.data_experts.lunchbox.android.client;

import de.data_experts.lunchbox.android.client.adapter.EssenlistAdapter;
import de.data_experts.lunchbox.android.client.adapter.FehlerAdapter;
import de.data_experts.lunchbox.android.client.model.Essen;
import de.data_experts.lunchbox.android.client.model.EssensAnbieter;

import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

/**
 * Das ist in der MainActivity die {@link ExpandableListView}. Dieses Fragment
 * kann hin und her gewischt werden.
 *
 * @author mrosenow
 */
public class MainListViewFragment extends Fragment {
    private static final String ANBIETER_COLLAPSED_PREFIX = "anbieterCollapsed_";
    static final String ARG_DATUM = "ARG_DATUM";
    private RequestQueue httpRequestQueue;
    private ExpandableListView expandableListView;
    private Handler handler;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_listview, container, false);

        progressBar = view.findViewById(R.id.mainProgressBar);
        expandableListView = view.findViewById(R.id.expandableListViewMain);
        EssenlistAdapter adapter = new EssenlistAdapter(getContext(), getDatum());
        setAdapter(adapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!EssensAnbieter.isAnbieterLoaded())
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return;
                    }
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        ladeEssensangebot();
                    }
                });
            }
        }).start();

        expandableListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                verarbeiteGroupCollapseStateChanged(groupPosition, true);
            }
        });
        expandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                verarbeiteGroupCollapseStateChanged(groupPosition, false);
            }
        });

        return view;
    }

    /**
     * Speichert den Aufklapp/Einklapp-Zustand des Anbieters.
     *
     * @param groupPosition Position des Anbieters innerhalb der Liste der Anbieter
     * @param isCollapsed   ob der Anbieter zugeklappt ist
     */
    protected void verarbeiteGroupCollapseStateChanged(int groupPosition, boolean isCollapsed) {
        EssensAnbieter anbieter = getAnbieter(groupPosition);
        if (anbieter != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            Editor editor = sharedPreferences.edit();
            editor.putBoolean(ANBIETER_COLLAPSED_PREFIX + anbieter.getId(), isCollapsed);
            editor.apply();
        }
    }

    /**
     * Gibt den Anbieter zurück, der an der übergebenen Position angezeigt wird
     *
     * @param groupPosition die Position innerhalb der Liste der Anbieter
     * @return nullable
     */
    @Nullable
    private EssensAnbieter getAnbieter(int groupPosition) {
        Object group = expandableListView.getExpandableListAdapter().getGroup(groupPosition);
        if (group instanceof EssensAnbieter)
            return (EssensAnbieter) group;
        return null;
    }

    /**
     * Setzt den ExpandableListAdapter. Dabei werden gleich noch die Anbieter
     * aufgeklappt
     *
     * @param adapter ungleich null
     */
    private void setAdapter(@NonNull ExpandableListAdapter adapter) {
        expandableListView.setAdapter(adapter);
        klappeAbschnitteAufUndZu();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        klappeAbschnitteAufUndZu();
    }

    /**
     * Klappt die Abschnitte je nach Einstellung auf oder zu
     */
    private void klappeAbschnitteAufUndZu() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        for (int i = expandableListView.getExpandableListAdapter().getGroupCount() - 1; i >= 0; i--) {
            EssensAnbieter anbieter = getAnbieter(i);
            if (anbieter != null)
                if (!sharedPreferences.getBoolean(ANBIETER_COLLAPSED_PREFIX + anbieter.getId(), false))
                    expandableListView.expandGroup(i, true);
                else
                    expandableListView.collapseGroup(i);
        }
    }

    /**
     */
    private void ladeEssensangebot() {
        final Context context = getContext();
        if (EssensAnbieter.isEssenLoaded(getDatum())) {
            setAdapter(new EssenlistAdapter(context, getDatum()));
            progressBar.setVisibility(View.GONE);
            return;
        }
        String url = "http://lunchbox.rori.info:80/api/v1/lunchOffer?day=" + getDatumString(getDatum());
        getHttpRequestQueue().add(new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Essen.init(response, getDatum());
                    if (response.length() == 0)
                        setAdapter(new FehlerAdapter(getContext(),
                                "Heute musst du wohl zu Hause Essen. Keine Essensangebote f�r heute gefunden."));
                    else
                        setAdapter(new EssenlistAdapter(context, getDatum()));
                } catch (JSONException e) {
                    e.printStackTrace();
                    setAdapter(new FehlerAdapter(getContext(),
                            "Es ist ein Fehler beim Lesen der Daten vom Server aufgetreten"));
                } finally {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError)
                    setAdapter(new FehlerAdapter(getContext(), "Kein Internetzugriff"));
                else {
                    error.printStackTrace();
                    setAdapter(new FehlerAdapter(getContext(), "Fehler beim Verbindungsaufbau zum Server"));
                }
                progressBar.setVisibility(View.GONE);
            }
        }));
    }

    /**
     * Gibt das Datum für die Anzeige in der Oberfläche zurück.
     *
     * @return ungleich null
     */
    @NonNull
    private String getDatumString(@NonNull GregorianCalendar datum) {
        int tag = datum.get(GregorianCalendar.DATE);
        int monat = datum.get(GregorianCalendar.MONTH) + 1;
        int jahr = datum.get(GregorianCalendar.YEAR);
        String tag2 = (tag < 10 ? "0" : "") + tag;
        String monat2 = (monat < 10 ? "0" : "") + monat;
        return jahr + "-" + monat2 + "-" + tag2;
    }

    @NonNull
    GregorianCalendar getDatum() {
        GregorianCalendar arg = (GregorianCalendar) (((getArguments() == null) || (getArguments().get(ARG_DATUM) == null)) ? null : getArguments().get(ARG_DATUM));
        return arg == null ? new GregorianCalendar() : arg;
    }

    @NonNull
    RequestQueue getHttpRequestQueue() {
        if (getActivity() == null)
            throw new RuntimeException("falsch!");
        if (httpRequestQueue == null)
            httpRequestQueue = Volley.newRequestQueue(getActivity());
        return httpRequestQueue;
    }

    @NonNull
    Handler getHandler() {
        if (handler == null)
            this.handler = new Handler(Looper.getMainLooper());
        return handler;
    }
}
