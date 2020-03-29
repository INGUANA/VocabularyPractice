package com.inguana.vocabularypractice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.google.android.material.snackbar.Snackbar;
import com.inguana.vocabularypractice.Room.AppDatabase;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.wajahatkarim3.roomexplorer.RoomExplorer;

import java.util.ArrayList;
import java.util.List;

import libs.mjn.prettydialog.PrettyDialog;


public class MainActivity extends AppCompatActivity {

    //private FrameLayout flMainFragmentContainerMa;
    public String currentMainFragment;
    public Vocabulary vocabulary, sessionVocabulary;
    public List<String> translationPairList, sourcePairList;
    private PrettyDialog dialogPopUp;
    private CoordinatorLayout colMainActivityMa;
    public AppDatabase DBInstance;

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
        colMainActivityMa = findViewById(R.id.colMainActivityMa);

        translationPairList = new ArrayList<>();
        sourcePairList = new ArrayList<>();
        DBInstance = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "Word-Database").build();

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(KeyEvent.KEYCODE_VOLUME_DOWN == keyCode) {
            RoomExplorer.show(this, AppDatabase.class, "Word-Database");
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void displaySnackBar(String message, int messageDuration) {
        Snackbar snackbar = Snackbar
                .make(colMainActivityMa, message, messageDuration);
        snackbar.show();
    }

    //TODO: Handle OnBackPressed to pop fragments correctly

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }
}
