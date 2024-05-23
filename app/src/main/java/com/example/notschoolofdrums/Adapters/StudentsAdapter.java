package com.example.notschoolofdrums.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notschoolofdrums.R;

import java.util.ArrayList;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.MyViewHolder> {
    Context context;
    ArrayList<Students> studentsArrayList;

    public StudentsAdapter(Context context, ArrayList<Students> studentsArrayList) {
        this.context = context;
        this.studentsArrayList = studentsArrayList;
    }

//TODO: завершить функцию поиска
    public void setFilteredList (ArrayList<Students> filteredList){
        this.studentsArrayList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Students student = studentsArrayList.get(position);
        holder.username.setText(student.getUsername());
    }

    @Override
    public int getItemCount() {
        return studentsArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.student_name);
        }
    }

    public void addStudent(Students student) {
        studentsArrayList.add(student);
        notifyItemInserted(studentsArrayList.size() - 1);
    }
}
