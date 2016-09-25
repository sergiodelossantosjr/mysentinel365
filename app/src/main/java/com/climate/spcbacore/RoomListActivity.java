package com.climate.spcbacore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RoomListActivity extends AppCompatActivity {
    ListView listView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roomlist);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);

        // Defined Array values to show in ListView
        String[] values = new String[] { "Room 101",
                "Room 102",
                "Room 103",
                "Room 104",
                "Room 105",
                "Room 106",
                "Room 107",
                "Room 108"
        };

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition  = position;

                Intent intent = new Intent(RoomListActivity.this, MainActivity.class);
                startActivity(intent);



            }

        });
    }

}
