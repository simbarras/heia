package tic.heiafr.ch.tp03_taboo.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.List;

import tic.heiafr.ch.tp03_taboo.R;
import tic.heiafr.ch.tp03_taboo.taboo.MyAdapter;
import tic.heiafr.ch.tp03_taboo.taboo.Taboo;
import tic.heiafr.ch.tp03_taboo.taboo.TabooManager;

public class ConfirmActivity extends AppCompatActivity {

    // Items for RecycleView
    List<Taboo> cardsPLayed;
    RecyclerView recyclerView;
    TabooManager tabooManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        tabooManager = TabooManager.getInstance(null);

        cardsPLayed = tabooManager.getPlayedThisRound();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        MyAdapter myAdapter = new MyAdapter(cardsPLayed);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        tabooManager.validateTurn();

        if (tabooManager.isGameOver()) {
            startActivity(new Intent(this, EndActivity.class));
        } else {
            Intent intent = new Intent(this, PlayActivity.class);
            intent.putExtra(HomeActivity.CODE_NEW_GAME, false);
            startActivity(intent);
        }
        return true;
    }
}
