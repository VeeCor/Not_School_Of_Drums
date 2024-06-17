package com.example.notschoolofdrums.Other;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FirestoreUpdater extends Worker {
    SimpleDateFormat dateFormat;
    String dateStr;
    String[] times = {"11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00"};
    FirebaseFirestore db;

    public FirestoreUpdater(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try{
            db = FirebaseFirestore.getInstance();
            dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();

            dateStr = dateFormat.format(calendar.getTime());
            for (int i = 1; i <= 10; i++) {
                for (String time : times) {
                    db.collection("dates").document(dateStr).collection(String.valueOf(i)).document(time)
                            .delete()
                            .addOnSuccessListener(aVoid -> Log.d("WorkManager", "Документ удален"))
                            .addOnFailureListener(e -> Log.e("WorkManager", "Документ не удален. Ошибка: " + e));
                }
            }
            db.collection("dates").document(dateStr).delete();

            calendar.add(Calendar.DAY_OF_YEAR, 22);
            dateStr = dateFormat.format(calendar.getTime());

            Map<String, Object> data = new HashMap<>();
            data.put("places", 4);

            for (int i = 1; i <= 10; i++) {
                for (String time : times) {
                    db.collection("dates").document(dateStr).collection(String.valueOf(i)).document(time).set(data);
                }
            }

            return Result.success();
        } catch (Exception e){
            return Result.failure();
        }
    }
}
