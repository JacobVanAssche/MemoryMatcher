//==============================================================
/**
 Title:       MemoryMatcher
 Course:      CSC 5991 – Mobile Application Development
 Application: 2
 Author:      Jacob VanAssche
 Date:        6-08-2016
 Description:
 This application will test the user's memory. The application has a grid of twelve cells
 hiding six images. The application randomizes where the images are hidden.  The user picks one
 cell to reveal its image and then picks another cell to reveal its image. If the two images match,
 the two cells are marked as matched.  If the two images don’t match, the two cells continue to
 be marked as unmatched. This process will continue until the user correctly matches up all of the
 images.
 */
//==============================================================

package wsu.csc5991.memorymatcher;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;

public class ActMain extends AppCompatActivity {

    private NumberPicker numberPicker;

    // Constants for numberPicker DP settings
    static final private int MIN_DP = 20;
    static final private int STARTING_DP = 60;
    static final private int MAX_DP = 120;

    // Constants for size of cells and images
    static final private int NUM_CELLS = 12;
    static final private int NUM_IMAGES = 6;

    // ImageViews for each of the cells
    private ImageView[] cells;
    // The IDs of the drawable resources for the images to be placed on the cells
    private int[] images;

    // Used for when the user selects one of the cells, and then another for determining a match
    private ImageView currentImageView;
    private ImageView previousImageView;

    private Button btnNextTry;

    // Whether or not the current 2 selected cells are a match
    private boolean isMatch;

    // How many cells are consecutively selected by the user
    private int selectedCells = 0;

    // Textviews and values for number of tries and matches
    private TextView tvNumTries;
    private int tries = 0;
    private TextView tvNumMatches;
    private int matches = 0;

    private double DPtoPXconversionFactor;

    private int unmatchedColor;

    //==============================================================
    /**
     * onCreate
     * Initialize each of the TextViews, ImageViews, and set up the game by randomizing the cells.
     */
    //==============================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_main);

        // Default the unmatched color to red.
        unmatchedColor = R.color.red;

        // Initialize the conversion factor used for changing the DP.
        DPtoPXconversionFactor =
                getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160.;

        // Initialize the nextTry button.
        btnNextTry = (Button) findViewById(R.id.btnNextTry);

        // Initialize textviews for number of tries and matches.
        tvNumTries = (TextView) findViewById(R.id.tvNumTries);
        tvNumMatches = (TextView) findViewById(R.id.tvNumMatches);

        // Initialize the numberPicker.
        numberPicker = (NumberPicker) findViewById(R.id.dpPicker);
        numberPicker.setMinValue(MIN_DP);
        numberPicker.setValue(STARTING_DP);
        numberPicker.setSelected(false);
        numberPicker.setMaxValue(MAX_DP);
        numberPicker.setWrapSelectorWheel(false);

        // Initialize the cells and their image pointers
        cells = new ImageView[NUM_CELLS];
        setImageViews();
        setCellsDP(STARTING_DP * DPtoPXconversionFactor);

        // Initialize the images and set the images to each of the cells.
        initializeImages();
        resetUnmatchedCells();
        randomizeImages();

        // Listener for the numberpicker, change the DP size to specified value.
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setCellsDP(newVal * DPtoPXconversionFactor);
            }

        });
    }

    //==============================================================
    /**
     * setImageViews
     * This method initializes all of the image view cells to each of the cells on the XML
     */
    //==============================================================
    public void setImageViews() {
        cells[0] = (ImageView) findViewById(R.id.cell1);
        cells[1] = (ImageView) findViewById(R.id.cell2);
        cells[2] = (ImageView) findViewById(R.id.cell3);
        cells[3] = (ImageView) findViewById(R.id.cell4);
        cells[4] = (ImageView) findViewById(R.id.cell5);
        cells[5] = (ImageView) findViewById(R.id.cell6);
        cells[6] = (ImageView) findViewById(R.id.cell7);
        cells[7] = (ImageView) findViewById(R.id.cell8);
        cells[8] = (ImageView) findViewById(R.id.cell9);
        cells[9] = (ImageView) findViewById(R.id.cell10);
        cells[10] = (ImageView) findViewById(R.id.cell11);
        cells[11] = (ImageView) findViewById(R.id.cell12);
    }

    //==============================================================
    /**
     * enableAllImageViews
     * This method will enable all of the imageviews so that the user can click them again.
     */
    //==============================================================
    public void enableAllImageViews() {
        for (int i = 0; i < cells.length; i++) {
            cells[i].setEnabled(true);
        }
    }

    //==============================================================
    /**
     * initializeImages
     * This method will initialize the images to the drawable resources.
     */
    //==============================================================
    public void initializeImages() {
        images = new int[NUM_IMAGES];
        images[0] = R.drawable.cheeseburger;
        images[1] = R.drawable.chicken;
        images[2] = R.drawable.chili_dog;
        images[3] = R.drawable.french_fries;
        images[4] = R.drawable.pizza;
        images[5] = R.drawable.soda_pop;
    }

    //==============================================================
    /**
     * randomizeImages
     * This method will randomize which images will be on each of the cells. Each cell will be
     * associated with a tag that will keep track of which image corresponds to each cell.
     */
    //==============================================================
    public void randomizeImages() {
        // Shuffle the cells array
        Collections.shuffle(Arrays.asList(cells));

        int imagesIndex = 0;

        for (int i = 0; i < cells.length; i++) {
            cells[i].setTag(images[imagesIndex++]);

            // After iterating through the images once, reset to apply the second matches
            if (imagesIndex == images.length) {
                imagesIndex = 0;
            }
        }
    }

    //==============================================================
    /**
     * setCellsDP
     * @param newDP What to set the DP of the cells to.
     *              This method will set the DP of all of the cells to the specified DP.
     */
    //==============================================================
    public void setCellsDP(double newDP) {
        // Iterate through all the cells and set the DP
        for (int i = 0; i < cells.length; i++) {
            LinearLayout.LayoutParams parms = (LinearLayout.LayoutParams) cells[i].getLayoutParams();
            parms.width = (int) (newDP);
            parms.height = (int) (newDP);
            cells[i].setLayoutParams(parms);
        }
    }

    //==============================================================
    /**
     *
     * @param view The radioButton selected.
     *             Change the color based on the tag of the radio button selected.
     */
    //==============================================================
    public void changeColor(View view) {
        if (view.getTag().equals("RED")) {
            unmatchedColor = R.color.red;
        } else if (view.getTag().equals("BLUE")) {
            unmatchedColor = R.color.blue;
        } else if (view.getTag().equals("GREEN")) {
            unmatchedColor = R.color.green;
        }

        resetBoard(view);
    }

    //==============================================================
    /**
     * resetBoard
     *
     * @param view The resetBoard button.
     *             This method is called when the user presses the resetBoard button.
     *             All the necessary variables will be reset and a toast message will appear
     *             notifying that the board/cells have been reset and randomized.
     */
    //==============================================================
    public void resetBoard(View view) {
        resetUnmatchedCells();
        randomizeImages();
        enableAllImageViews();
        tries = 0;
        matches = 0;
        tvNumTries.setText(String.valueOf(tries));
        tvNumMatches.setText(String.valueOf(matches));
        btnNextTry.setEnabled(false);
        selectedCells = 0;

        Toast toast = Toast.makeText(getApplicationContext(), "Board reset!", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    //==============================================================
    /**
     * resetUnmatchedCells
     * This method will change all of the cells to the unmatched color currently set.
     */
    //==============================================================
    public void resetUnmatchedCells() {
        for (int i = 0; i < cells.length; i++) {
            cells[i].setImageResource(unmatchedColor);
        }
    }

    //==============================================================
    /**
     * nextTry
     *
     * @param view The nextTry button.
     *             This method is called when the user presses the button nextTry and 2 cells
     *             have been selected. It will check if they were a match, and if so will set the background
     *             of the matched cells to yellow. If they were not a match, then they will reset back to
     *             the unmatched color.
     */
    //==============================================================
    public void nextTry(View view) {
        // Set the matched buttons to yellow
        if (isMatch) {
            // Disable the cells and set their colors
            currentImageView.setEnabled(false);
            previousImageView.setEnabled(false);
            currentImageView.setImageResource(R.color.yellow);
            previousImageView.setImageResource(R.color.yellow);

            matches++;
            tvNumMatches.setText(String.valueOf(matches));

            // If user matches all of the images, then show that they won!
            if (matches == NUM_IMAGES) {
                displayVictory();
            }
        }
        // Set the unmatched buttons back to the current unmatched color
        else {
            currentImageView.setImageResource(unmatchedColor);
            previousImageView.setImageResource(unmatchedColor);
        }

        // Increase and set number of tries
        tries++;
        tvNumTries.setText(String.valueOf(tries));

        // Reset number of cells selected
        selectedCells = 0;

        // Disable the button
        btnNextTry.setEnabled(false);
    }

    //==============================================================
    /**
     * matchImage
     *
     * @param view The latest selected cell the user selects.
     *             If 2 different cells are selected consecutively, then we check if they are a
     *             match by the tags set. If it is a match, then we open a dialog telling the user
     *             they have successfully found a match. Otherwise, we tell them it is not a match.
     *             After the user closes the dialog, then to continue they must hit Next Try.
     */
    //==============================================================
    public void matchImage(View view) {

        // Only allow the user to select cells when the nextTry button is NOT enabled
        if (!btnNextTry.isEnabled()) {
            selectedCells++;
            // Change the image to the corresponding tag
            currentImageView = (ImageView) findViewById(view.getId());
            currentImageView.setImageResource((int) currentImageView.getTag());

            if (selectedCells >= 2) {
                // If the user selects 2 cells that are NOT the same, then check if they are a match
                if (currentImageView.getId() != previousImageView.getId()) {
                    // Check if the currentButton being selected has the same tag as the previous
                    isMatch = ((int) currentImageView.getTag() == (int) previousImageView.getTag());
                    displayMatchDialog();
                    btnNextTry.setEnabled(true);
                }
            } else {
                previousImageView = currentImageView;
            }
        }
    }

    //==============================================================
    /**
     * displayMatchDialog
     * Displays the appropriate dialog message to the user to tell them whether or not
     * the cells they selected were a match.
     */
    //==============================================================
    public void displayMatchDialog() {
        String title;
        String message;

        if (isMatch) {
            title = "Success!";
            message = "Match Found! Good job, keep it up!";
        } else {
            title = "Nope!";
            message = "Not a match! Try again!";
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int option) {
                        dialog.cancel();
                    }
                }
        );

        builder.show();
    }

    //==============================================================
    /**
     * displayVictory
     * Displays that the user matched all of the images!
     */
    //==============================================================
    public void displayVictory() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You win!");
        builder.setMessage("Congratulations, you matched all of the images!");

        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int option) {
                        dialog.cancel();
                    }
                }
        );

        builder.show();
    }
}