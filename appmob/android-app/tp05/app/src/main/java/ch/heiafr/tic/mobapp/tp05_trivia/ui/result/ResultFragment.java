package ch.heiafr.tic.mobapp.tp05_trivia.ui.result;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ch.heiafr.tic.mobapp.tp05_trivia.R;
import ch.heiafr.tic.mobapp.tp05_trivia.databinding.FragmentResultBinding;

/**
 * Fragment handling the result screen
 */
public class ResultFragment extends Fragment {

    //=== Attributes

    private FragmentResultBinding binding;  // binding object


    //=== Fragment's overrides

    // Inflate the layout using DataBinding and keep a binding object reference which is used to directly access the views.
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_result, container, false);
        return binding.getRoot();
    }

    // Fragment's logic. Bind views to data.
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // creates a unique instance of the ResultViewModel class
        ResultViewModel resultViewModel = new ViewModelProvider(this).get(ResultViewModel.class);

        // sets data binding to automatically update the views when a LiveData variable of the ViewModel changes.
        binding.setViewModel(resultViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
    }
}
