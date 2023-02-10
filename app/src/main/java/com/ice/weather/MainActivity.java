package com.ice.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //
    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV, temperatureTV, conditionTV;
    private TextInputEditText cityEdt;
    private ImageView backIV, iconIV, searchIV;
    private RecyclerView weatherRV;
    private ArrayList<weatherRVModal> weatherRVModalArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        homeRL = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.ipPBloading);
        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.idIVBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idTVSearch);
        weatherRV = findViewById(R.id.idRVWeather);
        weatherRVModalArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this, weatherRVModalArrayList);
        weatherRV.setAdapter(weatherRVAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if(ActivityCompat.checkSelfPermission(this<Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_CORSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_CORSE_LOCATION},PERMISSION_CODE);
//        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_CORSE_LOCATION},PERMISSION_CODE);
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityname = getCityName(location.getLongitude(),location.getLatitude());
        getWeatherInfo(cityname);
        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                System.out.println("hello");
                String city = cityEdt.getText().toString();
                if (city.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please Enter the city",Toast.LENGTH_SHORT).show();

                }else{
                    cityNameTV.setText(cityname);
                    getWeatherInfo(city);
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permissions granted",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Please provide the permissions",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude){
        String cityname = "Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude,longitude,10);
            for(Address adr:addresses){
            if (adr!= null){
                String city = adr.getLocality();
                if(city!=null && !city.equals("")){
                    cityname = city;
                }else{
                    Log.d("TAG","City Not Found");
                    Toast.makeText(this,"City Not Found..",Toast.LENGTH_SHORT).show();
                }

            }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityname;
    };
    private void getWeatherInfo(String cityName){

        String url = "http://api.weatherapi.com/v1/forecast.json?key=f1c103fab95340c5919172507230902&q="+cityName+"&days=1&aqi=no&alerts=no";
        cityNameTV.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherRVModalArrayList.clear();
                try {
                    String temperature  = response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temperature+"°C");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String conditon = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditonIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditonIcon)).into(iconIV);
                    conditionTV.setText(conditon);
                    if (isDay == 1){
                        Picasso.get().load("https://w0.peakpx.com/wallpaper/779/771/HD-wallpaper-autumn-view-morning-nature.jpg").into(backIV);
                    }else{
                        Picasso.get().load("https://i.pinimg.com/originals/0c/d8/9b/0cd89b7b88bb1014373cee6aef3f4814.jpg").into(backIV);
                    }

                    JSONObject forecastobj = response.getJSONObject("forecast");
                    JSONObject forecast0 = forecastobj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray  hourArray = forecast0.getJSONArray("hour");
                    for (int i=0 ; i<hourArray.length();i++){
                        JSONObject hourOBj = hourArray.getJSONObject(i);
                        String time = hourOBj.getString("time");
                        String temper = hourOBj.getString("temp_c");
                        String wind = hourOBj.getString("wind_kph");
                        String img = hourOBj.getJSONObject("condition").getString("icon");

                        weatherRVModalArrayList.add(new weatherRVModal(time,temper,img,wind));

                    }
                    weatherRVAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Invalid City Name ",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}