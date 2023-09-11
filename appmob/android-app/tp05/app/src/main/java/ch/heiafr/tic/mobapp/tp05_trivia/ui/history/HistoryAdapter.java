package ch.heiafr.tic.mobapp.tp05_trivia.ui.history;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.heiafr.tic.mobapp.tp05_trivia.model.data.Question;

/**
 * Adapter handling the history screen
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.QuestionViewHolder> {

    //=== Attributes

    private List<Question> items;                       // list of items to display
    private final OnHistoryAdapterEvents listener;      // item click listener callback


    // Constructors

    // Create a HistoryAdapter object.
    public HistoryAdapter(List<Question> items, OnHistoryAdapterEvents listener) {
        this.items = items;
        this.listener = listener;
    }


    //=== Recyclerview's overrides

    // Inflate the holder of views for an item using data binding.
    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // TODO: To implement
        return null;
    }

    // Bind the data (model) to the views. Called once per item in the list of items.
    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = items.get(position);
        holder.bind(question, listener);
    }

    // Returns the total number of items in the data set held by the adapter.
    @Override
    public int getItemCount() {
        return items.size();
    }


    //=== Public methods

    // Update the RecyclerView when the model (id est items) has changed.
    public void updateItems(List<Question> items) {
        this.items = items;
        notifyDataSetChanged();
    }


    //===  ViewHolders

    /**
     * ViewHolder handling the view of a single item
     */
    public static class QuestionViewHolder extends RecyclerView.ViewHolder {

        // TODO: To implement

        // Create a HistoryViewHolder object using DataBinding
        // TODO: To implement and modify
        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        // Bind the data to the views using DataBinding. Set content of a list item.
        public void bind(Question question, OnHistoryAdapterEvents listener) {
            // TODO: To implement
        }
    }


    //=== Interfaces

    /**
     * Interface handling the events on the list
     */
    public interface OnHistoryAdapterEvents {

        // Called when an item of the list has been clicked.
        void onQuestionClicked(View view, Question question);
    }
}
