package tic.heiafr.ch.tp04_taboo.ui;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import tic.heiafr.ch.tp04_taboo.R;
public class ContainerActivity extends AppCompatActivity {

    private NavController navController;
    @Override
    public void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder().build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp();
    }


}

