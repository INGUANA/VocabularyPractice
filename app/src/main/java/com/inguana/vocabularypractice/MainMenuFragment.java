package com.inguana.vocabularypractice;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.inguana.vocabularypractice.Room.Word;
import com.inguana.vocabularypractice.rest.response.BaseResponse;

import java.io.File;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.inguana.vocabularypractice.MainActivity.MODULE_LIST_FRAGMENT_TAG;
import static com.inguana.vocabularypractice.MainActivity.WORD_GUESS_FRAGMENT_TAG;

public class MainMenuFragment extends Fragment {

    public enum TranslationMode {
        JapaneseToEnglish, EnglishToJapanese;
    }

    private int fragmentContainerId;

    private Button btStartMmf, btSettingsMmf, btModuleListMmf;
    private ImageButton ibSettingsInformationMmf;
    private TranslationMode translationMode;
    private TextView tvTooltipTextMmf;
    private Dialog overlayDialog;
    private StorageReference mStorageRef;
    private MainActivity activity;
    private Task firebaseTask;
    private CircularProgressView pbProgressBarMmf;

    private static final String VOCABULARY_WORDS_PATH = "google-10000-english.txt";
    //private static final String VOCABULARY_WORDS_URI = "gs://vocabularypractice-dae13.appspot.com/google-10000-english.txt";
    //TODO: make overlayDialog for loading (so that buttons cant be clicked when loading.

    private void initialize(View view, ViewGroup container) {
        btStartMmf = view.findViewById(R.id.btStartMmf);
        btSettingsMmf = view.findViewById(R.id.btSettingsMmf);
        btModuleListMmf = view.findViewById(R.id.btModuleListMmf);

        tvTooltipTextMmf = view.findViewById(R.id.tvTooltipTextMmf);

        ibSettingsInformationMmf = view.findViewById(R.id.ibSettingsInformationMmf);

        translationMode = TranslationMode.EnglishToJapanese;

        fragmentContainerId = container.getId();

        overlayDialog = new Dialog(getContext(), R.style.Theme_AppCompat_Dialog_Transparent);
        overlayDialog.setCancelable(true);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        pbProgressBarMmf = view.findViewById(R.id.pbProgressBarMmf);

        activity = ((MainActivity) getActivity());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        initialize(view, container);

        btStartMmf.setOnClickListener(view1 -> onClickStartWordGuess());

        btSettingsMmf.setOnClickListener(view12 -> onClickSwapTranslationMode());

        btModuleListMmf.setOnClickListener(view13 -> {
            activity.currentMainFragment = MODULE_LIST_FRAGMENT_TAG;
            getActivity().getSupportFragmentManager().beginTransaction().replace(fragmentContainerId, new ModuleListFragment(), MODULE_LIST_FRAGMENT_TAG).addToBackStack(null).commit();
        });

        ibSettingsInformationMmf.setOnClickListener(v -> onClickViewToolTip());

        overlayDialog.setOnCancelListener(dialog -> closeToolTip());

        return view;
    }

    private void onClickStartWordGuess() {
        //translationMode in main activity maybe
        Bundle startButtonBundle = new Bundle();
        startButtonBundle.putSerializable("translationMode", translationMode);
        MainMenuFragment mainMenuFragment = new MainMenuFragment();
        mainMenuFragment.setArguments(startButtonBundle);

        pbProgressBarMmf.setVisibility(View.VISIBLE);
        downloadFirebaseFile();


        //if inNetworkAvailable
        //make it as modular as possible, here comes the new curent fragment declaration, but is there another way besides ((MainActivity)getActivity().....
    }

    //do asynchronous call on firebase. when returned do synchronous 5 calls for getting word transations from the retrieved firebase words
    private void downloadFirebaseFile() {
        if(activity.isNetworkAvailable()) {
            final StorageReference vocabularyFirebaseRef = mStorageRef.child(VOCABULARY_WORDS_PATH);
            final File checkFileInstance = new File("vocabulary.txt");
            File localFile = null;
            try {
                if (checkFileInstance.exists()) {
                    checkFileInstance.delete();
                }
                localFile = File.createTempFile("vocabulary", "txt", getContext().getFilesDir());
                //NOTE: delete file when finished or when error. (myContext.deleteFile(fileName)
                //https://developer.android.com/training/data-storage/files/internal#DeleteFile
            } catch (IOException e) {
                e.printStackTrace();
                activity.displayDialog("Error", getResources().getString(R.string.popup_io_firebase_file_error), R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
            }
            final String instanceLocalFile = localFile.getPath();
            firebaseTask = vocabularyFirebaseRef.getFile(localFile);//asynchronous

            firebaseTask.addOnSuccessListener((OnSuccessListener<FileDownloadTask.TaskSnapshot>) taskSnapshot -> {
                // Successfully downloaded data to local file
                activity.vocabulary = new Vocabulary(instanceLocalFile);
                activity.sessionVocabulary = new Vocabulary(instanceLocalFile);
                getTranslationRequest();

            });
            firebaseTask.addOnFailureListener(exception -> {
                // Handle failed download
                vocabularyFirebaseRef.delete();
                pbProgressBarMmf.setVisibility(View.GONE);
            });
        } else {
            pbProgressBarMmf.setVisibility(View.GONE);
            activity.displayDialog("Error", "No internet availableHARDCODEDDDD", R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
        }
    }

    private void getTranslationRequest() { //do 5 calls and get the results into the array

        new Thread(() -> {
            try {

                for (int i = 0; 5 > i; i++) {
                    String iterationWord = activity.sessionVocabulary.getRandomVocabularyWord();
                    Call<BaseResponse> call = activity.apiInterface.getWordTranslation(iterationWord);
                    //Call<Object> call = APIInterface.getLangs("dict.1.1.20191113T191908Z.b09388c3c67363c8.a16b9135d70ffed223b2a9e83d3ae4d1cc3b95f7"); dictionary call
                    try {
                        Response<BaseResponse> response = call.execute(
                        );
                        if (response.isSuccessful()/*MainActivity.APICode.SUCCESS.getCode() == response.body().getCode()*/) {
                            activity.sourcePairList.add(iterationWord);
                            activity.translationPairList.add(response.body().getData().get(0).getJapanese().get(0).getReading());
                            activity.sessionVocabulary.removeWord(iterationWord);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                getActivity().runOnUiThread(() -> {
                    //TODO:   3. Deal with trash word
                    //if there is at least one word -> go into the next fragment
                    pbProgressBarMmf.setVisibility(View.GONE);
                    if (!activity.translationPairList.isEmpty()) {
                        activity.currentMainFragment = WORD_GUESS_FRAGMENT_TAG;
                        getActivity().getSupportFragmentManager().beginTransaction().replace(fragmentContainerId, new WordGuessFragment(), WORD_GUESS_FRAGMENT_TAG).addToBackStack("WORD_GUESS_FROM_MENU").commit();
                    } else {
                        activity.displayDialog("Error", "DIDNT get translation", R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
                    }
                });
            }


        }).start();
    }
    //////////////

    private void onClickSwapTranslationMode() {
        btSettingsMmf.setText(btSettingsMmf.getText().toString().equals(getString(R.string.fragment_main_menu_english_to_japanese))
                ? getString(R.string.fragment_main_menu_japanese_to_english)
                : getString(R.string.fragment_main_menu_english_to_japanese));

        translationMode = translationMode == TranslationMode.JapaneseToEnglish
                ? TranslationMode.EnglishToJapanese
                : TranslationMode.JapaneseToEnglish;
    }

    private void onClickViewToolTip() {
        if (View.VISIBLE != tvTooltipTextMmf.getVisibility()) {
            tvTooltipTextMmf.setVisibility(View.VISIBLE);
            overlayDialog.show();
        }
    }

    private void closeToolTip() {
        if (View.VISIBLE == tvTooltipTextMmf.getVisibility()) {
            tvTooltipTextMmf.setVisibility(View.GONE);
        }
    }
}
