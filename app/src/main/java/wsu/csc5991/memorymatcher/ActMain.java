package wsu.csc5991.memorymatcher;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
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

    private ImageView[] cells;
    private int[] images;

    private ImageView currentImageView;
    private ImageView previousImageView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_main);

        unmatchedColor = R.color.red;

        DPtoPXconversionFactor = getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160.;

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
        setImageButtons();
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

    public void setImageButtons() {
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

    public void initializeImages() {
        images = new int[NUM_IMAGES];
        images[0] = R.drawable.cheeseburger;
        images[1] = R.drawable.chicken;
        images[2] = R.drawable.chili_dog;
        images[3] = R.drawable.french_fries;
        images[4] = R.drawable.pizza;
        images[5] = R.drawable.soda_pop;
    }

    public void randomizeImages() {
        // Shuffle the cells array
        Collections.shuffle(Arrays.asList(cells));

        int imagesIndex = 0;

        for (int i = 0; i < cells.length; i++) {
            cells[i].setTag(images[imagesIndex++]);

            if (imagesIndex == images.length) {
                imagesIndex = 0;
            }
        }
    }

    public void setCellsDP(double newDP) {
        // Set the DP
        for (int i = 0; i < cells.length; i++) {
            LinearLayout.LayoutParams parms = (LinearLayout.LayoutParams) cells[i].getLayoutParams();
            parms.width = (int) (newDP);
            parms.height = (int) (newDP);
            cells[i].setLayoutParams(parms);
        }
    }

    // Change the color based on the tag of the radio button selected
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

    public void resetBoard(View view) {
        resetUnmatchedCells();
        randomizeImages();
        tries = 0;
        matches = 0;
        tvNumTries.setText(String.valueOf(tries));
        tvNumMatches.setText(String.valueOf(matches));
        selectedCells = 0;

        Toast toast = Toast.makeText(getApplicationContext(),"Board reset!", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public void resetUnmatchedCells() {
        for (int i = 0; i < cells.length; i++) {
            cells[i].setImageResource(unmatchedColor);
        }
    }

    /**
     * nextTry
     *
     * @param view The nextTry button.
     *             This method is called when the user presses the button nextTry and 2 cells
     *             have been selected. It will check if they were a match, and if so will set the background
     *             of the matched cells to yellow. If they were not a match, then they will reset back to
     *             the unmatched color.
     */
    public void nextTry(View view) {
        // Set the matched buttons to yellow
        if (isMatch) {
            currentImageView.setImageResource(R.color.yellow);
            previousImageView.setImageResource(R.color.yellow);
            matches++;
            tvNumMatches.setText(String.valueOf(matches));
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
    }

    /**
     * matchImage
     *
     * @param view The latest selected cell the user selects.
     *             If 2 different cells are selected consecutively, then we check if they are a
     *             match by the tags set. If it is a match, then we open a dialog telling the user
     *             they have successfully found a match. Otherwise, we tell them it is not a match.
     *             After the user closes the dialog, then to continue they must hit Next Try.
     */
    public void matchImage(View view) {
        selectedCells++;

        // Ignore if the user selects more than 2 cells
        if (selectedCells <= 2) {
            // Change the image to the corresponding tag
            currentImageView = (ImageView) findViewById(view.getId());
            currentImageView.setImageResource((int) currentImageView.getTag());

            if (selectedCells == 1) {
                previousImageView = currentImageView;
            } else if (selectedCells == 2) {
                // Check if the currentButton being selected has the same tag as the previous
                isMatch = ((int) currentImageView.getTag() == (int) previousImageView.getTag());
                displayDialog();
            }
        }
    }

    /**
     * displayDialog
     * Displays the appropriate dialog message to the user to tell them whether or not
     * the cells they selected were a match.
     */
    public void displayDialog() {
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
}