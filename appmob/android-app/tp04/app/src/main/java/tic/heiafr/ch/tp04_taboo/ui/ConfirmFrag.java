package tic.heiafr.ch.tp04_taboo.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import tic.heiafr.ch.tp04_taboo.R;
import tic.heiafr.ch.tp04_taboo.taboo.MyAdapter;
import tic.heiafr.ch.tp04_taboo.taboo.Taboo;
import tic.heiafr.ch.tp04_taboo.taboo.TabooManager;

public class ConfirmFrag extends Fragment {

    // Items for RecycleView
    List<Taboo> cardsPLayed;
    RecyclerView recyclerView;
    TabooManager tabooManager;

    private View view;

    public ConfirmFrag() {
        super(R.layout.activity_confirm);
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        tabooManager = TabooManager.getInstance(null);

        cardsPLayed = tabooManager.getPlayedThisRound();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstace) {
        view = inflater.inflate(R.layout.activity_confirm, container, false);
        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        MyAdapter myAdapter = new MyAdapter(cardsPLayed);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_confirm, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        tabooManager.validateTurn();

        if (tabooManager.isGameOver()) {
            Navigation.findNavController(view).navigate(R.id.action_confirmFrag_to_endFrag);
        } else {
            ConfirmFragDirections.ActionConfirmFragToPlayFrag action = ConfirmFragDirections.actionConfirmFragToPlayFrag(false, new String[0], -1, -1, -1, -1, false);
            Navigation.findNavController(view).navigate(action);
        }
        return true;
    }
}
