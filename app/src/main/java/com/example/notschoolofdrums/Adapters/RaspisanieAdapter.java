package com.example.notschoolofdrums.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notschoolofdrums.R;

import java.util.List;

public class RaspisanieAdapter extends RecyclerView.Adapter<RaspisanieAdapter.RaspisanieViewHolder> {

    private List<RaspisanieForAdapter> raspisanieList;

    public RaspisanieAdapter(List<RaspisanieForAdapter> raspisanieList) {
        this.raspisanieList = raspisanieList;
    }

    @NonNull
    @Override
    public RaspisanieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raspisanie_card, parent, false);
        return new RaspisanieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RaspisanieViewHolder holder, int position) {
        RaspisanieForAdapter raspisanie = raspisanieList.get(position);
        holder.date.setText(raspisanie.getDate());
        holder.class11.setText(raspisanie.getClass11());
        holder.class13.setText(raspisanie.getClass13());
        holder.class15.setText(raspisanie.getClass15());
        holder.class17.setText(raspisanie.getClass17());
        holder.class19.setText(raspisanie.getClass19());
        holder.class21.setText(raspisanie.getClass21());
        holder.students11.setText(raspisanie.getStudents11());
        holder.students13.setText(raspisanie.getStudents13());
        holder.students15.setText(raspisanie.getStudents15());
        holder.students17.setText(raspisanie.getStudents17());
        holder.students19.setText(raspisanie.getStudents19());
        holder.students21.setText(raspisanie.getStudents21());
    }

    @Override
    public int getItemCount() {
        return raspisanieList.size();
    }

    public static class RaspisanieViewHolder extends RecyclerView.ViewHolder {
        TextView date, class11, class13, class15, class17, class19, class21,
                students11, students13, students15, students17, students19, students21,
                time11, time13, time15, time17, time19, time21;

        public RaspisanieViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            class11 = itemView.findViewById(R.id.class11);
            class13 = itemView.findViewById(R.id.class13);
            class15 = itemView.findViewById(R.id.class15);
            class17 = itemView.findViewById(R.id.class17);
            class19 = itemView.findViewById(R.id.class19);
            class21 = itemView.findViewById(R.id.class21);
            students11 = itemView.findViewById(R.id.students11);
            students13 = itemView.findViewById(R.id.students13);
            students15 = itemView.findViewById(R.id.students15);
            students17 = itemView.findViewById(R.id.students17);
            students19 = itemView.findViewById(R.id.students19);
            students21 = itemView.findViewById(R.id.students21);
            time11 = itemView.findViewById(R.id.time11);
            time13 = itemView.findViewById(R.id.time13);
            time15 = itemView.findViewById(R.id.time15);
            time17 = itemView.findViewById(R.id.time17);
            time19 = itemView.findViewById(R.id.time19);
            time21 = itemView.findViewById(R.id.time21);
        }
    }
}
