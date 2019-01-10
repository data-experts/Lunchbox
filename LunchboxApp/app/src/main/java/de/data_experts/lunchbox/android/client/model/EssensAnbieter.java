package de.data_experts.lunchbox.android.client.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.LongSparseArray;

import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author mrosenow
 */
public class EssensAnbieter implements Comparable<EssensAnbieter> {
    /**
     * @param id       - ID des Anbieters
     * @param name     - Name des Anbieters
     * @param location - Stadt des Anbieters
     */
    private EssensAnbieter(int id, @NonNull String name, @NonNull String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    /**
     * @param id   ungleich null
     * @param name ungleich null
     */
    private static void registerAnbieter(int id, @NonNull String name, @NonNull String location) {
        anbieter.add(new EssensAnbieter(id, name, location));
    }

    @NonNull
    static EssensAnbieter getAnbieter(int id) {
        for (EssensAnbieter a : anbieter)
            if (a.id == id)
                return a;
        throw new RuntimeException("Anbieter mit dieser ID nicht vorhanden");
    }

    /**
     * @return Liste ungleich null
     */
    @NonNull
    public static List<EssensAnbieter> getAlleAnbieter() {
        return anbieter;
    }

    /**
     * @param input ungleich null
     * @throws JSONException wenn was schlimmes passiert :-)
     */
    public static void init(@NonNull JSONArray input) throws JSONException {
        for (int i = 0; i < input.length(); i++) {
            JSONObject jsonObject = input.getJSONObject(i);
            int id = jsonObject.getInt("id");
            String name = jsonObject.getString("name");
            String location = jsonObject.getString("location");
            registerAnbieter(id, name, location);
        }
    }

    @Override
    public int compareTo(@Nullable EssensAnbieter another) {
        if (another == null)
            return -1;
        return getName().compareTo(another.getName());
    }

    /**
     * @return der Name des Anbieters
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * @return die ID des Anbieters
     */
    public int getId() {
        return id;
    }

    /**
     * @return Ort des Essenabieters
     */
    @NonNull
    public String getLocation() {
        return location;
    }

    public List<Essen> getEssensangebote(GregorianCalendar datum) {
        return essensangebote.get(datum.getTimeInMillis());
    }

    /**
     * @param datum   ungleich null
     * @param angebot ungleich null
     */
    void addEssensangebot(@NonNull GregorianCalendar datum, Essen angebot) {
        long schluessel = datum.getTimeInMillis();
        List<Essen> angebote = essensangebote.get(schluessel);
        if (angebote == null)
            angebote = new LinkedList<>();
        if (!angebote.contains(angebot)) {
            angebote.add(angebot);
            essensangebote.put(schluessel, angebote);
        }
    }

    /**
     * @param datum ungleich null
     * @return ein boolean
     */
    public static boolean isEssenLoaded(@NonNull GregorianCalendar datum) {
        for (EssensAnbieter anbieter : getAlleAnbieter())
            if (anbieter.getEssensangebote(datum) != null)
                return true;
        return false;
    }

    /**
     * Gibt zurück, ob die Essensanbieter bereits geladen sind.
     *
     * @return ein boolean
     */
    public static boolean isAnbieterLoaded() {
        return !anbieter.isEmpty();
    }

    private final String location;
    private final String name;
    private final int id;

    private final LongSparseArray<List<Essen>> essensangebote = new LongSparseArray<>();
    private static List<EssensAnbieter> anbieter = new LinkedList<>();
}
