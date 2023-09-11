package ch.heiafr.tic.mobapp.tp05_trivia.ui.history;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ch.heiafr.tic.mobapp.tp05_trivia.model.trivia.TriviaRepository;

/**
 * Fragment handling the history screen
 */
public class HistoryFragment extends Fragment {

    //=== Fragment's overrides

    // Inflate the layout using DataBinding and keep a binding object reference which is used to directly access the views.
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO: To implement
        return null;
    }

    // Fragment's logic. Bind views to data and handle navigation.
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: To implement

        // set the adapter and handle click item callback
        final HistoryAdapter adapter = new HistoryAdapter(new ArrayList<>(),
                (view1, question) -> {
                    // navigate to the game view and pass the category id using SafeArgs
                    NavDirections action = HistoryFragmentDirections.actionHistoryFragmentToGameFragment(TriviaRepository.MODE_HISTORY, question.getId());
                    Navigation.findNavController(view1).navigate(action);
                });
    }
}
