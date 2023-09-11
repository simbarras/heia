package ch.heiafr.tic.simon.tp1_diceroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final int DICE_NUMBER = 6;
    private final int MIN_ACCELERATION = 3;
    private final int SAMPLING_PERIOD = 10;

    private final Random random = new Random();
    private ImageView vDice1;
    private ImageView vDice2;
    private Button btnDice1;
    private Button btnDice2;

    private double currentAccelerationValue;
    private double previousAccelerationValue;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            previousAccelerationValue = currentAccelerationValue;
            currentAccelerationValue = Math.sqrt(x * x + y * y + z * z);
            if(Math.abs(currentAccelerationValue-previousAccelerationValue) > MIN_ACCELERATION) rollDice();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vDice1 = findViewById(R.id.imVDice1);
        vDice2 = findViewById(R.id.imVDice2);

        btnDice1 = findViewById(R.id.btnDice1);
        disableButton(btnDice1, true);
        btnDice2 = findViewById(R.id.btnDice2);
        disableButton(btnDice2, false);

        Button roll = findViewById(R.id.btnPlay);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        btnDice1.setOnClickListener(view -> display2Dice(false));
        btnDice2.setOnClickListener(view -> display2Dice(true));


        /* New (start) */
        roll.setOnClickListener(view -> rollDice());
        /* New (end) */
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SAMPLING_PERIOD);
    }

    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

    private void rollDice(){
        int value = random.nextInt(DICE_NUMBER) + 1;
        switch (value) {
            case 1:
                vDice1.setImageResource(R.drawable.dice_1);
                break;
            case 2:
                vDice1.setImageResource(R.drawable.dice_2);
                break;
            case 3:
                vDice1.setImageResource(R.drawable.dice_3);
                break;
            case 4:
                vDice1.setImageResource(R.drawable.dice_4);
                break;
            case 5:
                vDice1.setImageResource(R.drawable.dice_5);
                break;
            case 6:
                vDice1.setImageResource(R.drawable.dice_6);
                break;
            default:
                vDice1.setImageResource(R.drawable.dice_empty);
                break;
        }
        if (vDice2.getVisibility() == View.VISIBLE) {
            value = random.nextInt(DICE_NUMBER) + 1;
            switch (value) {
                case 1:
                    vDice2.setImageResource(R.drawable.dice_1);
                    break;
                case 2:
                    vDice2.setImageResource(R.drawable.dice_2);
                    break;
                case 3:
                    vDice2.setImageResource(R.drawable.dice_3);
                    break;
                case 4:
                    vDice2.setImageResource(R.drawable.dice_4);
                    break;
                case 5:
                    vDice2.setImageResource(R.drawable.dice_5);
                    break;
                case 6:
                    vDice2.setImageResource(R.drawable.dice_6);
                    break;
                default:
                    vDice2.setImageResource(R.drawable.dice_empty);
                    break;
            }
        }
    }

    private void display2Dice(boolean display) {
        if (display) {
            vDice2.setVisibility(View.VISIBLE);
            disableButton(btnDice1, false);
            disableButton(btnDice2, true);
        } else {
            vDice2.setVisibility(View.GONE);
            disableButton(btnDice1, true);
            disableButton(btnDice2, false);
        }
    }

    private void disableButton(Button btn, boolean disable) {
        btn.setClickable(!disable);
        ColorStateList color = btn.getResources().getColorStateList(R.color.purple_500);
        if (disable)
            color = btn.getResources().getColorStateList(R.color.material_on_primary_disabled);
        btn.setBackgroundTintList(color);

    }
}