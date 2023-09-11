package tic.heiafr.ch.tp04_taboo.taboo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tic.heiafr.ch.tp04_taboo.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    List<Taboo> cardsPlayed;

    public MyAdapter (List<Taboo> cardsPLayed) {
        this.cardsPlayed = cardsPLayed;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder,final int position) {
        if (position > cardsPlayed.size()){
            return;
        }
        holder.myTxt.setText(cardsPlayed.get(position).getWord());
        if (cardsPlayed.get(position).getState() != null) {
            switch (cardsPlayed.get(position).getState()) {
                case PASSED:
                    holder.myView.setImageResource(R.drawable.ic_passed);
                    break;

                case FAILED:
                    holder.myView.setImageResource(R.drawable.ic_wrong);
                    break;

                case WON:
                    holder.myView.setImageResource(R.drawable.ic_win);
                    break;

                default:
                    holder.myView.setImageResource(R.drawable.ic_passed);
                    break;
            }
        } else {
            holder.myView.setImageResource(R.drawable.ic_wrong);
        }
    }

    @Override
    public int getItemCount() {
        return cardsPlayed.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView myTxt;
        ImageView myView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myView = itemView.findViewById(R.id.descriptionImage);
            myTxt = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}
