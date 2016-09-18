package com.climate.spcbacore;

import android.app.ProgressDialog;
import android.graphics.Typeface;
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
import java.util.List;

public class TemperatureActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private static String TAG = MainActivity.class.getSimpleName();

    private LineChart lineChart;
    private TextView average;
    private TextView message;

    private float aveTemp = 0;
    private float totalTemp = 0;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        lineChart = (LineChart) findViewById(R.id.chart);
        average = (TextView) findViewById(R.id.txtAveTemp);
        message = (TextView) findViewById(R.id.txtMessage);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(true);

        makeJsonArrayRequest("http://cpe05api.gear.host/api/celcius");
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

                                        String celcius = weatherdata.getString("temperature_celcius");
                                        entries.add(new Entry(Float.parseFloat(celcius), i));
                                        labels.add(String.valueOf(i));

                                        totalTemp = totalTemp + Float.parseFloat(celcius);
                                        counter++;
                                    }

                                    aveTemp = totalTemp / counter;
                                    average.setText(String.valueOf(Math.round(aveTemp))+"°C");

                                    if(Math.round(aveTemp) < 36.5){
                                       //Below
                                        message.setText("This temperature is too low for your Patient");
                                    }
                                    else if(Math.round(aveTemp) > 37.5){
                                        //Above
                                        message.setText("This temperature is too high for your Patient");
                                    }
                                    else{
                                        //Normal
                                        message.setText("Currently this temperature is normal for your Patient");
                                    }

                                    if(entries.size() > 0 && labels.size() > 0){
                                        LineDataSet dataset = new LineDataSet(entries, " Temperature (°C)");

                                        LineData data = new LineData(labels, dataset);
                                        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
                                        dataset.setDrawCubic(true);
                                        dataset.setDrawFilled(true);

                                        lineChart.setDescription("Celcius");
                                        lineChart.setData(data);
                                        lineChart.animateY(5000);
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),
                                                "Error: " + "No Temperature record found",
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
                        VolleyLog.d(TAG, "Temperature Error: " + error.getMessage());
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
