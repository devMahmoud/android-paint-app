
// Draws View which changes color in response to user touches.
package com.example.dev_mahmoud.paintoz;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;




public class Doodlz extends Activity {

   private DoodleView doodleView; // drawing view
   private SensorManager sensorManager; // monitors Accelerometer
   private float acceleration;
   private float currentAcceleration;
   private float lastAcceleration;
   private AtomicBoolean dialogIsVisible = new AtomicBoolean(); // false

   // create menu ids for each menu option
   private static final int COLOR_MENU_ID = Menu.FIRST;
    private static final int WIDTH_MENU_ID = Menu.FIRST + 1;
    private static final int ERASE_MENU_ID = Menu.FIRST + 2;
    private static final int CLEAR_MENU_ID = Menu.FIRST + 3;
    private static final int SAVE_MENU_ID = Menu.FIRST + 4;

   // value used to determine whether user shook the device to erase
    private static final int ACCELERATION_THRESHOLD = 15000;

   // variable that refers to a choose color or choose line width dialog
   private Dialog currentDialog;

   // called when this activity is loaded
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); // inflate the layout



        // get reference to the DoodleView
        doodleView = (DoodleView) findViewById(R.id.doodleView);

        // initialize acceleration values
        acceleration = 0.00f;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = SensorManager.GRAVITY_EARTH;

        enableAccelerometerListening(); // listen for shake
    } // end of onCreate method

    // when the app is sent to the background , stop listening for sensor events
    @Override
    protected void onPause() {

        super.onPause();
        disableAccelerometerListening(); // do not listen for shake
    } // end of onPause method

    // enable listening for accelerometer events
    private void enableAccelerometerListening(){

        // initialize the SensorManager
        sensorManager =
                (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                sensorManager.SENSOR_DELAY_NORMAL);
    } // end of enableAccelerometerListening method

    // disable listening for accelerometer events
    private void disableAccelerometerListening(){

        // stop listening for sensor events
        if (sensorManager != null){

            sensorManager.unregisterListener(
                    sensorEventListener,
                    sensorManager.getDefaultSensor(
                            SensorManager.SENSOR_ACCELEROMETER));
            sensorManager = null;
        } // end if
    } // end of disableAccelerometerListening method

    // event handler for accelerometer events
    private SensorEventListener sensorEventListener =
            new SensorEventListener()
            {
                // use accelerometer to determine whether user shook device
                @Override
                public void onSensorChanged(SensorEvent event) {

                    // ensure that other dialog is not displayed
                    if (!dialogIsVisible.get()){

                        // get x , y and z values for the SensorEvent
                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];

                        // save previous acceleration value
                        lastAcceleration = currentAcceleration;

                        // calculate the current acceleration
                        currentAcceleration = x * x+y * y+z * z;

                        // calculate the change in acceleration
                        acceleration = currentAcceleration *
                                (currentAcceleration - lastAcceleration);

                        // if acceleration is above a certain threshold
                        if (acceleration > ACCELERATION_THRESHOLD){

                            // create a new AlertDialog Builder
                            AlertDialog.Builder builder =
                                    new AlertDialog.Builder(Doodlz.this);

                            // set the AlertDialog's message
                            builder.setMessage(R.string.message_erase);
                            builder.setCancelable(true);

                            // add erase button
                            builder.setPositiveButton(R.string.button_erase ,
                                    new DialogInterface.OnClickListener(){

                                        public void onClick(DialogInterface dialog , int id){

                                            dialogIsVisible.set(false);
                                            doodleView.clear(); // clear the screen
                                        } // end of onClick method
                                    } // end anonymous inner class
                            ); // end call to setPositiveButton

                            // add cancel button
                            builder.setNegativeButton(R.string.button_cancel ,
                                    new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog , int id)
                                        {
                                            dialogIsVisible.set(false);
                                            dialog.cancel(); // dismiss the dialog
                                        } // end of onClick method
                                    } // end anonymous inner class
                            );

                            dialogIsVisible.set(true); // dialog is on the screen
                            builder.show();
                        } // end if
                    } // end if
                } // end of onSensorChanged method

                // required method of interface SensorEventListener
                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {


                }
            };
    // displays configuration options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu); // call super method


        // add options to menu
        menu.add(Menu.NONE, COLOR_MENU_ID, Menu.NONE,
                R.string.menuitem_color);
        menu.add(Menu.NONE, WIDTH_MENU_ID, Menu.NONE,
                R.string.menuitem_line_width);
        menu.add(Menu.NONE, ERASE_MENU_ID, Menu.NONE,
                R.string.menuitem_erase);
        menu.add(Menu.NONE, CLEAR_MENU_ID, Menu.NONE,
                R.string.menuitem_clear);
        menu.add(Menu.NONE, SAVE_MENU_ID, Menu.NONE,
                R.string.menuitem_save_image);

        return true; // option menu creation was handled
    } // çàâåðøåíèå ìåòîäà onCreateOptionsMenu

    // handle choice from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // switch based on the MenuItem id
        switch (item.getItemId()) {

            case COLOR_MENU_ID:
                showColorDialog(); // displays color selection dialog
                return true; // consume the menu event
            case WIDTH_MENU_ID:
                showLineWidthDialog(); // displays line thickness dialog
                return true; // consume the menu event
            case ERASE_MENU_ID:
                doodleView.setDrawingColor(Color.WHITE); // line color white
                return true; // consume the menu event
            case CLEAR_MENU_ID:
                doodleView.clear(); // clear doodleView
                return true; // consume the menu event
            case SAVE_MENU_ID:
                doodleView.saveImage(); // save the current images
                return true; // consume the menu event
        } // end switch

        return super.onOptionsItemSelected(item); // call super's method
    } // end method onOptionsItemSelected

       // displays a dialog for selection color
    private void showColorDialog() {

        // create the dialog and inflate its content
        currentDialog = new Dialog(this);
        currentDialog.setContentView(R.layout.color_dialog);
        currentDialog.setTitle(R.string.title_color_dialog);
        currentDialog.setCancelable(true);

        // get color seekBars and set their onChange listeners
        final SeekBar alphaSeekBar =
                (SeekBar) currentDialog.findViewById(R.id.alphaSeekBar);
        final SeekBar redSeekBar =
                (SeekBar) currentDialog.findViewById(R.id.redSeekBar);
        final SeekBar greenSeekBar =
                (SeekBar) currentDialog.findViewById(R.id.greenSeekBar);
        final SeekBar blueSeekBar =
                (SeekBar) currentDialog.findViewById(R.id.blueSeekBar);

        // register seekBar event listeners
        alphaSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        redSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        greenSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        blueSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);

        // use current drawing color to set SeekBar values
        final int color = doodleView.getDrawingColor();
        alphaSeekBar.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        blueSeekBar.setProgress(Color.blue(color));

        // set the set color Button's onClickListener
        Button setColorButton = (Button) currentDialog
                .findViewById(R.id.setColorButton);
        setColorButton.setOnClickListener(setColorButtonListener);

        dialogIsVisible.set(true); // dialog is on the screen
        currentDialog.show(); // show the dialog
    } // end method showColorDialog

    // OnSeekBarChangeListener for the SeekBars in the color dialog
    private SeekBar.OnSeekBarChangeListener colorSeekBarChanged =
            new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

            // get the SeekBars and the colorView LinearLayout
            SeekBar alphaSeekBar =
                    (SeekBar) currentDialog.findViewById(R.id.alphaSeekBar);
            SeekBar redSeekBar =
                    (SeekBar) currentDialog.findViewById(R.id.redSeekBar);
            SeekBar greenSeekBar =
                    (SeekBar) currentDialog.findViewById(R.id.greenSeekBar);
            SeekBar blueSeekBar =
                    (SeekBar) currentDialog.findViewById(R.id.blueSeekBar);
            View colorView =
                    (View) currentDialog.findViewById(R.id.colorView);

            // display the current color
            colorView.setBackgroundColor(Color.argb(
                    alphaSeekBar.getProgress(), redSeekBar.getProgress(),
                    greenSeekBar.getProgress(), blueSeekBar.getProgress()));
        } // end method onProgressChanged

        // required method of interface OnSeekBarChangeListener
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        } // end method onStartTrackingTouch

        // required method of interface OnSeekBarChangeListener
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        } // end method onStopTrackingTouch
    }; // end colorSeekBarChanged

    // OnClickListener for color dialog's Set Color Button
    private View.OnClickListener setColorButtonListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {

            // get color SeekBar
            SeekBar alphaSeekBar =
                    (SeekBar) currentDialog.findViewById(R.id.alphaSeekBar);
            SeekBar redSeekBar =
                    (SeekBar) currentDialog.findViewById(R.id.redSeekBar);
            SeekBar greenSeekBar =
                    (SeekBar) currentDialog.findViewById(R.id.greenSeekBar);
            SeekBar blueSeekBar =
                    (SeekBar) currentDialog.findViewById(R.id.blueSeekBar);

            // set the line color
            doodleView.setDrawingColor(Color.argb(
                    alphaSeekBar.getProgress(), redSeekBar.getProgress(),
                    greenSeekBar.getProgress(), blueSeekBar.getProgress()));
            dialogIsVisible.set(false); // dialog is not on the screen
            currentDialog.dismiss(); // hide the dialog
            currentDialog = null; // dialog no longer needed
        } // end method onClick
    }; // end setColorButtonListener

    // display a dialog for setting the line width
    private void showLineWidthDialog() {

        // create the dialog and inflate its content
        currentDialog = new Dialog(this);
        currentDialog.setContentView(R.layout.width_dialog);
        currentDialog.setTitle(R.string.title_line_width_dialog);
        currentDialog.setCancelable(true);

        // get widthSeekBar and configure it
        SeekBar widthSeekBar = (SeekBar) currentDialog
                .findViewById(R.id.widthSeekBar);
        widthSeekBar.setOnSeekBarChangeListener(widthSeekBarChanged);
        widthSeekBar.setProgress(doodleView.getLineWidth());

        // set the Set Line Width Button's onClickListener
        Button setLineWidthButton = (Button) currentDialog
                .findViewById(R.id.widthDialogDoneButton);
        setLineWidthButton.setOnClickListener(setLineWidthButtonListener);

        dialogIsVisible.set(true); // dialog is on the screen
        currentDialog.show(); // show the dialog
    } // end method showLineWidthDialog

    // OnSeekBarChangeListener for the SeekBar in the width dialog
    private SeekBar.OnSeekBarChangeListener widthSeekBarChanged =
            new SeekBar.OnSeekBarChangeListener() {

        Bitmap bitmap = Bitmap.createBitmap( // create Bitmap
                400, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);// associate with Canvas

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

            // get the ImageView
            ImageView widthImageView = (ImageView) currentDialog
                    .findViewById(R.id.widthImageView);

            // configure a Paint object for the current SeekBar value
            Paint p = new Paint();
            p.setColor(doodleView.getDrawingColor());
            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStrokeWidth(progress);

            // erase the bitmap and redraw the line
            bitmap.eraseColor(Color.WHITE);
            canvas.drawLine(30, 50, 370, 50, p);
            widthImageView.setImageBitmap(bitmap);
        } // end method onProgressChanged

        //required method of interface OnSeekBarChangeListener
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        } // end method onStartTrackingTouch

        // required method of interface OnSeekBarChangeListener
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        } // end method onStopTrackingTouch
    }; // end widthSeekBarChanged

    // OnClickListener for the line width dialog's Set Line Width Button
    private View.OnClickListener setLineWidthButtonListener =
            new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            // get the color SeekBars
            SeekBar widthSeekBar =
                    (SeekBar) currentDialog.findViewById(R.id.widthSeekBar);

            // set line color
            doodleView.setLineWidth(widthSeekBar.getProgress());
            dialogIsVisible.set(false); // dialog is not on the screen
            currentDialog.dismiss(); // hide the dialog
            currentDialog = null; // dialog no longer needed
        } // end method onClick
    };// end setColorButtonListener
} // end class Doodlz

