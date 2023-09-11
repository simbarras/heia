package tic.heiafr.ch.tp04_taboo.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import javax.xml.namespace.NamespaceContext;

import tic.heiafr.ch.tp04_taboo.R;
import tic.heiafr.ch.tp04_taboo.taboo.TabooManager;
import tic.heiafr.ch.tp04_taboo.taboo.Team;

/**
 * Activity charged to show the results of the finished taboo game
 *
 * @author Rafic Galli
 * @version 30.04.2020
 * @since 1.0
 */
public class EndFrag extends Fragment {

    private TabooManager tabooManager;
    private View view;

    public EndFrag(){
        super(R.layout.activity_end);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        tabooManager = TabooManager.getInstance(null);
    }


    /**
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_end, container, false);

        TextView goldTeamText = view.findViewById(R.id.team_gold);
        TextView goldScoreText = view.findViewById(R.id.score_gold);
        TextView silverTeamText = view.findViewById(R.id.team_silver);
        TextView silverScoreText = view.findViewById(R.id.score_silver);
        TextView bronzeTeamText = view.findViewById(R.id.team_bronze);
        TextView bronzeScoreText = view.findViewById(R.id.score_bronze);
        TextView failTeamText = view.findViewById(R.id.team_fail);
        TextView failScoreText = view.findViewById(R.id.score_fail);
        ImageView medal_bronze = view.findViewById(R.id.medal_bronze);
        ImageView medal_fail = view.findViewById(R.id.medal_fail);

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
        return view;
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

    public void onBackPressed() {
        Navigation.findNavController(view).navigate(R.id.action_endFrag_to_homeFrag);
    }

}
