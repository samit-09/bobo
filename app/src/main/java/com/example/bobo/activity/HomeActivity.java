package com.example.bobo.activity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.bobo.R;
import com.example.bobo.fragment.CategoryFragment;
import com.example.bobo.fragment.ChatFragment;
import com.example.bobo.fragment.FavoriteFragment;
import com.example.bobo.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                updateIconState(itemId);
                if (itemId == R.id.navigation_chat) {
                    loadFragment(new ChatFragment());
                    return true;
                } else if (itemId == R.id.navigation_category) {
                    loadFragment(new CategoryFragment());
                    return true;
                } else if (itemId == R.id.navigation_favorite) {
                    loadFragment(new FavoriteFragment());
                    return true;
                } else if (itemId == R.id.navigation_profile) {
                    loadFragment(new ProfileFragment());
                    return true;
                }
                return false;
            }
        });

        // Set the default fragment and icon state
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_chat);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
    }

    private void updateIconState(int selectedItemId) {
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            MenuItem menuItem = bottomNavigationView.getMenu().getItem(i);
            if (menuItem.getItemId() == selectedItemId) {
                menuItem.setIcon(getSelectedIcon(selectedItemId));
            } else {
                menuItem.setIcon(getUnselectedIcon(menuItem.getItemId()));
            }
        }
    }

    private int getSelectedIcon(int itemId) {
        if (itemId == R.id.navigation_chat) {
            return R.drawable.chat_selected;
        } else if (itemId == R.id.navigation_category) {
            return R.drawable.category_selected;
        } else if (itemId == R.id.navigation_favorite) {
            return R.drawable.favorite_selected;
        } else if (itemId == R.id.navigation_profile) {
            return R.drawable.profile_selected;
        } else {
            return 0;
        }
    }

    private int getUnselectedIcon(int itemId) {
        if (itemId == R.id.navigation_chat) {
            return R.drawable.chat_unselected;
        } else if (itemId == R.id.navigation_category) {
            return R.drawable.category_unselected;
        } else if (itemId == R.id.navigation_favorite) {
            return R.drawable.favorite_unselected;
        } else if (itemId == R.id.navigation_profile) {
            return R.drawable.profile_unselected;
        } else {
            return 0;
        }
    }
}
