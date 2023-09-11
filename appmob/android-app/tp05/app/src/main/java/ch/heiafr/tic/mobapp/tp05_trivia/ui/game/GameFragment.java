package ch.heiafr.tic.mobapp.tp05_trivia.ui.game;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.app.Application;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.heiafr.tic.mobapp.tp05_trivia.R;
import ch.heiafr.tic.mobapp.tp05_trivia.databinding.FragmentGameBinding;
import ch.heiafr.tic.mobapp.tp05_trivia.model.trivia.TriviaRepository;

/**
 * Fragment handling the game view
 */
public class GameFragment extends Fragment {

    //=== Attributes

    private FragmentGameBinding binding;  // binding object
    private GameViewModel gameViewModel;


    //=== Fragment's overrides
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    // Inflate the layout using DataBinding and keep a binding object reference which is used to directly access the views.
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false);
        return binding.getRoot();
    }

    // Fragment's logic. Bind views to data.
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int mode = GameFragmentArgs.fromBundle(getArguments()).getMode();
        int categoryID = GameFragmentArgs.fromBundle(getArguments()).getId();
        // creates a unique instance of the GameViewModel class
        gameViewModel = new ViewModelProvider(this, new GameViewModelFactory(getActivity().getApplication(), mode, categoryID)).get(GameViewModel.class);

        // sets data binding to automatically update the views when a LiveData variable of the ViewModel changes.
        binding.setViewModel(gameViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        OnGameEvents listener = view1 -> {

            switch (view1.getId()) {
                case R.id.card1:
                    gameViewModel.getCurrentQuestion().getValue().setGivenAnswer(0);
                    showResult(binding.answer1);
                    break;
                case R.id.card2:
                    gameViewModel.getCurrentQuestion().getValue().setGivenAnswer(1);
                    showResult(binding.answer2);
                    break;
                case R.id.card3:
                    gameViewModel.getCurrentQuestion().getValue().setGivenAnswer(2);
                    showResult(binding.answer3);
                    break;
                case R.id.card4:
                    gameViewModel.getCurrentQuestion().getValue().setGivenAnswer(3);
                    showResult(binding.answer4);
                    break;
            }
            binding.setListener(null);
            gameViewModel.startCountdown();
        };

        gameViewModel.getCurrentQuestion().observe(getViewLifecycleOwner(), question -> {
            this.binding.gameRoot.setVisibility(View.VISIBLE);
            this.binding.answer1.setBackgroundColor(Color.TRANSPARENT);
            this.binding.answer2.setBackgroundColor(Color.TRANSPARENT);
            this.binding.answer3.setBackgroundColor(Color.TRANSPARENT);
            this.binding.answer4.setBackgroundColor(Color.TRANSPARENT);
            this.binding.setListener(listener);

            if (question.getDifficulty().equals(TriviaRepository.EASY)) {
                this.binding.difficulty.setBackgroundColor(getResources().getColor(R.color.colorEasy));
            } else if (question.getDifficulty().equals(TriviaRepository.MEDIUM)) {
                this.binding.difficulty.setBackgroundColor(getResources().getColor(R.color.colorMedium));
            } else {
                this.binding.difficulty.setBackgroundColor(getResources().getColor(R.color.colorHard));
            }
        });

        gameViewModel.isGameEnded().observe(getViewLifecycleOwner(), gameEnd -> {
            if (gameEnd) {
                NavDirections action = GameFragmentDirections.actionGameFragmentToResultFragment();
                Navigation.findNavController(view).navigate(action);
            }
        });

        binding.setListener(listener);
    }

    public static class GameViewModelFactory implements ViewModelProvider.Factory {
        private final Application application;
        private final int mode;
        private final int categoryID;

        public GameViewModelFactory(Application application, int mode, int categoryID) {
            this.application = application;
            this.mode = mode;
            this.categoryID = categoryID;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(GameViewModel.class))
                return (T) new GameViewModel(this.application, this.mode, this.categoryID);
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

    private void showResult(TextView textView) {
        int colorRight = getResources().getColor(R.color.colorRight);
        int colorWrong = getResources().getColor(R.color.colorWrong);
        if (gameViewModel.getCurrentQuestion().getValue().isCorrect()) {
            textView.setBackgroundColor(colorRight);
            gameViewModel.incrementCorrect();
        } else {
            textView.setBackgroundColor(colorWrong);
            switch (gameViewModel.getCurrentQuestion().getValue().getCorrectAnswer()) {
                case 0:
                    binding.answer1.setBackgroundColor(colorRight);
                    break;
                case 1:
                    binding.answer2.setBackgroundColor(colorRight);
                    break;
                case 2:
                    binding.answer3.setBackgroundColor(colorRight);
                    break;
                case 3:
                    binding.answer4.setBackgroundColor(colorRight);
                    break;
            }
        }
    }


    public interface OnGameEvents {
        void onAnswerClicked(View view);
    }
}
