package com.inguana.vocabularypractice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.material.snackbar.Snackbar;
import com.inguana.vocabularypractice.Room.AppDatabase;
import com.inguana.vocabularypractice.Room.Migrations.Migration1To2;
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
    private Dialog overlayDialog;
    private CoordinatorLayout colMainActivityMa;
    private CircularProgressView pbProgressBarMa;
    public AppDatabase DBInstance;
    public APIInterface apiInterface;
    public static final String MAIN_MENU_FRAGMENT_TAG = "MAIN_MENU_FRAGMENT";
    public static final String WORD_GUESS_FRAGMENT_TAG = "WORD_GUESS_FRAGMENT";
    public static final String CREATE_MODULE_FRAGMENT_TAG = "CREATE_MODULE_FRAGMENT";
    public static final String MODULE_LIST_FRAGMENT_TAG = "MODULE_LIST_FRAGMENT";

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
        pbProgressBarMa = findViewById(R.id.pbProgressBarMa);
        overlayDialog = new Dialog(this, R.style.Theme_AppCompat_Dialog_Transparent);
        overlayDialog.setCancelable(true);

        translationPairList = new ArrayList<>();
        sourcePairList = new ArrayList<>();
        //TODO: check getInstance
        DBInstance = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "Word-Database")
                .addMigrations(new Migration1To2(1,2))
                .build();

        JsonGetter.getRetrofitInstance(); //needs fixing
        apiInterface = JsonGetter.buildService(APIInterface.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();

        currentMainFragment = MAIN_MENU_FRAGMENT_TAG;
        getSupportFragmentManager().beginTransaction().replace(R.id.flMainFragmentContainerMa, new MainMenuFragment(), currentMainFragment)/*.addToBackStack(null)*/.commit();
    }

    public void swapFragment() {

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

    public PrettyDialog displayOptionDialog(String title, String message, int icon, int iconColor) {
        displayDialog(title, message, icon, iconColor);
        return dialogPopUp;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_VOLUME_DOWN == keyCode) {
            RoomExplorer.show(this, AppDatabase.class, "Word-Database");
        }
        return super.onKeyDown(keyCode, event);
    }

    public void startTransition(boolean isStarting) {
        if (isStarting) {
            pbProgressBarMa.setVisibility(View.VISIBLE);
            overlayDialog.show();
        } else {
            pbProgressBarMa.setVisibility(View.GONE);
            overlayDialog.dismiss();
        }
    }

    public boolean isProgressBarVisible() {
        return View.VISIBLE == pbProgressBarMa.getVisibility();
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

    public void prepareDisplayLists(boolean isTransitioning, String iterationWord, String translationWord) {
        if (isTransitioning) {
            clearDisplayLists();
        }

        sourcePairList.add(iterationWord);
        translationPairList.add(translationWord);

        sessionVocabulary.removeWord(iterationWord);

        //TODO: investigate whether i need this or not. if i do, then i make the tags as enum and i pass it
        //currentMainFragment = WORD_GUESS_FRAGMENT_TAG;
    }

    public void clearDisplayLists() {
        sourcePairList = new ArrayList<>();
        translationPairList = new ArrayList<>();
    }

    //TODO: Handle OnBackPressed to pop fragments correctly
    //TODO: FEATURE: Option if should consider case sensitive or not
    //TODO: investigate why it behaves weird whne push notification from other app appears
    //TODO: bundle same code to be written one time only (example when you go to word guess, you have to do several things to join)
    //TODO: module word calling in wordguess is the same as in module list. place calls in the DAO (they require bundling)

    //Layout
    //TODO: Button pressing should be better
    //TODO: progressbar alteration
    //TODO: AAE App intro + particles
    //TODO: background color depending on the fragment and intent the user has?
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }
}
