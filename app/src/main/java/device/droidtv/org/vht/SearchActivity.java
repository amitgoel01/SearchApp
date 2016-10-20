package device.droidtv.org.vht;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity {
    private ListView mListView;
    private SearchView mSearchView;
    private ArrayList<Item> myItemList = new ArrayList<Item>();
    private MyAdapter mAdapter = null;
    private final String TAG = SearchActivity.class.getName();
    private Handler mHandler;
    Toast mToast;
    ArrayList<Item> list = new ArrayList<Item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler(Looper.myLooper());
        mListView = (ListView) findViewById(R.id.mylist);
        mSearchView = (SearchView) findViewById(R.id.mySearchView);
        mSearchView.setQueryHint(getResources().getString(R.string.search_states));
        mSearchView.setVisibility(View.INVISIBLE);
        mAdapter = new MyAdapter(getApplicationContext(), list);
        mListView.setAdapter(mAdapter);
        mToast = Toast.makeText(this, getResources().getString(R.string.message), Toast.LENGTH_SHORT);
        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 3) {
                    mListView.setVisibility(View.VISIBLE);
                    FetchAsyncTask fetchData = new FetchAsyncTask();
                    String url = "http://services.groupkt.com/state/search/IND?text=" + newText;
                    Log.d(TAG, "url is:  " + url);
                    fetchData.execute(url);
                } else {
                    mListView.setVisibility(View.INVISIBLE);
//                    mHandler.removeCallbacks(runnable);
                }
                return false;
            }
        });
    }

    public void onStateSearch(View view) {
        mSearchView.setVisibility(View.VISIBLE);
    }

    public void onIpSearch(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    class FetchAsyncTask extends AsyncTask<String, Void, ArrayList<Item>> {
        @Override
        protected ArrayList<Item> doInBackground(String... params) {
            Log.d(TAG, "doinbackground called");
            String jsonString = null;
            try {
                URL url = new URL(params[0]);
                Log.d(TAG, "url is: " + url);
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
                JSONArray jsonArray = jsonObject1.getJSONArray("messages");
                JSONArray jsonArray1 = jsonObject1.getJSONArray("result");
                int arraylength = jsonArray1.length();
                String s = jsonArray1.toString();
                Log.d(TAG, "string is:   " + s);
                list.clear();

                for (int i = 0; i < arraylength; i++) {
                    list.add(changeObj(jsonArray1.getJSONObject(i)));
                }
                return list;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private Item changeObj(JSONObject obj) throws JSONException {
            String country = obj.getString("country");
            String name = obj.getString("name");
            String abbr = obj.getString("abbr");
            String area = obj.getString("area");
            String largest_city = null;
            String capital = obj.getString("capital");
            if (obj.has("largest_city")) {
                largest_city = obj.getString("largest_city");
            }
            return new Item(country, name, abbr, area, largest_city, capital);
        }

        @Override
        protected void onPostExecute(ArrayList<Item> items) {
            Log.d(TAG, "onPostExecute called items  " + items);

            if ((items != null) && items.size() == 0) {
                mToast.show();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mToast.cancel();
                    }
                },1000);

            }
            super.onPostExecute(items);
            mAdapter.setItemList(list);
            mAdapter.notifyDataSetChanged();

        }
    }
}

