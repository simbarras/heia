package ch.heiafr.tic.mobapp.tp05_trivia.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.heiafr.tic.mobapp.tp05_trivia.databinding.ItemCategoryBinding;
import ch.heiafr.tic.mobapp.tp05_trivia.model.data.Category;
import ch.heiafr.tic.mobapp.tp05_trivia.model.data.Question;

/**
 * Adapter handling the home screen
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.CategoryViewHolder> {

    //=== Attributes

    private List<Category> items;                   // list of items to display
    private final OnHomeAdapterEvents listener;     // item click listener callback
    private ItemCategoryBinding binding;

    //=== Constructors

    public HomeAdapter(List<Category> items, OnHomeAdapterEvents listener) {
        this.items = items;
        this.listener = listener;
    }


    //=== RecyclerView's overrides

    // Inflate the holder of views for an item using data binding.
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // TODO: To implement
        binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }

    // Bind the data (model) to the views. Called once per item in the list of items.
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = items.get(position);
        holder.bind(category, listener);
    }

    // Returns the total number of items in the data set held by the adapter.
    @Override
    public int getItemCount() {
        return items.size();
    }


    //=== Public methods

    // Update the RecyclerView when the model (id est items) has changed.
    public void updateItems(List<Category> items) {
        this.items = items;
        notifyDataSetChanged();
    }


    //=== ViewHolders

    /**
     * Holder handling the view of a single item
     */
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        // TODO: To implement
        private ItemCategoryBinding binding;


        // Create a CategoryViewHolder object using DataBinding
        // TODO: To implement and modify
        public CategoryViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        // Bind the data to the views using DataBinding. Set content of a list item.
        public void bind(Category category, OnHomeAdapterEvents listener) {
            // TODO: To implement
            binding.setCategory(category);
            binding.setListener(listener);

        }
    }


    //=== Interfaces

    /**
     * Interface handling events on the list
     */
    public interface OnHomeAdapterEvents {

        // Called when an item of the list has been clicked.
        void onCategoryClicked(View view, Category category);
    }
}
