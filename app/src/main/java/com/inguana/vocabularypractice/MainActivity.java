package com.inguana.vocabularypractice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.FrameLayout;


class MainActivity extends AppCompatActivity {

    //private FrameLayout flMainFragmentContainerMa;
    public String currentMainFragment;

    public void initialize() {
        //flMainFragmentContainerMa = findViewById(R.id.flMainFragmentContainerMa);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();

        currentMainFragment = "MAIN_MENU_FRAGMENT";
        getSupportFragmentManager().beginTransaction().replace(R.id.flMainFragmentContainerMa, new MainMenuFragment(), currentMainFragment).commit();
    }

    public void swapFragment(){

    }
}
