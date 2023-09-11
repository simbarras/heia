package ch.heiafr.tic.mobapp.tp05_trivia.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ch.heiafr.tic.mobapp.tp05_trivia.R;
import ch.heiafr.tic.mobapp.tp05_trivia.databinding.FragmentHomeBinding;
import ch.heiafr.tic.mobapp.tp05_trivia.databinding.FragmentResultBinding;
import ch.heiafr.tic.mobapp.tp05_trivia.model.trivia.TriviaRepository;

import static androidx.navigation.fragment.NavHostFragment.findNavController;

/**
 * Fragment handling the home screen
 */
public class HomeFragment extends Fragment {

    // === Attributes
    private FragmentHomeBinding binding;

    //=== Fragment's override

    // Tell the system that this fragment has its own menu.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    // Inflate the layout using DataBinding and keep a binding object reference which is used to directly access the views.
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: To implement
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return binding.getRoot();
    }

    // Fragment's logic. Bind views to data and handle navigation.
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: To implement and modify
        RecyclerView recyclerView = binding.categories;

        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding.setViewModel(homeViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        // set the adapter and handle click item callback
        final HomeAdapter adapter = new HomeAdapter(new ArrayList<>(),
                (view1, category) -> {
                    // navigate to the game view and pass the category id using SafeArgs
                    NavDirections action = HomeFragmentDirections.actionHomeFragmentToGameFragment(TriviaRepository.MODE_GAME, category.getId());
                    Navigation.findNavController(view1).navigate(action);
                });
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        homeViewModel.getCategories().observe(getViewLifecycleOwner(), adapter::updateItems);
    }


    //=== Fragment's override

    // Inflate the menu displayed in the toolbar.
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_home, menu);
    }

    // Handles the action when an option menu is selected. Navigate to the correct destination.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavDirections action;
        switch (item.getItemId()) {
            case R.id.action_history:
                action = HomeFragmentDirections.actionHomeFragmentToHistoryFragment();
                findNavController(this).navigate(action);
                return true;
            case R.id.action_profile:
                action = HomeFragmentDirections.actionHomeFragmentToProfileFragment();
                findNavController(this).navigate(action);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
