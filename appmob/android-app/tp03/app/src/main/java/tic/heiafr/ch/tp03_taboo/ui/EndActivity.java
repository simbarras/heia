package tic.heiafr.ch.tp03_taboo.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import tic.heiafr.ch.tp03_taboo.R;
import tic.heiafr.ch.tp03_taboo.taboo.TabooManager;
import tic.heiafr.ch.tp03_taboo.taboo.Team;

/**
 * Activity charged to show the results of the finished taboo game
 *
 * @author Rafic Galli
 * @version 30.04.2020
 * @since 1.0
 */
public class EndActivity extends AppCompatActivity {

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        TabooManager tabooManager = TabooManager.getInstance(null);

        TextView goldTeamText = findViewById(R.id.team_gold);
        TextView goldScoreText = findViewById(R.id.score_gold);
        TextView silverTeamText = findViewById(R.id.team_silver);
        TextView silverScoreText = findViewById(R.id.score_silver);
        TextView bronzeTeamText = findViewById(R.id.team_bronze);
        TextView bronzeScoreText = findViewById(R.id.score_bronze);
        TextView failTeamText = findViewById(R.id.team_fail);
        TextView failScoreText = findViewById(R.id.score_fail);
        ImageView medal_bronze = findViewById(R.id.medal_bronze);
        ImageView medal_fail = findViewById(R.id.medal_fail);

        List<Team> teams = tabooManager.getTeams();

        goldTeamText.setText(getString(R.string.playing_team, teams.get(0).getName()));
        goldScoreText.setText(getString(R.string.score, teams.get(0).getScore()));

        silverTeamText.setText(getString(R.string.playing_team, teams.get(1).getName()));
        silverScoreText.setText(getString(R.string.score, teams.get(1).getScore()));

        if (teams.size() >= 3) {
            setVisibility(new View[]{medal_bronze, bronzeTeamText, bronzeScoreText}, true);
            bronzeTeamText.setText(getString(R.string.playing_team, teams.get(2).getName()));
            bronzeScoreText.setText(getString(R.string.score, teams.get(2).getScore()));
        } else {
            setVisibility(new View[]{medal_bronze, bronzeTeamText, bronzeScoreText}, false);
            setVisibility(new View[]{medal_fail, failTeamText, failScoreText}, false);
        }

        if (teams.size() >= 4) {
            setVisibility(new View[]{medal_fail, failTeamText, failScoreText}, true);
            failTeamText.setText(getString(R.string.playing_team, teams.get(3).getName()));
            failScoreText.setText(getString(R.string.score, teams.get(3).getScore()));
        } else {
            setVisibility(new View[]{medal_fail, failTeamText, failScoreText}, false);
        }
    }

    private void setVisibility(View[] items, boolean visible) {
        for (View elt : items) {
            if (visible) {
                elt.setVisibility(View.VISIBLE);
            } else {
                elt.setVisibility(View.GONE);
            }
        }
    }

    /**
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
