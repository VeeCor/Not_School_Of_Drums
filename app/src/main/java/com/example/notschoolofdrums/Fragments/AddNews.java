package com.example.notschoolofdrums.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.notschoolofdrums.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class AddNews extends Fragment {

    ImageButton close;
    FloatingActionButton addImageButton;
    CardView MainImageLoadingCard, ImageLoadingCard;
    ImageView imageLoading;
    TextView textLoadingCard, sendNews;
    EditText postText;
    ProgressBar progressBar;
    ImageButton clearImageButton;
    Uri imageUri, downloadUri;
    private float dY, initialY, screenHeight;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_news, container, false);

        Animation slideInBottomAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_bottom);
        view.startAnimation(slideInBottomAnimation);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        close = view.findViewById(R.id.close_item);
        addImageButton = view.findViewById(R.id.floatingActionButton);
        MainImageLoadingCard = view.findViewById(R.id.main_image_loading_card);
        ImageLoadingCard = view.findViewById(R.id.image_loading_card);
        imageLoading = view.findViewById(R.id.image_loading);
        textLoadingCard = view.findViewById(R.id.text_loading_card);
        progressBar = view.findViewById(R.id.progress_bar);
        clearImageButton = view.findViewById(R.id.clear_image_button);
        sendNews = view.findViewById(R.id.send_news);
        postText = view.findViewById(R.id.post_text);

        close.setOnClickListener(v -> CloseFragment());

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        DeleteImage();
                        Intent image = result.getData();
                        assert image != null;
                        imageUri = image.getData();
                        imageLoading.setImageURI(imageUri);
                        textLoadingCard.setText(String.valueOf(imageUri));

                        MainImageLoadingCard.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.VISIBLE);

                        new Handler().postDelayed(() -> {
                            progressBar.setVisibility(View.GONE);
                            ImageLoadingCard.setVisibility(View.VISIBLE);
                            textLoadingCard.setVisibility(View.VISIBLE);
                            clearImageButton.setVisibility(View.VISIBLE);
                        }, 1500);
                    }
                }
        );

        addImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            someActivityResultLauncher.launch(intent);
        });

        clearImageButton.setOnClickListener(v -> DeleteImage());

        sendNews.setOnClickListener(v -> {
            if (!(postText.getText().toString().isEmpty()) || MainImageLoadingCard.getVisibility() == View.VISIBLE){
                String text = postText.getText().toString();
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+03:00"));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+03:00"));
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                timeFormat.setTimeZone(TimeZone.getTimeZone("GMT+03:00"));

                String currentDate = dateFormat.format(calendar.getTime());
                String currentTime = timeFormat.format(calendar.getTime());

                Map<String, Object> news = new HashMap<>();
                if (!(postText.getText().toString().isEmpty())){
                    news.put("text", text);
                }
                news.put("datetime", currentDate + " " + currentTime);

                if (MainImageLoadingCard.getVisibility() == View.VISIBLE){
                    String filename = UUID.randomUUID().toString() + ".jpg";
                    StorageReference imageRef = storageRef.child("images/" + filename);
                    UploadTask uploadTask = imageRef.putFile(imageUri);

                    uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            downloadUri = task.getResult();
                            news.put("imageURI", downloadUri);

                            db.collection("news")
                                    .add(news)
                                    .addOnSuccessListener(documentReference -> CloseFragment())
                                    .addOnFailureListener(documentReference -> Log.e("FirebaseFirestore", "Error adding document", documentReference));

                            Log.d("FirebaseStorage", "Download URI: " + downloadUri.toString());
                        } else {
                            Log.e("FirebaseStorage", "Failed to get download URI", task.getException());
                        }
                    })).addOnFailureListener(exception -> Log.e("FirebaseStorage", "Failed to upload image", exception));
                } else {
                    db.collection("news")
                            .add(news)
                            .addOnSuccessListener(documentReference -> CloseFragment())
                            .addOnFailureListener(documentReference -> Log.e("FirebaseFirestore", "Error adding document", documentReference));
                }
            }
        });

        screenHeight = getResources().getDisplayMetrics().heightPixels;

        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    dY = v.getY() - event.getRawY();
                    initialY = v.getY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float newY = event.getRawY() + dY;
                    if (newY >= initialY) {
                        v.setY(newY);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if ((v.getY() / screenHeight) < 0.35) {
                        v.animate().y(initialY).setDuration(400);
                    } else {
                        CloseFragment();
                    }
                    break;

                default:
                    return false;
            }
            view.performClick();
            return true;
        });

        return view;
    }

    private void DeleteImage(){
        MainImageLoadingCard.setVisibility(View.GONE);
        ImageLoadingCard.setVisibility(View.GONE);
        textLoadingCard.setVisibility(View.GONE);
        clearImageButton.setVisibility(View.GONE);
        imageLoading.setImageResource(R.drawable.no_photo);
        textLoadingCard.setText("");
    }

    private void CloseFragment() {
        Animation slideOutBottomAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_to_bottom);
        requireView().startAnimation(slideOutBottomAnimation);
        requireActivity().getOnBackPressedDispatcher().onBackPressed();
    }
}
