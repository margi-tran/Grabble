package margi_tran.grabble;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 *
    InventoryActivity.java
 *
 *  @author Margi Tran */

public class InventoryActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getName();

    private Inventory inventory;
    private Map<String, TextView> lettersWithQtyLabels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpQuantityLabels();
        init();
    }

    /**
     * This method maps each letter to it's corresponding quantity label from activity_inventory.xml,
     * and puts them in a HashMap for easier access.
     */
    private void setUpQuantityLabels() {
        lettersWithQtyLabels = new HashMap<>();
        String idPrefix = "label_qty_";
        for(String letter: Inventory.ALPHABET) {
            String idName = idPrefix + letter;
            int id = getResources().getIdentifier(idName, "id", getPackageName());
            TextView textView = (TextView) findViewById(id);
            lettersWithQtyLabels.put(letter, textView);
        }
    }

    private void init() {
        inventory = new Inventory(this);
        inventory.printInventory();
        Map<String, Integer> inventoryContents = inventory.getInventory();

        for(String letter : Inventory.ALPHABET) {
            TextView textView = lettersWithQtyLabels.get(letter);
            textView.setText("x" + inventoryContents.get(letter));
        }
    }
}
