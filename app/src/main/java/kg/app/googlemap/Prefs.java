package kg.app.googlemap;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Prefs {
    private static SharedPreferences mPreferences;
    private static Context mContext;
    private static final String MAP_POLYGON = "kg.google.map.polygon";
    public static final String PREFS_KEY = "prefs";
    public static final LatLng mLatLng = new LatLng(42.8666998, 74.5814659);

    public static void init(Context context) {
        mContext = context;
        mPreferences = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
    }

    public static void savePolygon(List<LatLng> latLngList) {
        mPreferences = mContext.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        Gson gson = new Gson();
        String list = gson.toJson(latLngList);
        editor.putString(MAP_POLYGON, list).apply();
        editor.apply();
    }
    


    public static ArrayList<LatLng> getLocation() {
        mPreferences = mContext.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        String gSonString = mPreferences.getString(MAP_POLYGON, null);
        Type type = new TypeToken<List<LatLng>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<LatLng> latLngList = gson.fromJson(gSonString, type);
        if (latLngList == null) {
            latLngList = new ArrayList<>();
            latLngList.add(mLatLng);
        }
        return latLngList;
    }
}
