package com.example.notschoolofdrums.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notschoolofdrums.R;

import java.util.ArrayList;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {
    private final ArrayList<EntriesForAdapter> entriesList;

    public EntryAdapter(ArrayList<EntriesForAdapter> entriesList) {
        this.entriesList = entriesList;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lessons_card, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        EntriesForAdapter entry = entriesList.get(position);
        holder.type.setText(entry.getType());
        holder.teacherFio.setText(entry.getUsername());
        holder.classroomNum.setText(entry.getChosenClass());
        holder.consistValue.setText(entry.getConsist());
        holder.levelNum.setText(entry.getLevel());
        holder.lessonNum.setText(entry.getLesson());
        holder.lessonTime.setText(entry.getTime());
        holder.lessonDate.setText(entry.getDate());

        if (entry.getType().equals("Репетиция")){
            holder.teacher.setVisibility(View.GONE);
            holder.teacherFio.setVisibility(View.GONE);
            holder.consist.setVisibility(View.GONE);
            holder.consistValue.setVisibility(View.GONE);
            holder.levelNum.setVisibility(View.GONE);
            holder.lessonNum.setVisibility(View.GONE);
            holder.level.setVisibility(View.GONE);
        }
        if (entry.getType().equals("Занятие")){
            holder.teacher.setVisibility(View.VISIBLE);
            holder.teacherFio.setVisibility(View.VISIBLE);
            holder.consist.setVisibility(View.VISIBLE);
            holder.consistValue.setVisibility(View.VISIBLE);
            holder.levelNum.setVisibility(View.VISIBLE);
            holder.lessonNum.setVisibility(View.VISIBLE);
            holder.level.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return entriesList.size();
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView teacherFio, classroomNum, consistValue, levelNum, lessonNum, lessonTime, lessonDate, teacher, consist, type, level;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            teacherFio = itemView.findViewById(R.id.teacher_fio);
            classroomNum = itemView.findViewById(R.id.classroom_num);
            consistValue = itemView.findViewById(R.id.consist_value);
            levelNum = itemView.findViewById(R.id.level_num);
            lessonNum = itemView.findViewById(R.id.lesson_num);
            lessonTime = itemView.findViewById(R.id.lesson_time);
            lessonDate = itemView.findViewById(R.id.lesson_date);
            teacher = itemView.findViewById(R.id.teacher);
            consist = itemView.findViewById(R.id.consist);
            type = itemView.findViewById(R.id.lesson);
            level = itemView.findViewById(R.id.level);
        }
    }
}
