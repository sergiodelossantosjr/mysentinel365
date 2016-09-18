package com.climate.spcbacore;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HumidityActivity extends AppCompatActivity {


    private ProgressDialog pDialog;
    private static String TAG = MainActivity.class.getSimpleName();

    private LineChart lineChart;
    private TextView _humidity;
    private TextView _messagehumid;
    private float _humidval = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humidity);

        lineChart = (LineChart) findViewById(R.id.chart);
        _humidity = (TextView) findViewById(R.id.txtHumidityLevel);
        _messagehumid = (TextView) findViewById(R.id.txtMessageHumid) ;

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(true);

        makeJsonArrayRequest("http://cpe05api.gear.host/api/humidity");
    }

    private void makeJsonArrayRequest(String _url) {
        showpDialog();

        JsonArrayRequest req = new JsonArrayRequest( _url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        ArrayList<Entry> entries = new ArrayList<>();
                        ArrayList<String> labels = new ArrayList<String>();

                        try {
                            // Parsing json array response
                            // loop through each json object
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject weatherdata = (JSONObject) response
                                        .get(i);

                                String humidity = weatherdata.getString("humidity");
                                entries.add(new Entry(Float.parseFloat(humidity), i));
                                labels.add(String.valueOf(i));
                                _humidity.setText(String.valueOf(Math.round(Float.parseFloat(humidity)))+"%");
                                _humidval = Float.parseFloat(humidity);
                            }

                            if(_humidval < 36.5){
                                //Below
                                _messagehumid.setText("This humidity is too low for your Patient");
                            }
                            else if(_humidval > 37.5){
                                //Above
                                _messagehumid.setText("This humidity is too high for your Patient");
                            }
                            else{
                                //Normal
                                _messagehumid.setText("Currently this humidity is normal for your Patient");
                            }

                            if(entries.size() > 0 && labels.size() > 0){
                                LineDataSet dataset = new LineDataSet(entries, " Humidity");

                                LineData data = new LineData(labels, dataset);
                                dataset.setColors(ColorTemplate.COLORFUL_COLORS);
                                dataset.setDrawCubic(true);
                                dataset.setDrawFilled(true);

                                lineChart.setDescription("Humidity(%)");
                                lineChart.setData(data);
                                lineChart.animateY(5000);
                            }
                            else{
                                Toast.makeText(getApplicationContext(),
                                        "Error: " + "No Humidity record found",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                        hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Humidity Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });

        // Adding request to request queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(req);
    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
