package margi_tran.grabble;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
    CreateWordActivity.java
 *
 *  @author Margi Tran */

public class CreateWordActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getName();

    private static final int MAX_LETTERS = 7;

    // each letter quantity label in the layout XML is prefixed with this string
    private static final String LETTER_QTY_LABEL_PREFIX = "label_qty_";

    // each letter button in the layout XML is prefixed with this string
    private static final String LETTER_BUTTON_PREFIX = "letter_";

    private Map<String, TextView> lettersWithQtyLabels;
    private Map<Integer, String> lettersWithIdsForButtons;
    private Map<String, Button> lettersWithButtons;
    private int index; // keeps the index of the last letter entered into the display
    private Inventory inventory;
    private ArrayList<String> lettersToRemove;
    private TextView[] wordDisplay;
    private Button createBtn;

    private WordsDbHandler wordsDbHandler;

    private int scoreToAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_word);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        index = 0;
        scoreToAdd = 0;
        lettersToRemove = new ArrayList<>();
        init();
        wordsDbHandler = new WordsDbHandler(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        wordsDbHandler.close();

        UsersDbHandler usersDbHandler = new UsersDbHandler(this);
        usersDbHandler.updateUserSharedPrefsToDb(this);
        usersDbHandler.close();
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            String letter = lettersWithIdsForButtons.get(id);
            int qty = getLetterQuantityFromLabel(letter);
            TextView tv = lettersWithQtyLabels.get(letter);

            if(index < 7 && qty > 0) {
                wordDisplay[index].setText(letter);
                lettersToRemove.add(letter);
                tv.setText("x" + (qty-1));

                if(qty-1 == 0)
                    disableButtonAndLetter(letter);
                index++;
            }

            if(index > 6)
                createBtn.setEnabled(true);
        }
    }

    public void init() {
        mapQuantityLabels();
        mapButtons();
        setUpWordDisplay();
        createBtn = (Button) findViewById(R.id.createBtn);
        createBtn.setEnabled(false); // create button is disabled if there isn't seven letters entered

        inventory = new Inventory(this);
        inventory.printInventory();
        Map<String, Integer> inventoryContents = inventory.getInventory();

        for(String letter : Inventory.ALPHABET) {
            lettersWithQtyLabels.get(letter).setText("x" + inventoryContents.get(letter));
            // if the quantity with its corresponding letter is 0 then disabled the button and label
            if(getLetterQuantityFromLabel(letter) == 0)
                disableButtonAndLetter(letter);
        }
    }

    /**
     * This method maps each letter to it's corresponding quantity label from
     * activity_create_word.xml, and puts them in a HashMap for easier access.
     */
    private void mapQuantityLabels() {
        lettersWithQtyLabels = new HashMap<>();
        for(String letter: Inventory.ALPHABET) {
            String idName = LETTER_QTY_LABEL_PREFIX + letter;
            int id = getResources().getIdentifier(idName, "id", getPackageName());
            TextView textView = (TextView) findViewById(id);
            lettersWithQtyLabels.put(letter, textView);
        }
    }

    /**
     * This method validates the user's entered word against the words stored in the database.
     * It is invoked by pressing the "CREATE" button.
     */
    public void processWord(View view) {
        String word = "";
        for(int i = 0; i < 7; i++) {
            word += wordDisplay[i].getText();
        }
        boolean wordInDB = wordsDbHandler.isWordInDB(word);
        if(wordInDB) {
            doWordIsInDBAction(word);
            doWordQuestCheck();
        } else {
            doWordIsNotInDBAction(word);
        }
    }

    /**
     * This method clears the last letter from the letters display and returns it to the inventory.
     * It is invoked by pressing the "DEL" button.
     */
    public void deleteLetter(View view) {
        if(index > 0) {
            if(index == 7)
                createBtn.setEnabled(false);

            String letter = "" + wordDisplay[index - 1].getText();
            wordDisplay[index - 1].setText("");
            index--;
            int qty = getLetterQuantityFromLabel(letter);
            if (qty == 0)
                enableButtonAndLetter(letter);
            lettersWithQtyLabels.get(letter).setText("x" + (qty + 1));
        }
    }

    /**
     * This method assigns each of the 7 indexes of the wordDisplay array to a letter
     * label for access.
     */
    private void setUpWordDisplay() {
        wordDisplay = new TextView[MAX_LETTERS];
        String idPrefix = "letter";
        for(int i = 0; i < 7; i++) {
            String idName = idPrefix + (i+1);
            int id = getResources().getIdentifier(idName, "id", getPackageName());
            wordDisplay[i] = (TextView) findViewById(id);
        }
    }

    /**
     * This method does two mappings:
     *  1) maps the ID of each letter button with it's corresponding letter string
     *  2) maps each letter button with its corresponding letter string
     * then each button is registered with the onClickListener.
     */
    private void mapButtons() {
        lettersWithButtons = new HashMap<>();
        lettersWithIdsForButtons = new HashMap<>();
        for(String letter : Inventory.ALPHABET) {
            String idName = LETTER_BUTTON_PREFIX + letter;
            int id = getResources().getIdentifier(idName, "id", getPackageName());
            Button btn = (Button) findViewById(id);
            lettersWithIdsForButtons.put(id, letter);
            lettersWithButtons.put(letter, btn);
            btn.setOnClickListener(new ButtonListener());
        }
    }

    /**
     * Helper method the retrieve the quantity attached to a label for a specific letter.
     */
    private int getLetterQuantityFromLabel(String letter) {
        TextView tv = lettersWithQtyLabels.get(letter);
        String qtyString = tv.getText().toString();
        return Integer.parseInt(qtyString.substring(1, qtyString.length()));
    }

    private void disableButtonAndLetter(String letter) {
        lettersWithQtyLabels.get(letter).setEnabled(false);
        lettersWithButtons.get(letter).setEnabled(false);
    }

    private void enableButtonAndLetter(String letter) {
        lettersWithQtyLabels.get(letter).setEnabled(true);
        lettersWithButtons.get(letter).setEnabled(true);
    }

    private void clearLettersDisplay() {
        for(int i = 0; i < MAX_LETTERS; i++)
            wordDisplay[i].setText("");
        index = 0;
    }

    /**
     * This method resets the word display, updates the users inventory on storage with the
     * used letters, and updates the user's highscore.
     */
    private void doWordIsInDBAction(String word) {
        int pointsToAdd = 0;
        for(int i = 0; i < MAX_LETTERS; i++) {
            String letter = wordDisplay[i].getText().toString();
            pointsToAdd += LetterPoints.valueOf(letter).value();
            wordDisplay[i].setText("");
        }
        clearLettersDisplay();
        createBtn.setEnabled(false);
        index = 0;

        for(String letter : lettersToRemove)
            inventory.remove(letter);
        lettersToRemove.clear();
        inventory.writeInventoryToStorage();
        scoreToAdd += pointsToAdd;
        SharedPreferencesHelper.addToHighscore(this, scoreToAdd);
        SharedPreferencesHelper.addToNumberOfWordsCreated(this);
        Toast.makeText(this, pointsToAdd +
                " points scored for creating \"" + word + "\"!", Toast.LENGTH_LONG).show();
    }

    /**
     * This method resets the letters display and puts the used letters back into the inventory.
     * If the there are suggestions to the users misspelled word then a pop up menu will appear
     * with them.
     */
    private void doWordIsNotInDBAction(String word) {
        clearLettersDisplay();
        for(String letter : lettersToRemove) {
            int qty = getLetterQuantityFromLabel(letter);
            if(qty == 0)
                enableButtonAndLetter(letter);
            lettersWithQtyLabels.get(letter).setText("x" + (qty+1));
        }
        lettersToRemove.clear();
        createBtn.setEnabled(false);

        // display the popup menu for possible suggestions to the user's misspelled word if there
        // are any
        Set<String> similarWords = wordsDbHandler.getSimilarWords(word);
        int numberOfSimilarWords = similarWords.size();
        if(numberOfSimilarWords == 0) {
            Toast.makeText(this, "\"" + word + "\" is not a valid word!", Toast.LENGTH_LONG).show();
            return;
        }

        String suggestionsString = "";
        for(String w: similarWords)
            suggestionsString = suggestionsString + '\n' + w;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Instead of \"" + word + "\" did you mean...")
                .setMessage(suggestionsString)
                .setPositiveButton("Back", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void doWordQuestCheck() {
        int wordGoal = SharedPreferencesHelper.getWordGoal(this);
        int noWordsCreatedForQuest = SharedPreferencesHelper.get_q_NumbersOfWordsCreated(this);

        if(noWordsCreatedForQuest == wordGoal-1) {
            Snackbar sb = Snackbar.make(findViewById(R.id.coordinatorLayout),
                    "You have just completed quest \"Make some words\" for " +
                            QuestPoints.getPointsForWordsQuest(wordGoal) + " points!",
                    Snackbar.LENGTH_INDEFINITE);
            sb.show();
        }
        SharedPreferencesHelper.addTo_q_NumberOfWordsCreated(this);
    }
}