package com.example.notschoolofdrums.Fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.provider.MediaStore;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.notschoolofdrums.Activity.MainActivity;
import com.example.notschoolofdrums.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AddNews extends Fragment {

    ImageButton close;
    FloatingActionButton addImageButton;
    CardView MainImageLoadingCard, ImageLoadingCard;
    ImageView imageLoading;
    TextView textLoadingCard, whatIsNewTitle;
    EditText postText;
    ProgressBar progressBar;
    ImageButton clearImageButton;
    Uri imageUri;
    private float dY, initialY, screenHeight;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_news, container, false);

        Animation slideInBottomAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_bottom);
        view.startAnimation(slideInBottomAnimation);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        close = view.findViewById(R.id.close_item);
        addImageButton = view.findViewById(R.id.floatingActionButton);
        MainImageLoadingCard = view.findViewById(R.id.main_image_loading_card);
        ImageLoadingCard = view.findViewById(R.id.image_loading_card);
        imageLoading = view.findViewById(R.id.image_loading);
        textLoadingCard = view.findViewById(R.id.text_loading_card);
        progressBar = view.findViewById(R.id.progress_bar);
        clearImageButton = view.findViewById(R.id.clear_image_button);
        whatIsNewTitle = view.findViewById(R.id.what_is_new_title);
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
                        assert imageUri != null;
                        String StringUri = imageUri.toString();
                        MainImageLoadingCard.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        imageLoading.setImageURI(imageUri);
                        textLoadingCard.setText(StringUri);
                        new Handler().postDelayed(() -> {
                            progressBar.setVisibility(View.GONE);
                            ImageLoadingCard.setVisibility(View.VISIBLE);
                            textLoadingCard.setVisibility(View.VISIBLE);
                            clearImageButton.setVisibility(View.VISIBLE);
                        }, 3000);
                    }
                }
        );

        addImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            someActivityResultLauncher.launch(intent);
        });

        clearImageButton.setOnClickListener(v -> DeleteImage());


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
