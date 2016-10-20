package device.droidtv.org.vht;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{
    private SearchView mSearchView;
    private TextView mIpTextview;

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private final String TAG = MapsActivity.class.getName();
    private float mLatitude;
    private float mLongitude;
    private String mCountryName;
    private SupportMapFragment mapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mIpTextview = (TextView) findViewById(R.id.ipTextView);
        googleApiClient = new GoogleApiClient.Builder(this)
                   .addApi(LocationServices.API).build();
        mSearchView = (SearchView)findViewById(R.id.searchView);
        mSearchView.setQueryHint(getResources().getString(R.string.search_location));
        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {}
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() > 3) {
                    mIpTextview.setVisibility(View.VISIBLE);
                    FetchAsyncTask fetchData = new FetchAsyncTask();
                    String url = "http://geo.groupkt.com/ip/"+newText+"/json";
                    Log.d(TAG,"url is:  " + url);
                    fetchData.execute(url);
                }
                else {
                    mIpTextview.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
       LatLng sydney = new LatLng(mLongitude, mLatitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title(mCountryName));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    class FetchAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "doinbackground called");
            String jsonString = null;
            try {
                URL url = new URL(params[0]);
                Log.d(TAG, "url is: "+ url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream input = urlConnection.getInputStream();
                byte[] data = new byte[1024];
                ByteArrayOutputStream bOutPut = new ByteArrayOutputStream();
                while (input.read(data) != -1) {
                    bOutPut.write(data);
                }

                jsonString = new String(bOutPut.toByteArray());
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONObject jsonObject1 = jsonObject.getJSONObject("RestResponse");
                JSONObject jsonObject2 = jsonObject1.getJSONObject("result");
                String s = jsonObject2.toString();
                String str = s.replaceAll("(,)", "\n");
                String s1 = jsonObject2.getString("longitude");
                String s2  = jsonObject2.getString("latitude");
                mLongitude = Float.parseFloat(s1);
                mLatitude = Float.parseFloat(s2);
                mCountryName  = jsonObject2.getString("country");
                Log.d(TAG, "longitude:  " + mLongitude + "latitude:  " + mLatitude + "county:  " + mCountryName);

                Log.d(TAG, "string is:   " + str);
                return str;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String item) {
            Log.d(TAG, "onPostExecute called");
            super.onPostExecute(item);
            mIpTextview.setText(item);
            onMapReady(mMap);

        }
    }
}
