package margi_tran.grabble;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 *
    CollectedMarkers.java
 *
 *  This class responsible for retrieving and writing the data of the user's collected markers
 *  from and to storage. It offers a method to retrieve this data in the form an arraylist.
 *
 *  @author Margi Tran */

public class CollectedMarkers {
    private static String PATH;
    private ArrayList<String> collectedMarkers;
    Context context;

    public CollectedMarkers(Context context) {
        collectedMarkers = new ArrayList<>();
        this.context = context;
        PATH = "/collected_letters_" + SharedPreferencesHelper.getUsername(context) + ".txt";
        getCollectedMarkersFromStorage();
    }

    public ArrayList<String> getCollectedMarkers() {
        return collectedMarkers;
    }

    private void getCollectedMarkersFromStorage() {
        File file;
        FileReader fr;
        BufferedReader br;
        FileWriter fw;
        BufferedWriter bw;
        try {
            file = new File(context.getFilesDir(), PATH);
            // if the file doesn't exist then create a new empty file to write
            // collected markers later
            if(!file.exists()) {
                file.createNewFile();
                file.mkdir();
                return;
            }
            // if the file does exist, then retrieve the contents line by line and use it to update
            // the collectedLetters arraylist
            fr = new FileReader(new File(context.getFilesDir(), PATH));
            br = new BufferedReader(fr);

            // need to check if the day on file matches the current day.
            String dayOnFile = br.readLine();
            Date date = Calendar.getInstance().getTime();
            String currentDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());

            // if the day on file matches the current day then load
            // its contents into the collectedMarkers arraylist
            if(dayOnFile != null && dayOnFile.equals(currentDay)) {
                String s;
                while ((s = br.readLine()) != null)
                    collectedMarkers.add(s);
            } else { // if the day don't match then the file needs to be cleared
                fw = new FileWriter(new File(context.getFilesDir(), PATH));
                bw = new BufferedWriter(fw);
                bw.write(" ");
                bw.close();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeCollectedMarkersToPhoneStorage() {
        FileWriter fw;
        BufferedWriter bw;
        try {
            fw = new FileWriter(new File(context.getFilesDir(), PATH));
            bw = new BufferedWriter(fw);
            Date date = Calendar.getInstance().getTime();
            String day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());

            bw.write(day);
            bw.newLine();
            for(String s : collectedMarkers) {
                bw.write(s);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(String point) {
        collectedMarkers.add(point);
    }
}