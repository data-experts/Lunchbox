package de.data_experts.lunchbox.android.client.model;

import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author mrosenow
 */
public class Essen {

    private Essen(long id, String gericht, int preis) {
        this.name = gericht;
        this.preis = preis;
        this.id = id;
    }

    /**
     * @param in    ungleich null
     * @param datum ungleich null
     * @throws JSONException wenn irgendwas schlimmes passiert :-)
     */
    public synchronized static void init(JSONArray in, GregorianCalendar datum) throws JSONException {
        if (EssensAnbieter.isEssenLoaded(datum))
            return;
        for (int i = 0; i < in.length(); i++) {
            JSONObject object = in.getJSONObject(i);
            int anbieterId = object.getInt("provider");
            String name = object.getString("name");
            int preis = object.getInt("price");
            EssensAnbieter.getAnbieter(anbieterId).addEssensangebot(datum,
                    new Essen(name.hashCode() + anbieterId * 1000000000, name, preis));
        }
    }

    public String getName() {
        return name;
    }

    public String getPreis() {
        return preis / 100 + "," + (preis % 100) + " €";
    }

    public long getId() {
        return id;
    }

    private final String name;
    private final int preis;
    private long id;
}
