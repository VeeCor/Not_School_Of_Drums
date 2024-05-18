package com.example.notschoolofdrums;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EmailChooserActivity extends AppCompatActivity {
    private static final int EMAIL_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, getIntent().getStringArrayExtra(Intent.EXTRA_EMAIL));
        intent.putExtra(Intent.EXTRA_SUBJECT, getIntent().getStringExtra(Intent.EXTRA_SUBJECT));
        intent.putExtra(Intent.EXTRA_TEXT, getIntent().getStringExtra(Intent.EXTRA_TEXT));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Выберите почтовое приложение"));
            ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Toast.makeText(EmailChooserActivity.this, "Сообщение отправлено", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EmailChooserActivity.this, "Сообщение не отправлено", Toast.LENGTH_LONG).show();
                }
                setResult(result.getResultCode());
                finish();
            });
            launcher.launch(intent);
        } else {
            Toast.makeText(this, "Не найдено почтовое приложение", Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}
