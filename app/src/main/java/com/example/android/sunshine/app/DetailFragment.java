package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";

    private String forecastString;
    private ShareActionProvider shareActionProvider;
    private TextView dayView;
    private TextView dateView;
    private TextView highTempView;
    private TextView lowTempView;
    private TextView descriptionView;
    private TextView humidityView;
    private TextView pressureView;
    private TextView windView;
    private ImageView iconView;

    private static final int DETAIL_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_WIND_SPEED = 6;
    private static final int COL_WEATHER_PRESSURE = 7;
    private static final int COL_WEATHER_DEGREES = 8;
    private static final int COL_WEATHER_CONDITION_ID = 9;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        dayView = (TextView) rootView.findViewById(R.id.detail_view_day_textview);
        dateView = (TextView) rootView.findViewById(R.id.detail_view_date_textview);
        highTempView = (TextView) rootView.findViewById(R.id.detail_view_high_temp);
        lowTempView = (TextView) rootView.findViewById(R.id.detail_view_low_temp);
        descriptionView = (TextView) rootView.findViewById(R.id.detail_view_weather_desc);
        humidityView = (TextView) rootView.findViewById(R.id.detail_view_humidity);
        windView = (TextView) rootView.findViewById(R.id.detail_view_wind);
        pressureView = (TextView) rootView.findViewById(R.id.detail_view_pressure);
        iconView = (ImageView) rootView.findViewById(R.id.detail_view_weather_icon);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment, menu);
        MenuItem item = menu.findItem(R.id.action_share_id);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (forecastString != null) {
            shareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, forecastString + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null || intent.getData() == null) {
            return null;
        }

        return new CursorLoader(
                getActivity(),
                intent.getData(),
                FORECAST_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);
        int artIdForWeather = Utility.getArtResourceForWeatherCondition(weatherId);
        String day = Utility.getDayName(getActivity(), data.getLong(COL_WEATHER_DATE));
        String date = Utility.getFormattedMonthDay(getActivity(), data.getLong(COL_WEATHER_DATE));
        boolean isMetric = Utility.isMetric(getActivity());
        String high = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        String low = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        String weatherDescription = data.getString(COL_WEATHER_DESC);
        String humidity = getString(R.string.format_humidity, data.getFloat(COL_WEATHER_HUMIDITY));
        String wind = Utility.getFormattedWind(getActivity(), data.getFloat(COL_WEATHER_WIND_SPEED), data.getFloat(COL_WEATHER_DEGREES));
        String pressure = getString(R.string.format_pressure, data.getFloat(COL_WEATHER_PRESSURE));

        dayView.setText(day);
        dateView.setText(date);
        highTempView.setText(high);
        lowTempView.setText(low);
        descriptionView.setText(weatherDescription);
        humidityView.setText(humidity);
        windView.setText(wind);
        pressureView.setText(pressure);
        iconView.setImageResource(artIdForWeather);

        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}