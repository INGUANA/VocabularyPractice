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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
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

public class ModuleListFragment extends Fragment implements ModuleRecyclerViewArrayAdapter.OnModuleClickListener {

    private int fragmentContainerId;

    private Dialog overlayDialog;
    private MainActivity activity;
    private RecyclerView rvModuleListMlf;
    private CircularProgressView pbProgressBarMmf;
    private LinearLayoutManager layoutManager;
    private ModuleRecyclerViewArrayAdapter recyclerViewArrayAdapter;
    private List<String> moduleStringList, wordList;
    private IconicsImageView iivNewModuleIconMlf;
    private ConstraintLayout clNewModuleMlf;
    private IconicsTextView itvNoModulesMlf;

    public void initialize(View view, ViewGroup container) {
        fragmentContainerId = container.getId();

        overlayDialog = new Dialog(getContext(), R.style.Theme_AppCompat_Dialog_Transparent);
        overlayDialog.setCancelable(true);

        pbProgressBarMmf = view.findViewById(R.id.pbProgressBarMmf);
        rvModuleListMlf = view.findViewById(R.id.rvModuleListMlf);
        clNewModuleMlf = view.findViewById(R.id.clNewModuleMlf);

        iivNewModuleIconMlf = view.findViewById(R.id.iivNewModuleIconMlf);
        itvNoModulesMlf = view.findViewById(R.id.itvNoModulesMlf);

        activity = ((MainActivity) getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_module_list, container, false);
        initialize(view, container);

        rvModuleListMlf.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        rvModuleListMlf.setLayoutManager(layoutManager);

        IconicsDrawable addNewWordIcon = new IconicsDrawable(getContext(), GoogleMaterial.Icon.gmd_add);
        addNewWordIcon.color(ContextCompat.getColor(getContext(), R.color.pdlg_color_black));
        iivNewModuleIconMlf.setIcon(addNewWordIcon);

        clNewModuleMlf.setOnClickListener(view1 -> {
            activity.currentMainFragment = CREATE_MODULE_FRAGMENT_TAG;
            getActivity().getSupportFragmentManager().beginTransaction().replace(fragmentContainerId, new CreateModuleFragment(), CREATE_MODULE_FRAGMENT_TAG).addToBackStack(null).commit();
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

                activity.getSupportFragmentManager().beginTransaction().replace(fragmentContainerId,
                        prepareFragment(recyclerViewArrayAdapter.getItem(clickedItemPosition))).addToBackStack(null).commit();
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

    private CreateModuleFragment prepareFragment(String moduleName) {
        Bundle bundle = new Bundle();
        bundle.putString("SELECTED_MODULE_KEY", moduleName);
        CreateModuleFragment createModuleFragment = new CreateModuleFragment();
        createModuleFragment.setArguments(bundle);

        return createModuleFragment;
    }

    @Override
    public void OnModuleClick(int positionClicked) {
        //TODO: put progress bar
        //DONE: make call for getting 1st word translation

        getWordListFromDB(recyclerViewArrayAdapter.getItem(positionClicked));
        activity.sessionVocabulary = new Vocabulary(wordList);
        final String iterationWord = activity.sessionVocabulary.getRandomVocabularyWord();
        if (NO_WORD_IN_LIST.equals(iterationWord)) {
            activity.displayDialog("Error", getResources().getString(R.string.popup_no_word_inside_exists), R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
        } else {
            makeModuleCall(iterationWord);
        }
    }

    private void makeModuleCall(String iterationWord) {
        if (activity.isNetworkAvailable()) {
            //pbProgressBarMmf.setVisibility(View.VISIBLE);

            Call<BaseResponse> call = activity.apiInterface.getWordTranslation(iterationWord);

            try {
                call.enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        //pbProgressBarMmf.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            //activity.sourcePairList.remove(0);
                            //activity.translationPairList.remove(0);

                            activity.sourcePairList.add(iterationWord);
                            activity.translationPairList.add(response.body().getData().get(0).getJapanese().get(0).getReading());
                            activity.sessionVocabulary.removeWord(iterationWord);

                            //TODO: Make WordGuess fragment to handle words differently (adding many words to backstack wont be good)
                            activity.currentMainFragment = WORD_GUESS_FRAGMENT_TAG;
                            getActivity().getSupportFragmentManager().beginTransaction().replace(fragmentContainerId, new WordGuessFragment(), WORD_GUESS_FRAGMENT_TAG).addToBackStack(null).commit();
                        } else {
                            //onClickMoveToNextWord();
                            activity.displayDialog("Error", "DIDNT get translation", R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        //something wrong with internet
                        //pbProgressBarMmf.setVisibility(View.GONE);
                        activity.displayDialog("Error", "DIDNT get translation. Refresh", R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            activity.displayDialog("Error", "No internet availableHARDCODEDDDD", R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
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
