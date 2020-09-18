package margi_tran.grabble;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;
import com.google.maps.android.SphericalUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static com.google.android.gms.common.api.GoogleApiClient.*;

/**
 *
    MapsActivity.java
 *
 *  @author Margi Tran */

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, ConnectionCallbacks, LocationListener,
        OnConnectionFailedListener, ResultCallback<LocationSettingsResult> ,
        GoogleMap.OnMarkerClickListener {

    private static final String TAG = LoginActivity.class.getName();
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int DEFAULT_CAMERA_ZOOM_LEVEL = 19;
    private static final int RADIUS_DISTANCE = 50; // in meters

    private static String PATH = "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/";
    private static final LatLng FORREST_HILL = new LatLng(55.946233, -3.192473);

    private GoogleMap map;
    private Location currentLocation;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;

    private Inventory inventory; // the contents of the inventory are rewritten only when the user
                                 // starts a new activity
    private CollectedMarkers collectedMarkers;

    // The following variables are used for the travelling and create words quests:
    //      1) The distance required for the travelling quest is between 6-10km.
    //      2) The words required for the create words quest is between 15-20.
    private static int MAX_DISTANCE_GOAL = 10; // this distance is in km
    private static int MAX_WORDS_GOAL = 20;
    private static int MIN_DISTANCE_GOAL = 6; // this distance is in km
    private static int MIN_WORDS_GOAL = 15;

    private boolean distanceQuestCompleted = false;
    private double distanceTravelledForQuest;
    private double distanceGoal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // the next three lines is from
        // https://github.com/googlesamples/android-play-location/blob/master/LocationSettings/app/
        // src/main/java/com/google/android/gms/location/sample/locationsettings/MainActivity.java
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();

        inventory = new Inventory(this);

        // This block of code is for testing only - to give users some letters
        /*for(String l : Inventory.ALPHABET)
            for(int i = 0; i < 5; i++)
                inventory.add(l);
        inventory.writeInventoryToStorage();*/

        collectedMarkers = new CollectedMarkers(this);
        setUpQuests();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(FORREST_HILL, DEFAULT_CAMERA_ZOOM_LEVEL));

        setUpMarkers();

        // This block of code is for testing only - make a marker always spawn near my location
        /*Placemark testMark;
        testMark = new Placemark();
        testMark.setName("Test marker 1");
        testMark.setDescription("X");
        testMark.setLatitude(55.935611);
        testMark.setLongitude(-3.23569);
        if(!collectedMarkers.getCollectedMarkers().contains("Test"))
            map.addMarker(new MarkerOptions().position(
                new LatLng(testMark.getLatitude(), testMark.getLongitude())).title(testMark.getName())
                .snippet("B"));*/


        map.setOnMarkerClickListener(this);
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
        AppIndex.AppIndexApi.start(googleApiClient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(googleApiClient, getIndexApiAction());
        googleApiClient.disconnect();
        //writeUserSharedPrefsToDb();

        UsersDbHandler usersDbHandler = new UsersDbHandler(this);
        usersDbHandler.updateUserSharedPrefsToDb(this);
        usersDbHandler.close();
    }

    /**
     * The following method is adapted from
     * https://developer.android.com/training/location/retrieve-current.html
     */
    @Override
    public void onConnected(Bundle bundle) {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null) {
            currentLocation = lastLocation;
            moveCameraToCurrentLocation();
        } else {
            Toast.makeText(this, "Last location unknown.", Toast.LENGTH_SHORT).show();
        }
        checkLocationSettings();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient.isConnected())
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        LatLng newLoc = new LatLng(location.getLatitude(), location.getLongitude());
        double distance = SphericalUtil.computeDistanceBetween(current, newLoc);
        System.out.println("ON LOC CHANGED DIST: " + distance);

        SharedPreferencesHelper.addToTotalDistancedTravelled(this, distance);
        System.out.println("TOTAL DIST: " +
                SharedPreferencesHelper.getDistanceTravelled(this));

        currentLocation = location;

        // update the distance travelled for the distance quest
        if(distanceQuestCompleted == false) {
            distanceTravelledForQuest += distance;

            if(distanceTravelledForQuest >= distanceGoal) {
                distanceQuestCompleted = true;
                SharedPreferencesHelper.addToHighscore(this,
                        QuestPoints.getPointsForDistanceQuest(distanceGoal));

                // this prevents the snackbar notification when MapsActivity is first launched
                if(SharedPreferencesHelper.get_q_DistanceTravelled(this)/1000 <
                        SharedPreferencesHelper.getDistanceGoal(this)) {
                    Snackbar sb = Snackbar.make(findViewById(R.id.coordinatorLayout),
                        "You have just completed quest \"Let's go\" for " +
                                QuestPoints.getPointsForDistanceQuest(distanceGoal/1000) + " points!",
                        Snackbar.LENGTH_INDEFINITE);
                    sb.show(); }
                }
            SharedPreferencesHelper.addTo_q_distanceTravelled(this, distance);
        }
    }

    /**
     * The following method is adapted from
     * https://github.com/googlesamples/android-play-location/blob/master/LocationSettings/app/
     * src/main/java/com/google/android/gms/location/sample/locationsettings/MainActivity.java
     */
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // NO need to show the dialog;
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    // PendingIntent unable to execute request.
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are inadequate, and cannot be fixed here. Dialog not created
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected())
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
    }

    /**
     * The following method is from
     * https://guides.codepath.com/android/Using-the-App-ToolBar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * The following method is adapted from
     * https://github.com/googlesamples/android-play-location/blob/master/LocationSettings/app/
     * src/main/java/com/google/android/gms/location/sample/locationsettings/MainActivity.java
     */
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API)
                .build();
    }

    /**
     * The following method is from
     * https://developer.android.com/training/location/change-location-settings.html
     */
    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * The following method is from
     * https://github.com/googlesamples/android-play-location/blob/master/LocationSettings/app/
     * src/main/java/com/google/android/gms/location/sample/locationsettings/MainActivity.java
     */
    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        googleApiClient, locationSettingsRequest);
        result.setResultCallback(this);
    }

    /**
     * The following method is from
     * https://github.com/googlesamples/android-play-location/blob/master/LocationSettings/app/
     * src/main/java/com/google/android/gms/location/sample/locationsettings/MainActivity.java
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    private void moveCameraToCurrentLocation() {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_CAMERA_ZOOM_LEVEL));
    }

    public void setUpMarkers() {
        downloadFile();
    }

    /**
     * The following method is adapted from
     * https://developer.android.com/training/basics/network-ops/connecting.html
     */
    private void downloadFile() {
        Date date = Calendar.getInstance().getTime();
        String day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
        String stringUrl = PATH + day.toLowerCase() + ".kml" ;
        Log.d(TAG, "Retrieving file from " + stringUrl);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
            Toast.makeText(this, "Connection is fine.", Toast.LENGTH_SHORT).show();
        } else {
            Snackbar sb = Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.no_internet,
                    Snackbar.LENGTH_INDEFINITE);
            sb.setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadFile();
                }
            });
            sb.show();
        }
    }

    /**
     * The following method is adapted from
     * http://theopentutorials.com/tutorials/android/xml/android-simple-xmlpullparser-tutorial/
     * to parse placemark xml data.
     */
    private ArrayList<Placemark> parse(String xml) {
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        ArrayList<Placemark> placemarks = new ArrayList<>();
        XmlPullParserFactory factory;
        XmlPullParser parser;

        try {
            Placemark p = null;
            String text = null;

            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();
            parser.setInput(is, null);

            int eventType = parser.getEventType();
            String name = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("placemark"))
                            p = new Placemark();
                        break;
                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("placemark")) {
                            if(!collectedMarkers.getCollectedMarkers().contains(name))
                                placemarks.add(p);
                        } else if (tagname.equalsIgnoreCase("name")) {
                            name = text;
                            p.setName(text);
                        } else if (tagname.equalsIgnoreCase("description")) {
                            p.setDescription(text);
                        } else if (tagname.equalsIgnoreCase("coordinates")) {
                            String[] s = text.split(",");
                            p.setLongitude(Double.parseDouble(s[0]));
                            p.setLatitude(Double.parseDouble(s[1]));
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return placemarks;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // get the distance of the clicked marker in meters
        LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        double distance = SphericalUtil.computeDistanceBetween(marker.getPosition(), current); // in meters

        Log.d(TAG, "Your current location: " + marker.getPosition().latitude + " " +
                marker.getPosition().longitude );
        Log.d(TAG, "Marker location: " + currentLocation.getLatitude() + " " +
                currentLocation.getLongitude() );
        Log.d(TAG, "Marker is " + distance + " away");

        // if the marker is within 50 meters of the users current location then collect it,
        // otherwise exit this method to not collect the letter
        if(distance > RADIUS_DISTANCE) {
            Toast.makeText(this, "You need to be closer to that letter to collect it!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        String letter = marker.getSnippet();
        String pointNumber = marker.getTitle();
        Toast.makeText(this, "Collected letter " + letter + " from point " + pointNumber + "!",
                Toast.LENGTH_SHORT).show();
        inventory.add(letter);
        inventory.writeInventoryToStorage();
        collectedMarkers.add(pointNumber);
        collectedMarkers.writeCollectedMarkersToPhoneStorage();
        marker.remove();

        // inventory.printInventory(); // for testing only

        SharedPreferencesHelper.addToNumberOfLettersCollected(this);
        return false;
    }

    /**
     * The following method is adapted from
     * https://developer.android.com/training/basics/network-ops/connecting.html
     */
    private class DownloadWebpageTask extends AsyncTask<String, Void, ArrayList<Placemark>> {
        @Override
        protected ArrayList<Placemark> doInBackground(String... urls) {
            try {
                String xml = downloadUrl(urls[0]);
                return parse(xml);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Placemark> placemarks) {
            for (Placemark p : placemarks)
                map.addMarker(new MarkerOptions().position(
                        new LatLng(p.getLatitude(), p.getLongitude())).title(p.getName())
                .snippet(p.getDescription()));
        }
    }

    /**
     * The following method is adapted from
     * https://developer.android.com/training/basics/network-ops/connecting.html
     */
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        BufferedReader br = null;
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int response = conn.getResponseCode();
            Log.d(TAG, "The response is: " + response);

            is = conn.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
                sb.append(line);

            return sb.toString();
        } finally {
            if(is != null) is.close();
            if(br != null) br.close();
        }
    }

    /**
     * The following method is adapted from
     * https://developer.android.com/training/appbar/actions.html
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_inventory:
                Intent viewInventoryIntent = new Intent(this, InventoryActivity.class);
                startActivity(viewInventoryIntent);
                return true;

            case R.id.action_create_word:
                Intent createWordIntent = new Intent(this, CreateWordActivity.class);
                startActivity(createWordIntent);
                return true;

            case R.id.action_leaderboard:
                Intent viewLeaderboardIntent = new Intent(this, ViewLeaderboardActivity.class);
                startActivity(viewLeaderboardIntent);
                return true;

            case R.id.action_statistics:
                Intent viewStatisticsIntent = new Intent(this, ViewStatisticsActivity.class);
                startActivity(viewStatisticsIntent);
                return true;

            case R.id.action_quests:
                Intent viewQuestsIntent = new Intent(this, ViewQuestsActivity.class);
                startActivity(viewQuestsIntent);
                return true;

            case R.id.action_quit:
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpQuests() {
        Date date = Calendar.getInstance().getTime();
        String day = new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime());
        String month = new SimpleDateFormat("MM", Locale.ENGLISH).format(date.getTime());

        String dateStored = SharedPreferencesHelper.getLastLoggedInDate(this);
        if(dateStored.equals("0,0")) // if no date was set in shared prefs then set one
            SharedPreferencesHelper.setDate(this, day, month);

        String[] splitDate = dateStored.split(",");
        String storedDay = splitDate[0];
        String storedMonth = splitDate[1];
        // if the current date is different from the one in shared prefs then reset the quests
        if(!day.equals(storedDay) && !month.equals(storedMonth)) {
            Log.d(TAG, "Needed to reset quests");
            Random random = new Random();

            // set new goal for distance quest
            // the following line was adapted from Malcolm's post via
            // http://stackoverflow.com/questions/20389890/generating-a-random-number-between-1-and-10-java
            double goalDistance = random.nextInt((MAX_DISTANCE_GOAL -
                    MIN_DISTANCE_GOAL) + 1) + MIN_DISTANCE_GOAL; // this is in km
            SharedPreferencesHelper.setDistanceGoal(this, String.valueOf(goalDistance));

            // set new goal for creating words quest
            SharedPreferencesHelper.set_q_NumbersOfWordsCreated(this, 0);
            // the following line was adapted from Malcolm's post via
            // http://stackoverflow.com/questions/20389890/generating-a-random-number-between-1-and-10-java
            int wordGoal = random.nextInt((MAX_WORDS_GOAL - MIN_WORDS_GOAL) + 1) + MIN_WORDS_GOAL;
            SharedPreferencesHelper.setWordGoal(this, wordGoal);

            // reset quest counters
            SharedPreferencesHelper.set_q_distanceTravelled(this, 0.0);
            SharedPreferencesHelper.set_q_NumbersOfWordsCreated(this, 0);
        }

        distanceTravelledForQuest = SharedPreferencesHelper.get_q_DistanceTravelled(this);
        distanceGoal = SharedPreferencesHelper.getDistanceGoal(this) * 1000; // goal in meters
    }
}