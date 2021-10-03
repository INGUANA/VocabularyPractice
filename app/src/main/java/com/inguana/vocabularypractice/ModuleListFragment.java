package com.inguana.vocabularypractice;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.inguana.vocabularypractice.CustomExtensions.ModuleRecyclerViewArrayAdapter;
import com.inguana.vocabularypractice.Room.Word;
import com.inguana.vocabularypractice.rest.response.BaseResponse;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;
import com.mikepenz.iconics.view.IconicsTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import libs.mjn.prettydialog.PrettyDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.inguana.vocabularypractice.MainActivity.CREATE_MODULE_FRAGMENT_TAG;
import static com.inguana.vocabularypractice.MainActivity.WORD_GUESS_FRAGMENT_TAG;
import static com.inguana.vocabularypractice.Vocabulary.NO_WORD_IN_LIST;

public class ModuleListFragment extends BaseFragment implements ModuleRecyclerViewArrayAdapter.OnModuleClickListener {

    private int fragmentContainerId;

    private MainActivity activity;
    private RecyclerView rvModuleListMlf;
    private LinearLayoutManager layoutManager;
    private ModuleRecyclerViewArrayAdapter recyclerViewArrayAdapter;
    private List<String> moduleStringList, wordList;
    private IconicsImageView iivNewModuleIconMlf;
    private ConstraintLayout clNewModuleMlf;
    private IconicsTextView itvNoModulesMlf;
    private int timesFailedGetTranslation;

    public void initialize(View view, ViewGroup container) {
        fragmentContainerId = container.getId();

        rvModuleListMlf = view.findViewById(R.id.rvModuleListMlf);
        clNewModuleMlf = view.findViewById(R.id.clNewModuleMlf);

        iivNewModuleIconMlf = view.findViewById(R.id.iivNewModuleIconMlf);
        itvNoModulesMlf = view.findViewById(R.id.itvNoWordsMlf);

        activity = ((MainActivity) getActivity());
        timesFailedGetTranslation = 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_module_list, container, false);
        initialize(view, container);

        rvModuleListMlf.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        rvModuleListMlf.setLayoutManager(layoutManager);

        IconicsDrawable addNewWordIcon = new IconicsDrawable(getContext(), GoogleMaterial.Icon.gmd_library_add);
        addNewWordIcon.color(ContextCompat.getColor(getContext(), R.color.pdlg_color_black));
        iivNewModuleIconMlf.setIcon(addNewWordIcon);

        clNewModuleMlf.setOnClickListener(view1 -> {
            activity.currentMainFragment = CREATE_MODULE_FRAGMENT_TAG;
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .replace(fragmentContainerId, new CreateModuleFragment(), CREATE_MODULE_FRAGMENT_TAG).addToBackStack(null).commit();
        });

        setModuleList();

        return view;
    }

    private void setModuleList() {
        new Thread(() -> {
            try {
                moduleStringList = activity.DBInstance.wordDao().getAllModules();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                getActivity().runOnUiThread(() -> {
                    if (moduleStringList.isEmpty()) {
                        itvNoModulesMlf.setVisibility(View.VISIBLE);
                    } else {
                        itvNoModulesMlf.setVisibility(View.GONE);
                        recyclerViewArrayAdapter = new ModuleRecyclerViewArrayAdapter(moduleStringList, this);
                        rvModuleListMlf.setAdapter(recyclerViewArrayAdapter);
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        int clickedItemPosition = item.getOrder(); // plays with getAdapterposition that is commented out in adapter
        int clickedOption = item.getItemId(); // is it edit or delete procedure

        switch (clickedOption) {
            case 0: {
                //activity.displaySnackBar("Clicked Edit option", Snackbar.LENGTH_SHORT);
                activity.currentMainFragment = CREATE_MODULE_FRAGMENT_TAG;

                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .replace(fragmentContainerId, prepareFragment(recyclerViewArrayAdapter.getItem(clickedItemPosition))).addToBackStack(null).commit();
                break;
            }
            case 1: {
                //activity.displaySnackBar("Clicked Delete option", Snackbar.LENGTH_SHORT);
                PrettyDialog dialog = activity.displayOptionDialog("Confirmation", getResources().getString(R.string.popup_delete_confirmation,
                        recyclerViewArrayAdapter.getItem(clickedItemPosition)), R.drawable.pdlg_icon_info, R.color.pdlg_color_red);
                dialog.addButton(getResources().getString(R.string.popup_delete_button),
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_red,
                        () -> {
                            deleteModuleDB(recyclerViewArrayAdapter.getItem(clickedItemPosition));
                            activity.displaySnackBar(getResources().getString(R.string.snackbar_delete_module_success), Snackbar.LENGTH_SHORT);
                            dialog.dismiss();
                            updateAdapterList(clickedItemPosition);
                        });
                dialog.addButton(getResources().getString(R.string.popup_cancel_button),
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_gray,
                        dialog::dismiss);
                break;
            }
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteModuleDB(String moduleName) {
        try {
            Thread thread = new Thread(() -> {
                activity.DBInstance.wordDao().deleteByModuleName(moduleName);
            });
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateAdapterList(int removePosition) {
        moduleStringList.remove(removePosition);
        rvModuleListMlf.removeViewAt(removePosition);
        recyclerViewArrayAdapter.notifyItemRemoved(removePosition);
        recyclerViewArrayAdapter.notifyItemRangeChanged(removePosition, moduleStringList.size());
    }

    //TODO:this exists in word guess fragment . centralize it
    private CreateModuleFragment prepareFragment(String moduleName) {
        Bundle bundle = new Bundle();
        bundle.putString("SELECTED_MODULE_KEY", moduleName);
        CreateModuleFragment createModuleFragment = new CreateModuleFragment();
        createModuleFragment.setArguments(bundle);

        return createModuleFragment;
    }

    private WordGuessFragment prepareFragment(int modulePosition) {
        Bundle bundle = new Bundle();
        bundle.putInt("SELECTED_MODULE_POSITION_KEY", modulePosition);
        WordGuessFragment wordGuessFragment = new WordGuessFragment();
        wordGuessFragment.setArguments(bundle);

        return wordGuessFragment;
    }

    @Override
    public void OnModuleClick(int positionClicked) {
        //DONE: put progress bar
        //DONE: make call for getting 1st word translation

        getWordListFromDB(recyclerViewArrayAdapter.getItem(positionClicked));
        activity.sessionVocabulary = new Vocabulary(wordList);

        moduleCall(positionClicked);
    }

    //TODO: maybe positionClicked should be class field
    private void moduleCall(int positionClicked) {
        final String iterationWord = activity.sessionVocabulary.getRandomVocabularyWord();
        if (NO_WORD_IN_LIST.equals(iterationWord)) {
            activity.startTransition(false);
            activity.displayDialog("Error", getResources().getString(R.string.popup_word_missspell_warning), R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
        } else {
            if (activity.isNetworkAvailable()) {
                if (!activity.isProgressBarVisible()) {
                    activity.startTransition(true);
                }
                makeModuleCall(iterationWord, positionClicked);
            } else {
                activity.displayDialog("Error", getResources().getString(R.string.popup_no_internet), R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
            }
        }
    }

    private void makeModuleCall(String iterationWord, int positionClicked) {
        Call<BaseResponse> call = activity.apiInterface.getWordTranslation(iterationWord);

        try {
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    if (response.isSuccessful()) {
                        //activity.sourcePairList.remove(0);
                        //activity.translationPairList.remove(0);
                        if (response.body().getData().get(0).getJapanese().get(0).getReading().isEmpty()) {
                            timesFailedGetTranslation++;

                            activity.sessionVocabulary.removeWord(iterationWord);
                            if (5 == timesFailedGetTranslation) {
                                activity.startTransition(false);
                                activity.displayDialog("Error", getResources().getString(R.string.popup_fail_cannot_get_translation), R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
                            } else {
                                moduleCall(positionClicked);
                            }
                        } else {
                            timesFailedGetTranslation = 0;
                            activity.startTransition(false);

                            /*activity.sourcePairList.add(iterationWord);
                            activity.translationPairList.add(response.body().getData().get(0).getJapanese().get(0).getReading());
                            activity.sessionVocabulary.removeWord(iterationWord);*/
                            activity.prepareDisplayLists(true, iterationWord, response.body().getData().get(0).getJapanese().get(0).getReading());
                            //activity.currentMainFragment = WORD_GUESS_FRAGMENT_TAG;
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                                    .replace(fragmentContainerId, prepareFragment(positionClicked), WORD_GUESS_FRAGMENT_TAG).addToBackStack(null).commit();
                        }
                    } else {
                        activity.startTransition(false);
                        activity.displayDialog("Error", getResources().getString(R.string.popup_no_translation_retrieved), R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    //something wrong with internet
                    activity.startTransition(false);
                    activity.displayDialog("Error", getResources().getString(R.string.popup_no_translation_retrieved_fail_response), R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getWordListFromDB(String moduleToDisplay) {
        wordList = new ArrayList<>();
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

    private List<String> ConvertWordToString(List<Word> wordList) {
        return wordList.stream()
                .map(item -> item.getWord())
                .collect(Collectors.toList());
    }

}