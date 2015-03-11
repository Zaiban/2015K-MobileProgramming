package com.example.tampereweather;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.view.View.OnClickListener;

import com.example.tampereweather.R;


public class WeatherFragment extends Fragment {
    private Button refreshButton;

    public WeatherFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        // Bind a Listener on the refreshButton
        refreshButton = (Button) rootView.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Log.w("TampereWeather", "in WeatherFragment: onClick()");
                getWeatherData(rootView);
            }
        });
        return rootView;
    }

    public void getWeatherData( View view ){
        Log.w("TampereWeather", "in WeatherFragment: GetWeatherData()");
        // Käsittele nappulan painallus
        refreshButton.setText("Getting weather data..");
        TextView temperatureText = (TextView) view.findViewById(R.id.temperatureTextView);
        temperatureText.setText("jooh");
    }
}