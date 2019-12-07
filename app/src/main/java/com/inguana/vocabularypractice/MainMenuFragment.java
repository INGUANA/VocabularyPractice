package com.inguana.vocabularypractice;

import android.app.Dialog;
import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.inguana.vocabularypractice.rest.response.BaseResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMenuFragment extends Fragment {

    public enum TranslationMode {
        JapaneseToEnglish, EnglishToJapanese;
    }

    private int fragmentContainerId;

    private Button btStartMmf, btSettingsMmf, btExitMmf;
    private ImageButton ibSettingsInformationMmf;
    private TranslationMode translationMode;
    private TextView tvTooltipTextMmf;
    private Dialog overlayDialog;
    private StorageReference mStorageRef;
    private MainActivity activity;

    private static final String VOCABULARY_WORDS_PATH = "google-10000-english.txt";
    //private static final String VOCABULARY_WORDS_URI = "gs://vocabularypractice-dae13.appspot.com/google-10000-english.txt";

    public void initialize(View view, ViewGroup container) {
        btStartMmf = view.findViewById(R.id.btStartMmf);
        btSettingsMmf = view.findViewById(R.id.btSettingsMmf);
        btExitMmf = view.findViewById(R.id.btExitMmf);

        tvTooltipTextMmf = view.findViewById(R.id.tvTooltipTextMmf);

        ibSettingsInformationMmf = view.findViewById(R.id.ibSettingsInformationMmf);

        translationMode = TranslationMode.EnglishToJapanese;

        fragmentContainerId = container.getId();

        overlayDialog = new Dialog(getContext(), R.style.Theme_AppCompat_Dialog_Transparent);
        overlayDialog.setCancelable(true);

        mStorageRef = FirebaseStorage.getInstance().getReference();

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

        btStartMmf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickStartWordGuess();
            }
        });

        btSettingsMmf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSwapTranslationMode();
            }
        });

        btExitMmf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        ibSettingsInformationMmf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickViewToolTip();
            }
        });

        overlayDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                closeToolTip();
            }
        });

        return view;
    }

    private void onClickStartWordGuess() {
        //TODO: initialize next fragment, close this one
        //translationMode in main activity maybe
        Bundle startButtonBundle = new Bundle();
        startButtonBundle.putSerializable("translationMode", translationMode);
        MainMenuFragment mainMenuFragment = new MainMenuFragment();
        mainMenuFragment.setArguments(startButtonBundle);

        //TODO: thread the call of storage and the download. As soon as it finishes, move to second fragment

        //if inNetworkAvailable
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    downloadFirebaseFile();


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //show error message or go to next activity when youve stored the text file
                            //if there are no words in vocab {and i = 5 (aka this is the last call)} show error message( should examine this in finally of finally (run thread)
                            //if there is at least one word -> go into the next fragment
                            if (!activity.translationPairList.isEmpty()) {
                                getActivity().getSupportFragmentManager().beginTransaction().replace(fragmentContainerId, new TranslationFragment()).commit();
                            }
                        }
                    });
                }


            }
        }).start();
        //getActivity().getSupportFragmentManager().beginTransaction().replace(fragmentContainerId, mainMenuFragment).commit();
        //make it as modular as possible, here comes the new curent fragment declaration, but is there another way besides ((MainActivity)getActivity().....
    }

    private void downloadFirebaseFile() {
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
            activity.displayDialog("Error", "Something went wrong with files.", R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
        }
        final String instanceLocalFile = localFile.getPath();
        vocabularyFirebaseRef.getFile(localFile)//asynchronous
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        activity.vocabulary = new Vocabulary(instanceLocalFile);
                        getTranslationRequest();
                        /*getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity.displayDialog("Success", "Retrieved vocabulary: " + activity.vocabulary.getVocabularyWords().get((int) (Math.random() * 10000) + 1) + "!!!", R.drawable.pdlg_icon_success, R.color.pdlg_color_blue);
                            }
                        });*/

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                vocabularyFirebaseRef.delete();
                /*getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.displayDialog("Error", "Failed to download the vocabulary file.", R.drawable.pdlg_icon_close, R.color.pdlg_color_red);

                    }
                });*/
            }
        });
    }

    ////////////////

    public void getTranslationRequest() { //do 5 calls and get the results into the array

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final APIInterface APIInterface = JsonGetter.buildService(APIInterface.class);

                    for (int i = 0; 5 > i; i++) {
                        String iterationWord = activity.vocabulary.getRandomVocabularyWord();
                        Call<BaseResponse> call = APIInterface.getWordTranslation("trnsl.1.1.20191122T214733Z.cf94b5c1ffe7138e.4e0d2c816ca7086b3fb094fae7693af405c30627", iterationWord, "ja");
                        //Call<Object> call = APIInterface.getLangs("dict.1.1.20191113T191908Z.b09388c3c67363c8.a16b9135d70ffed223b2a9e83d3ae4d1cc3b95f7"); dictionary call
                        try {
                            Response<BaseResponse> response = call.execute(
                                    //===================================================================
                                    // code that would go for enqueue and not for execute
                    /*new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> request, Response<BaseResponse> response) {
                    if (MainActivity.APICode.SUCCESS.getCode() == response.body().getCode()) {
                        activity.translationPairList.add(response.body().getText().get(0));
                    } else {
                        //dont display message
                        //activity.displayDialog("Error", "There is no such word to translate.<HiddenMessage>", R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
                    }
                    //activity.displayDialog("Success", "got translation", R.drawable.pdlg_icon_success, R.color.pdlg_color_blue);
                }

                @Override
                public void onFailure(Call<BaseResponse> request, Throwable t) {
                    //do whatever with response (error)
                    activity.displayDialog("Error", "DIDNT get translation", R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
                }

            }*/
                                    //===================================================================
                            );
                            if (MainActivity.APICode.SUCCESS.getCode() == response.body().getCode()) {
                                activity.translationPairList.add(response.body().getText().get(0));
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
                }


            }
        }).start();
        JsonGetter.getRetrofitInstance(); //needs fixing


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
