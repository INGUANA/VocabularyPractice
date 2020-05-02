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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.StorageReference;
import com.inguana.vocabularypractice.CustomExtensions.WordRecyclerViewArrayAdapter;
import com.inguana.vocabularypractice.Room.Word;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.inguana.vocabularypractice.CustomExtensions.WordRecyclerViewArrayAdapter.ADD_BUTTON_NAME_INDICATOR;

public class CreateModuleFragment extends Fragment {

    public enum TranslationMode {
        JapaneseToEnglish, EnglishToJapanese;
    }

    private int fragmentContainerId;

    private TextView itvModuleTitleCmf;
    private Dialog overlayDialog;
    private MainActivity activity;
    private CircularProgressView pbProgressBarMmf;
    private RecyclerView rvWordListCmf;
    private ViewGroup clCreateUpdateMockLayoutCmf;
    private LinearLayoutManager layoutManager;
    private WordRecyclerViewArrayAdapter recyclerViewArrayAdapter;
    private IconicsImageView iivCreateUpdateNewModuleIconCmf;
    private List<Word> wordList;

    public void initialize(View view, ViewGroup container) {
        fragmentContainerId = container.getId();

        overlayDialog = new Dialog(getContext(), R.style.Theme_AppCompat_Dialog_Transparent);
        overlayDialog.setCancelable(true);

        pbProgressBarMmf = view.findViewById(R.id.pbProgressBarMmf);
        rvWordListCmf = view.findViewById(R.id.rvWordListCmf);
        iivCreateUpdateNewModuleIconCmf = view.findViewById(R.id.iivCreateUpdateNewModuleIconCmf);
        itvModuleTitleCmf = view.findViewById(R.id.itvModuleTitleCmf);

        clCreateUpdateMockLayoutCmf = view.findViewById(R.id.clCreateUpdateMockLayoutCmf);

        activity = ((MainActivity) getActivity());
        wordList = new ArrayList<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_module, container, false);
        initialize(view, container);

        rvWordListCmf.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        rvWordListCmf.setLayoutManager(layoutManager);

        recyclerViewArrayAdapter = new WordRecyclerViewArrayAdapter(new ArrayList<>(Arrays.asList(ADD_BUTTON_NAME_INDICATOR)), getContext());//new ArrayList<>(Arrays.asList("")). Collections.singletonList("")) produces immutable list so i cant add additional element if i wanted to
        rvWordListCmf.setAdapter(recyclerViewArrayAdapter);

        IconicsDrawable addNewWordIcon = new IconicsDrawable(getContext(), GoogleMaterial.Icon.gmd_create_new_folder);
        addNewWordIcon.color(ContextCompat.getColor(getContext(), R.color.pdlg_color_black));
        iivCreateUpdateNewModuleIconCmf.setIcon(addNewWordIcon);
        //using lambda instead of old ways: https://stackoverflow.com/questions/30752547/what-does-it-mean-that-a-listener-can-be-replaced-with-lambda

        clCreateUpdateMockLayoutCmf.setOnClickListener((View iivView) -> {
            if(itvModuleTitleCmf.getText().toString().isEmpty()) {
                //TODO: play animation on button
                activity.displayDialog("Error", getResources().getString(R.string.popup_empty_title), R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
            } else {
                //DONE: add last word in wordlist without onFocus
                //DONE: don't add empty words
                //DONE: check if module exists, if exists 1. how it behaves(should replace) 2. make it so that it gives a warning.
                //DONE: return to module list
                AtomicBoolean moduleExists = new AtomicBoolean(false);
                rvWordListCmf.clearFocus();
                new Thread(() -> {
                    try {
                        if(activity.DBInstance.wordDao().checkIfDuplicate(itvModuleTitleCmf.getText().toString())) {
                            moduleExists.set(true);
                        } else {
                            wordList = recyclerViewArrayAdapter.getCleanWordList(itvModuleTitleCmf.getText().toString());
                            if(!wordList.isEmpty()) {
                                activity.DBInstance.wordDao().insertAllModule(wordList);
                                //activity.DBInstance.wordDao().delete(Collections.singletonList(new Word(ADD_BUTTON_NAME_INDICATOR, itvModuleTitleCmf.getText().toString())));

                                activity.displaySnackBar(getResources().getString(R.string.snackbar_create_module_success), Snackbar.LENGTH_LONG);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        getActivity().runOnUiThread(() -> {
                            if(moduleExists.get()) {
                                activity.displayDialog("Oops", getResources().getString(R.string.popup_no_module_name_exists), R.drawable.pdlg_icon_info, R.color.pdlg_color_blue);
                            } else if (wordList.isEmpty()) {
                                activity.displayDialog("Oops", getResources().getString(R.string.popup_no_word_inside_exists), R.drawable.pdlg_icon_info, R.color.pdlg_color_blue);
                            } else {
                                //int count = getActivity().getSupportFragmentManager().getBackStackEntryCount();
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        });
                    }
                }).start();
            }
        });

        return view;
    }

    public void addWordItem(String word) {
        recyclerViewArrayAdapter.getWordList().add(word);
        recyclerViewArrayAdapter.notifyItemInserted(recyclerViewArrayAdapter.getWordList().size() - 1);
    }
}
