package com.inguana.vocabularypractice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import libs.mjn.prettydialog.PrettyDialog;


class MainActivity extends AppCompatActivity {

    //private FrameLayout flMainFragmentContainerMa;
    public String currentMainFragment;
    public Vocabulary vocabulary;
    public List<String> translationPairList;
    private PrettyDialog dialogPopUp;
    public enum APICode {
        SUCCESS(200);

        private int code;

        APICode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public void initialize() {
        //flMainFragmentContainerMa = findViewById(R.id.flMainFragmentContainerMa);
        translationPairList = new ArrayList<>();
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

    public void displaySnackbar() {

    }

    public void displayDialog(String title, String message, int icon, int iconColor) {
        //if(!dialogPopUp.isShowing()) {
            dialogPopUp = new PrettyDialog(this);
            //test PrettyDialog capabilities
            //dialogPopUp.setContentView();
            dialogPopUp.setTitle(title);
            dialogPopUp.setIcon(icon);
            dialogPopUp.setIconTint(iconColor);
            dialogPopUp.setMessage(message);
            dialogPopUp.show();
            //dialogPopUp.setOnCancelListener();
        //}
    }
}
