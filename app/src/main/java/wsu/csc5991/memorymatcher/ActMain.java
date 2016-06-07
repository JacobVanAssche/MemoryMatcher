package wsu.csc5991.memorymatcher;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.util.Arrays;
import java.util.Collections;

public class ActMain extends AppCompatActivity {

    private NumberPicker numberPicker;
    static final private int MIN_DP = 20;
    static final private int STARTING_DP = 60;
    static final private int MAX_DP = 120;

    static final private int NUM_CELLS = 12;
    static final private int NUM_IMAGES = 6;

    private ImageButton[] cells;
    private int[] images;

    private ImageButton currentImageButton;
    private ImageButton previousImageButton;

    private int selectedCells = 0;

    private int tries = 0;
    private int matches = 0;

    private double DPtoPXconversionFactor;

    private int unmatchedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_main);

        unmatchedColor = R.color.red;

        DPtoPXconversionFactor = getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160.;

        // Initialize the numberPicker.
        numberPicker = (NumberPicker) findViewById(R.id.dpPicker);
        numberPicker.setMinValue(MIN_DP);
        numberPicker.setValue(STARTING_DP);
        numberPicker.setMaxValue(MAX_DP);
        numberPicker.setWrapSelectorWheel(false);

        // Initialize the cells and their image pointers
        cells = new ImageButton[NUM_CELLS];
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
        cells[0] = (ImageButton) findViewById(R.id.cell1);
        cells[1] = (ImageButton) findViewById(R.id.cell2);
        cells[2] = (ImageButton) findViewById(R.id.cell3);
        cells[3] = (ImageButton) findViewById(R.id.cell4);
        cells[4] = (ImageButton) findViewById(R.id.cell5);
        cells[5] = (ImageButton) findViewById(R.id.cell6);
        cells[6] = (ImageButton) findViewById(R.id.cell7);
        cells[7] = (ImageButton) findViewById(R.id.cell8);
        cells[8] = (ImageButton) findViewById(R.id.cell9);
        cells[9] = (ImageButton) findViewById(R.id.cell10);
        cells[10] = (ImageButton) findViewById(R.id.cell11);
        cells[11] = (ImageButton) findViewById(R.id.cell12);
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
        unmatchedColor = (int) view.getTag();
        resetBoard(view);
    }

    public void resetBoard(View view) {
        resetUnmatchedCells();
        randomizeImages();
    }

    public void resetUnmatchedCells() {
        for (int i = 0; i < cells.length; i++) {
            cells[i].setImageResource(unmatchedColor);
        }
    }

    public void nextTry(View view) {
        if ((int)currentImageButton.getTag() == (int)previousImageButton.getTag()) {
            System.out.println("MATCH FOUND!");
            currentImageButton.setImageResource(R.color.yellow);
            previousImageButton.setImageResource(R.color.yellow);
            matches++;
        } else {
            System.out.println("NO MATCH!");
            currentImageButton.setImageResource(unmatchedColor);
            previousImageButton.setImageResource(unmatchedColor);
        }
        selectedCells = 0;
    }

    public void matchImage(View view) {
        selectedCells++;

        // Ignore if the user selects more than 2 cells
        if (selectedCells <= 2) {
            tries++;
            // Change the image to the corresponding tag
            currentImageButton = (ImageButton) findViewById(view.getId());
            currentImageButton.setImageResource((int) currentImageButton.getTag());

            if (selectedCells == 1) {
                previousImageButton = currentImageButton;
            } else if (selectedCells == 2) {
                System.out.println("currTag: " + currentImageButton.getTag());
                System.out.println("prevTag: " + previousImageButton.getTag());
                if ((int)currentImageButton.getTag() == (int)previousImageButton.getTag()) {
                    System.out.println("MATCH FOUND!");
                } else {
                    System.out.println("NO MATCH!");
                }
            }
        }

    }
}