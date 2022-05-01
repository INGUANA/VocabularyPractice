package com.inguana.vocabularypractice;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.inguana.vocabularypractice.CustomExtensions.WordRecyclerViewArrayAdapter;
import com.inguana.vocabularypractice.Room.Word;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial;
import com.mikepenz.iconics.view.IconicsImageView;
import com.mikepenz.iconics.view.IconicsTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.inguana.vocabularypractice.CustomExtensions.WordRecyclerViewArrayAdapter.ADD_BUTTON_NAME_INDICATOR;

public class CreateModuleFragment extends BaseFragment {

    public enum TranslationMode {
        JapaneseToEnglish, EnglishToJapanese;
    }

    private int fragmentContainerId;

    private TextView itvModuleTitleCmf;
    private MainActivity activity;
    private RecyclerView rvWordListCmf;
    private ViewGroup clCreateUpdateMockLayoutLibi;
    private LinearLayoutManager layoutManager;
    private WordRecyclerViewArrayAdapter recyclerViewArrayAdapter;
    private IconicsImageView iivCreateUpdateNewModuleIconLibi;
    private IconicsTextView itvCreateUpdateNewModuleIconLibi;
    private IconicsDrawable addNewWordIcon;
    private List<Word> wordObjectList;
    private List<String> wordList;
    private String test2;

    public void initialize(View view, ViewGroup container) {
        fragmentContainerId = container.getId();

        rvWordListCmf = view.findViewById(R.id.rvWordListCmf);
        iivCreateUpdateNewModuleIconLibi = view.findViewById(R.id.iivButtonIconLibi);
        itvModuleTitleCmf = view.findViewById(R.id.itvModuleTitleCmf);
        itvCreateUpdateNewModuleIconLibi = view.findViewById(R.id.itvTextButtonIconLibi);

        clCreateUpdateMockLayoutLibi = view.findViewById(R.id.clButtonLayoutLibi);

        activity = ((MainActivity) getActivity());
        wordList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_module, container, false);
        initialize(view, container);

        rvWordListCmf.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        rvWordListCmf.setLayoutManager(layoutManager);

        Bundle argumentBundle = this.getArguments();
        if (null != argumentBundle) {
            initializeForUpdateModule(argumentBundle);
        } else {
            initializeForCreateModule();
        }

        wordList.add(ADD_BUTTON_NAME_INDICATOR);
        recyclerViewArrayAdapter = new WordRecyclerViewArrayAdapter(wordList, getContext());//new ArrayList<>(Arrays.asList("")). Collections.singletonList("")) produces immutable list so i cant add additional element if i wanted to
        rvWordListCmf.setAdapter(recyclerViewArrayAdapter);

        //addNewWordIcon.color(ContextCompat.getColor(getContext(), R.color.pdlg_color_black));
        iivCreateUpdateNewModuleIconLibi.setIcon(addNewWordIcon);

        return view;
    }

    private void getWordListFromDB(String moduleToDisplay) {
        try {
            Thread DBThread = new Thread(() -> {
                wordList = ConvertWordToString(activity.DBInstance.wordDao().getModule(moduleToDisplay));
            });
            DBThread.start();
            DBThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initializeForUpdateModule(Bundle argumentBundle) {
        String moduleToDisplay = argumentBundle.getString("SELECTED_MODULE_KEY");

        itvModuleTitleCmf.setText(moduleToDisplay);
        itvCreateUpdateNewModuleIconLibi.setText(R.string.fragment_create_module_update_module);
        getWordListFromDB(moduleToDisplay);

        addNewWordIcon = new IconicsDrawable(getContext(), GoogleMaterial.Icon.gmd_folder);

        clCreateUpdateMockLayoutLibi.setOnClickListener((View iivView) -> {
            if (itvModuleTitleCmf.getText().toString().isEmpty()) {
                //TODO: play animation on button
                activity.displayDialog("Error", getResources().getString(R.string.popup_empty_title), R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
            } else {
                AtomicBoolean moduleExists = new AtomicBoolean(false);
                rvWordListCmf.clearFocus();
                new Thread(() -> {
                    try {
                        if (activity.DBInstance.wordDao().checkIfDuplicateExcept(itvModuleTitleCmf.getText().toString(), moduleToDisplay)) {
                            moduleExists.set(true);
                        } else {
                            wordObjectList = recyclerViewArrayAdapter.getCleanWordList(itvModuleTitleCmf.getText().toString());
                            if (!wordObjectList.isEmpty()) {
                                activity.DBInstance.wordDao().deleteByModuleName(moduleToDisplay);
                                activity.DBInstance.wordDao().insertAllModule(wordObjectList);
                                activity.displaySnackBar(getResources().getString(R.string.snackbar_edit_module_success), Snackbar.LENGTH_LONG);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        getActivity().runOnUiThread(() -> {
                            if (moduleExists.get()) {
                                activity.displayDialog("Oops", getResources().getString(R.string.popup_no_module_name_exists), R.drawable.pdlg_icon_info, R.color.pdlg_color_blue);
                            } else if (wordObjectList.isEmpty()) {
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
    }

    private void initializeForCreateModule() {
        itvCreateUpdateNewModuleIconLibi.setText(R.string.fragment_create_module_create_new_module);
        addNewWordIcon = new IconicsDrawable(getContext(), GoogleMaterial.Icon.gmd_create_new_folder);

        //using lambda instead of old ways: https://stackoverflow.com/questions/30752547/what-does-it-mean-that-a-listener-can-be-replaced-with-lambda
        clCreateUpdateMockLayoutLibi.setOnClickListener((View iivView) -> {
            if (itvModuleTitleCmf.getText().toString().isEmpty()) {
                //TODO: play animation on button
                activity.displayDialog("Error", getResources().getString(R.string.popup_empty_title), R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
            } else {
                AtomicBoolean moduleExists = new AtomicBoolean(false);
                rvWordListCmf.clearFocus();
                new Thread(() -> {
                    try {
                        if (activity.DBInstance.wordDao().checkIfDuplicate(itvModuleTitleCmf.getText().toString())) {
                            moduleExists.set(true);
                        } else {
                            wordObjectList = recyclerViewArrayAdapter.getCleanWordList(itvModuleTitleCmf.getText().toString());
                            if (!wordObjectList.isEmpty()) {
                                activity.DBInstance.wordDao().insertAllModule(wordObjectList);
                                //activity.DBInstance.wordDao().delete(Collections.singletonList(new Word(ADD_BUTTON_NAME_INDICATOR, itvModuleTitleCmf.getText().toString())));

                                activity.displaySnackBar(getResources().getString(R.string.snackbar_create_module_success), Snackbar.LENGTH_LONG);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        getActivity().runOnUiThread(() -> {
                            if (moduleExists.get()) {
                                activity.displayDialog("Oops", getResources().getString(R.string.popup_no_module_name_exists), R.drawable.pdlg_icon_info, R.color.pdlg_color_blue);
                            } else if (wordObjectList.isEmpty()) {
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
    }

    private List<String> ConvertWordToString(List<Word> wordList) {
        return wordList.stream()
                .map(item -> item.getWord())
                .collect(Collectors.toList());
    }
}
