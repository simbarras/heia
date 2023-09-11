package ch.heiafr.tic.sebastiano.ricchiuti.tp01_diceroler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final int DICE_NUMBER = 6;              // New
    private final Random random = new Random();
    private ImageView vDice;
    private ImageView vDice2;
    private boolean twoDice = false;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vDice = findViewById(R.id.imageView);
        vDice2 = findViewById(R.id.imageView2);
        Button roll = findViewById(R.id.button);
        Button btn1 = findViewById(R.id.button2);
        Button btn2 = findViewById(R.id.button3);
        Space space = findViewById(R.id.space1);
        linearLayout = findViewById(R.id.linearLayout1);

        vDice2.setVisibility(View.GONE);
        space.setVisibility(View.GONE);

        roll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Button pressed", "Button Roll");
                int value = random.nextInt(DICE_NUMBER) + 1;
                int value2 = random.nextInt(DICE_NUMBER) + 1;

                switch (value) {
                    case 1:
                        vDice.setImageResource(R.drawable.ic_dice_1);
                        break;
                    case 2:
                        vDice.setImageResource(R.drawable.ic_dice_2);
                        break;
                    case 3:
                        vDice.setImageResource(R.drawable.ic_dice_3);
                        break;
                    case 4:
                        vDice.setImageResource(R.drawable.ic_dice_4);
                        break;
                    case 5:
                        vDice.setImageResource(R.drawable.ic_dice_5);
                        break;
                    case 6:
                        vDice.setImageResource(R.drawable.ic_dice_6);
                        break;
                    default:
                        vDice.setImageResource(R.drawable.ic_dice_empty);
                        break;
                }
                if (twoDice) {
                    switch (value2) {
                        case 1:
                            vDice2.setImageResource(R.drawable.ic_dice_1);
                            break;
                        case 2:
                            vDice2.setImageResource(R.drawable.ic_dice_2);
                            break;
                        case 3:
                            vDice2.setImageResource(R.drawable.ic_dice_3);
                            break;
                        case 4:
                            vDice2.setImageResource(R.drawable.ic_dice_4);
                            break;
                        case 5:
                            vDice2.setImageResource(R.drawable.ic_dice_5);
                            break;
                        case 6:
                            vDice2.setImageResource(R.drawable.ic_dice_6);
                            break;
                        default:
                            vDice.setImageResource(R.drawable.ic_dice_empty);
                            break;
                    }
                }
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Button pressed", "Button1");
                twoDice = false;
                vDice2.setVisibility(View.GONE);
                space.setVisibility(View.GONE);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Button pressed", "Button2");
                twoDice = true;
                vDice2.setVisibility(View.VISIBLE);
                space.setVisibility(View.VISIBLE);
            }
        });
    }
}