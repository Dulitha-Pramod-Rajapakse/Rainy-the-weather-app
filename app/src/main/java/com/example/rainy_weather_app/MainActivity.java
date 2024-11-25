package com.example.rainy_weather_app;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout Home;
    private ProgressBar Loading;
    private TextView CityName,Temperature,Condition;
    private RecyclerView WeatherRV;
    private TextInputEditText CityEdit;
    private ImageView background,icon,search;
    private ArrayList<RVModel> RVModelArrayList;
    private Adapter adapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Home = findViewById(R.id.Home);
        Loading = findViewById(R.id.loading);
        CityName = findViewById(R.id.city_name_title);
        Temperature = findViewById(R.id.Temp);
        Condition = findViewById(R.id.Condition);
        icon = findViewById(R.id.Temp_Condition);
        WeatherRV = findViewById(R.id.Rv_weather);
        CityEdit = findViewById(R.id.input_city);
        background = findViewById(R.id.background);
        search = findViewById(R.id.search_logo);
        RVModelArrayList = new ArrayList<>();
        adapter = new Adapter(this,RVModelArrayList);
        WeatherRV.setAdapter(adapter);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATIOM,Manifest.permission.ACCESS_COARSE_LOCATION,PERMISSION_CODE});
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName = getCityName(location.getLongitude(),location.getLatitude());
        getWeatherInfo(cityName);

        search.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String city = CityEdit.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();
                }else{
                    CityName.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Please provide the permisson", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitute, double latitude){
        String cityName = "Not Found";

        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> addresses = gcd.getFromLocation(latitude,longitute,10);
            for(Address adr : addresses){
                if(adr!=null){
                    String city = adr.getLocality();
                    if(city!=null && !city.equals("")){
                        cityName = city;
                    }else{
                        Log.d("TAG", "CITY NOT FOUND");
                        Toast.makeText(this, "User City Not Found!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityName){
        String url = "https://api.openweathermap.org/data/2.5/weather?" +cityName + "&appid=a4bfd904c0e393ed99ed4bf4ddf994b5";
        CityName.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Loading.setVisibility(View.GONE);
                Home.setVisibility(View.VISIBLE);
                RVModelArrayList.clear();
                try {
                    String temperature = response.getJSONObject("main").getString("temp");
                    Temperature.setText(temperature + "°c");
                    String condition = response.getJSONObject("main").getJSONObject("weather").getString("description");
                    String conditionIcon = response.getJSONObject("main").getJSONObject("weather").getString("icon");
                    Picasso.get().load("http".concat(conditionIcon)).into(icon);
                    Condition.setText(condition);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Please Enter Valid City Name...", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);

    }
}