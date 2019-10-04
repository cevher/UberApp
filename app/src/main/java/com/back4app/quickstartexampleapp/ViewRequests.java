package com.back4app.quickstartexampleapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ViewRequests extends AppCompatActivity implements LocationListener {

    ListView listView;
    ArrayList<String> listViewContent;
    ArrayAdapter arrayAdapter;
    LocationManager locationManager;
    String provider;
    ArrayList<String> usernames;
    ArrayList<Double> latitudes;
    ArrayList<Double> longitudes;
    Location location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);

        listView = findViewById(R.id.listView);
        listViewContent = new ArrayList<>();
        listViewContent.add("Finding nearby requests...");
        usernames =new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listViewContent);
        listView.setAdapter(arrayAdapter);
        latitudes =new ArrayList<>();
        longitudes = new ArrayList<>();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
         location = locationManager.getLastKnownLocation(provider);

        if(location != null){
            updateLocation();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i  = new Intent(getApplicationContext(), ViewerRiderLocation.class);
                i.putExtra("username", usernames.get(position));
                i.putExtra("latitude", latitudes.get(position));
                i.putExtra("longitude", longitudes.get(position));
                i.putExtra("userLatitude", location.getLatitude());
                i.putExtra("userLongitude", location.getLongitude());
                startActivity(i);

            }
        });
    }

    public void updateLocation(){

        final ParseGeoPoint userLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Requests");
        //query.whereDoesNotExist("driverUsername");
        query.whereNear("requesterLocation", userLocation);
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    Log.i("Request", "success");
                    if(objects.size()>=0){
                        listViewContent.clear();
                        usernames.clear();
                        latitudes.clear();
                        longitudes.clear();


                        for(ParseObject object: objects){
                            if(object.get("driverUsername") ==null){
                                Double distance = userLocation.distanceInKilometersTo((ParseGeoPoint)object.get("requesterLocation"));
                                listViewContent.add(String.format("%.2f", distance) + " km");
                                usernames.add(object.getString("requesterUsername"));
                                latitudes.add(object.getParseGeoPoint("requesterLocation").getLatitude());
                                longitudes.add(object.getParseGeoPoint("requesterLocation").getLongitude());

                            }
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.i("Request", e.getMessage().toString());
                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {

        updateLocation();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
