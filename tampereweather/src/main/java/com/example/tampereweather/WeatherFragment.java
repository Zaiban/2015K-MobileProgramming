package com.example.tampereweather;

import android.graphics.Bitmap;
import android.os.AsyncTask;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class WeatherFragment extends Fragment {
    private Button refreshButton;
    private View mainView;

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

        // 1. Tee web request
        new WebTask().execute();

        // 2.

        mainView = view;

        TextView windText = (TextView) view.findViewById(R.id.windTextView);
        windText.setText("Wind 500m/s");

        TextView temperatureText = (TextView) view.findViewById(R.id.temperatureTextView);
        temperatureText.setText("Temperature 235C");
    }



    public String doWebRequest() {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=tampere");

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();


            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return forecastJsonStr;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.

                buffer.append(line + "\n");
                Log.w("TampereWeather", "line: " + line);
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return forecastJsonStr;
            }
            forecastJsonStr = buffer.toString();
            Log.w("TampereWeather","forecastJsonStr" + forecastJsonStr);
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return forecastJsonStr;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        Log.w("TampereWeather", "task: success: ");
        return forecastJsonStr;
    }

    private class WebTask extends AsyncTask<Object, Void, String> {
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        @Override
        protected String doInBackground(Object[] params) {
            return doWebRequest();
        }
        protected void onPostExecute(String jsonResponse){
            parseWeatherDataFromJsonResponse(jsonResponse);

        }

    }
    private void parseWeatherDataFromJsonResponse(String jsonResponse){
        JSONObject currentWeatherJSON = null;
        try {
            currentWeatherJSON = new JSONObject(jsonResponse);

            // Update wind speed to view
            JSONObject windJSON = currentWeatherJSON.getJSONObject("wind");
            String windSpeed = windJSON.getString("speed");
            TextView windTextView = (TextView) mainView.findViewById(R.id.windTextView);
            windTextView.setText("Wind speed: " + windSpeed + "m/s");

            // Update temperature to view
            Double temperature = currentWeatherJSON.getJSONObject("main").getDouble("temp");
            TextView tempTextView = (TextView) mainView.findViewById(R.id.temperatureTextView);
            tempTextView.setText("Temperature: " + temperature + " Kelvin");

            // Update weather to view
            String weather = currentWeatherJSON.getJSONObject("weather").getString("main");
            TextView weatherTextView = (TextView) mainView.findViewById(R.id.weatherTextView);
            weatherTextView.setText("Weather: " + weather);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}