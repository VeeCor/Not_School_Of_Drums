package com.example.notschoolofdrums.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.notschoolofdrums.Fragments.Account;
import com.example.notschoolofdrums.Fragments.Account_change;
import com.example.notschoolofdrums.Fragments.Add_news;
import com.example.notschoolofdrums.Fragments.Entry;
import com.example.notschoolofdrums.Fragments.NewPerson;
import com.example.notschoolofdrums.Fragments.News;
import com.example.notschoolofdrums.Fragments.Messages;
import com.example.notschoolofdrums.Fragments.Raspisanie;
import com.example.notschoolofdrums.Fragments.Settings;
import com.example.notschoolofdrums.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements Account.OnChangeBtnClickListener, Settings.OnExitButtonClickListener {

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser() == null){
            startActivity(new Intent(MainActivity.this, Activity_login.class));
            finish();
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        toolbar = findViewById(R.id.main_toolbar);
        drawerLayout = findViewById(R.id.Main_Drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        AccountIndex();

        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            if(menuItem.getItemId() == R.id.news){
                ReplaceFragment(new News());
                toolbar.setTitle(R.string.title_news);
                toolbar.inflateMenu(R.menu.news_menu);
            } else if (menuItem.getItemId() == R.id.entry){
                ReplaceFragment(new Entry());
                toolbar.setTitle(R.string.title_entry);
                toolbar.inflateMenu(R.menu.plus_menu);
            } else if (menuItem.getItemId() == R.id.messages){
                ReplaceFragment(new Messages());
                toolbar.setTitle(R.string.title_messages);
                toolbar.inflateMenu(R.menu.new_message_menu);
            } else if (menuItem.getItemId() == R.id.raspisanie) {
                ReplaceFragment(new Raspisanie());
                toolbar.setTitle(R.string.raspis);
            } else if (menuItem.getItemId() == R.id.add_person) {
                ReplaceFragment(new NewPerson());
                toolbar.setTitle(R.string.create);
            }
            return  true;
        });

        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.new_entry_icon_menu){
                Intent intent = new Intent(MainActivity.this, Add_Entry.class);
                startActivity(intent);
            }
            if(item.getItemId() == R.id.add_icon_menu){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame_for_add_news, new Add_news());
                ft.addToBackStack(null);
                ft.commit();
            }
            return true;
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.drawer_close, R.string.drawer_open);
        drawerLayout.addDrawerListener(toggle);

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            if(menuItem.getItemId() == R.id.profile_item){
                FragmentTransaction FT = getSupportFragmentManager().beginTransaction();
                FT.setCustomAnimations(R.anim.slide_in_from_right, 0);
                FT.replace(R.id.frame_for_fragments, new Account()).commit();
                toolbar.getMenu().clear();
                toolbar.setTitle(R.string.account);
            } else if (menuItem.getItemId() == R.id.task_item) {

            } else if (menuItem.getItemId() == R.id.settings_item) {
                ReplaceFragment(new Settings());
                toolbar.setTitle(R.string.settings);
            } else if (menuItem.getItemId() == R.id.raspis_item) {
                ReplaceFragment(new Raspisanie());
                toolbar.setTitle(R.string.raspis);
            } else if (menuItem.getItemId() == R.id.about_item) {

            } else if (menuItem.getItemId() == R.id.FAQ_item) {

            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }

    private void ReplaceFragment(Fragment fragment){
        FragmentTransaction FT = getSupportFragmentManager().beginTransaction();
        FT.replace(R.id.frame_for_fragments, fragment).commit();
        toolbar.getMenu().clear();
    }

    private void AccountIndex(){
        int index = getIndex(this);
        toolbar.getMenu().clear();
        if(index == 1){
            toolbar.inflateMenu(R.menu.news_menu);
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.drawer_navigation_menu_student);
        } else if (index == 2) {
            toolbar.inflateMenu(R.menu.news_menu);
            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu_teacher);
        } else if (index == 3) {
            toolbar.inflateMenu(R.menu.news_menu_admin);
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.drawer_navigation_menu_admin);
        } else if (index == 4) {
            toolbar.inflateMenu(R.menu.news_menu);
            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu_manager);
        }
    }

    public static int getIndex(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AccountID", Context.MODE_PRIVATE);
        return prefs.getInt("Index", 0);
    }

    @Override
    public void OnChangeBtnClick() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_for_fragments, new Account_change());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void OnExitButtonClick() {
        fAuth.signOut();
        finish();
        Intent intent = new Intent(MainActivity.this, Activity_login.class);
        startActivity(intent);
    }
}