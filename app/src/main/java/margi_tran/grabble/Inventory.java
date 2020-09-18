package margi_tran.grabble;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
    Inventory.java
 *
 *  This class responsible for retrieving and writing the data of the user's collected letters
 *  from and to storage. It offers a method to retrieve this data in the form of an arraylist.
 *
 *  @author Margi Tran */

public class Inventory {
    private static final String TAG = LoginActivity.class.getName();

    public static final String[] ALPHABET =
            { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

    private static String PATH;
    private Map<String, Integer> letters;
    Context context;

    public Inventory(Context context) {
        letters = new HashMap<>();
        this.context = context;
        PATH = "/grabble_inventory_" + SharedPreferencesHelper.getUsername(context) + ".txt";
        initLetters();
        getInventoryFromStorage();
    }

    private void initLetters() {
        for(String s : ALPHABET)
            letters.put(s, 0);
    }

    public Map<String, Integer> getInventory() {
        return letters;
    }

    private void getInventoryFromStorage() {
        File file;
        FileReader fr;
        BufferedReader br;
        try {
            file = new File(context.getFilesDir(), PATH);
            // if the file doesn't exist then create a new empty file to write the
            // contents of the inventory later
            if(!file.exists()) {
                file.createNewFile();
                file.mkdir();
                return;
            }
            // if the file does exist, then retrieve the contents line by line and use it to update
            // the letters hashmap
            fr = new FileReader(new File(context.getFilesDir(), PATH));
            br = new BufferedReader(fr);
            String s;
            while((s = br.readLine()) != null) {
                String[] arr = s.split("\\s+");
                // arr[0] holds the letter, arr[1] holds the quantity of that letter
                letters.put(arr[0], Integer.parseInt(arr[1]));
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {

        }
    }

    public void writeInventoryToStorage() {
        FileWriter fw;
        BufferedWriter bw;
        try {
            fw = new FileWriter(new File(context.getFilesDir(), PATH));
            bw = new BufferedWriter(fw);

            Iterator it = letters.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                bw.write(entry.getKey() + " " + entry.getValue());
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }

    public void add(String letter) {
        int prev = letters.get(letter);
        letters.put(letter, prev+1);
    }

    public void remove(String letter) {
        int prev = letters.get(letter);
        letters.put(letter, prev-1);
    }

    /**
     * Utility method to view the contents of the inventory.
     */
    public void printInventory() {
        Log.d(TAG, "Printing inventory:");
        Iterator it = letters.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Log.d(TAG, entry.getKey() + " " + entry.getValue());
        }
    }
}
