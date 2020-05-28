package com.inguana.vocabularypractice;

import android.animation.Animator;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.material.snackbar.Snackbar;
import com.inguana.vocabularypractice.CustomExtensions.CustomAnimatorListener;
import com.inguana.vocabularypractice.CustomExtensions.OnSwipeTouchListener;
import com.inguana.vocabularypractice.Room.Word;
import com.inguana.vocabularypractice.rest.response.BaseResponse;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;
import com.mikepenz.iconics.view.IconicsTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.inguana.vocabularypractice.MainActivity.WORD_GUESS_FRAGMENT_TAG;
import static com.inguana.vocabularypractice.Vocabulary.NO_WORD_IN_LIST;

public class WordGuessFragment extends Fragment {

    private TextView tvSourceWordFwg, tvDestinationWordFwg;
    private CircularProgressView pbProgressBarFwg;
    private Button btMoveNextWordFwg, btDisplayWordFwg, btRedoFwg, btNextModuleFwg;
    private ImageView ivCurtainOfWordFwg;
    private MainActivity activity;
    private ViewGroup clAddWordToModuleMockLayoutLibi, clWordGuessFragmentFwg;
    private IconicsTextView itvNoWordsMlf, itvAddToModuleLibi;
    private IconicsImageView iivAddToModuleLibi, iivSlideRightFwg, iivSlideLeftFwg;
    private View vLineSeparatorFwg;
    private int fragmentContainerId, currentWordPosition, timesFailedGetTranslation, nextModulePosition;
    private List<String> moduleStringList, wordList;
    private YoYo.YoYoString animationSource, animationCurtain, animationDestination;

    private void initialize(View view, ViewGroup container) {
        tvSourceWordFwg = view.findViewById(R.id.tvSourceWordFwg);
        tvDestinationWordFwg = view.findViewById(R.id.tvDestinationWordFwg);

        btMoveNextWordFwg = view.findViewById(R.id.btMoveNextWordFwg);
        btDisplayWordFwg = view.findViewById(R.id.btDisplayWordFwg);
        btRedoFwg = view.findViewById(R.id.btRedoFwg);
        btNextModuleFwg = view.findViewById(R.id.btNextModuleFwg);

        pbProgressBarFwg = view.findViewById((R.id.pbProgressBarFwg));

        ivCurtainOfWordFwg = view.findViewById(R.id.ivCurtainOfWordFwg);
        itvNoWordsMlf = view.findViewById(R.id.itvNoWordsMlf);
        itvAddToModuleLibi = view.findViewById(R.id.itvTextButtonIconLibi);
        vLineSeparatorFwg = view.findViewById(R.id.vLineSeparatorFwg);
        iivAddToModuleLibi = view.findViewById(R.id.iivButtonIconLibi);
        iivSlideRightFwg = view.findViewById(R.id.iivSlideRightFwg);
        iivSlideLeftFwg = view.findViewById(R.id.iivSlideLeftFwg);

        clAddWordToModuleMockLayoutLibi = view.findViewById(R.id.clButtonLayoutLibi);
        clWordGuessFragmentFwg = view.findViewById(R.id.clWordGuessFragmentFwg);

        fragmentContainerId = container.getId();

        currentWordPosition = 0;
        nextModulePosition = null != getArguments() ? getArguments().getInt("SELECTED_MODULE_POSITION_KEY") + 1 : 0;

        activity = ((MainActivity) getActivity());
        timesFailedGetTranslation = 0;
    }

    //TODO: Fade inOut animation with 2 curtains (one on left one on down)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_guess, container, false);
        initialize(view, container);
        //initializeTextViews();
        setModuleList();
        updateLayoutWithText();
        makePreviousButtonOpaque(true);
        clWordGuessFragmentFwg.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeRight() {
                onClickMoveToPreviousWord();
            }

            public void onSwipeLeft() {
                onClickMoveToNextWord();
            }
        });

        itvAddToModuleLibi.setText(getResources().getString(R.string.fragment_word_guess_add_to_module));
        IconicsDrawable addNewWordIcon = new IconicsDrawable(getContext(), GoogleMaterial.Icon.gmd_playlist_add);
        addNewWordIcon.color(ContextCompat.getColor(getContext(), R.color.pdlg_color_black));
        iivAddToModuleLibi.setIcon(addNewWordIcon);

        addNewWordIcon = new IconicsDrawable(getContext(), CommunityMaterial.Icon.cmd_chevron_double_right);
        addNewWordIcon.color(ContextCompat.getColor(getContext(), R.color.pdlg_color_black));
        addNewWordIcon.setAlpha(200);
        /*addNewWordIcon.sizeDpY(80);
        addNewWordIcon.sizeDpX(100);*/
        iivSlideRightFwg.setIcon(addNewWordIcon);

        addNewWordIcon = new IconicsDrawable(getContext(), CommunityMaterial.Icon.cmd_chevron_double_left);
        addNewWordIcon.color(ContextCompat.getColor(getContext(), R.color.pdlg_color_black));
        addNewWordIcon.setAlpha(200);
        iivSlideLeftFwg.setIcon(addNewWordIcon);

        registerForContextMenu(clAddWordToModuleMockLayoutLibi);
        clAddWordToModuleMockLayoutLibi.setOnClickListener((View iivView) -> {
            //clAddWordToModuleMockLayoutLibi.performLongClick();
            activity.wordToBeAddedToModule = activity.sourcePairList.get(currentWordPosition);
            clAddWordToModuleMockLayoutLibi.showContextMenu();
            //activity.getSupportFragmentManager().beginTransaction().replace(fragmentContainerId, new ModuleListFragment(), MODULE_LIST_FRAGMENT_TAG).addToBackStack(null).commit();
        });

        btDisplayWordFwg.setOnClickListener(view1 -> {
            /*ivCurtainOfWordFwg.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_down));
            ivCurtainOfWordFwg.setVisibility(View.GONE);*/
            if (null != animationCurtain) {
                animationCurtain.stop(true);
            }
            animationCurtain = YoYo.with(Techniques.FadeOutDown)
                    .duration(500)
                    .playOn(ivCurtainOfWordFwg);
        });

        btMoveNextWordFwg.setOnClickListener(view12 -> onClickMoveToNextWord());

        btRedoFwg.setOnClickListener(View3 -> resetCurrentModule());
        btNextModuleFwg.setOnClickListener(View4 -> nextModule());

        return view;
    }

    private void onClickMoveToPreviousWord() {
        if(0 != currentWordPosition) {
            if (1 == currentWordPosition) {
                makePreviousButtonOpaque(true);
            }
            currentWordPosition--;
            updateLayoutWithText();
        }
    }

    private void onClickMoveToNextWord() {

        final String iterationWord = activity.sessionVocabulary.getRandomVocabularyWord();
        if (NO_WORD_IN_LIST.equals(iterationWord)) {
            swapLayout(true);
        } else if (activity.sourcePairList.contains(iterationWord)) {
            makePreviousButtonOpaque(false);
            displayNextWord(iterationWord);
        } else {
            makePreviousButtonOpaque(false);
            makeCallForNextWord(iterationWord);
        }

    }

    private void makePreviousButtonOpaque(boolean makeOpaque) {
        if(null != iivSlideLeftFwg.getIcon()) {
            iivSlideLeftFwg.getIcon().setAlpha(makeOpaque ? 80 : 200);
        }
    }

    private void swapLayout(boolean isEndOfModule) {
        itvNoWordsMlf.setVisibility(isEndOfModule ? View.VISIBLE : View.GONE);
        btRedoFwg.setVisibility(isEndOfModule ? View.VISIBLE : View.GONE);
        btNextModuleFwg.setVisibility(isEndOfModule ? View.VISIBLE : View.GONE);

        tvSourceWordFwg.setVisibility(isEndOfModule ? View.GONE : View.VISIBLE);
        vLineSeparatorFwg.setVisibility(isEndOfModule ? View.GONE : View.VISIBLE);
        btMoveNextWordFwg.setVisibility(isEndOfModule ? View.GONE : View.VISIBLE);
        tvDestinationWordFwg.setVisibility(isEndOfModule ? View.GONE : View.VISIBLE);
        ivCurtainOfWordFwg.setVisibility(isEndOfModule ? View.GONE : View.VISIBLE);
        btDisplayWordFwg.setVisibility(isEndOfModule ? View.GONE : View.VISIBLE);
    }

    private void displayNextWord(String iterationWord) {
        timesFailedGetTranslation = 0;
        currentWordPosition = activity.sourcePairList.indexOf(iterationWord);
        activity.sessionVocabulary.removeWord(iterationWord);
        //activity.getSupportFragmentManager().beginTransaction().replace(fragmentContainerId, passCurrentPositionFragment()).commit();
        updateLayoutWithText();
    }

    private void resetCurrentModule() {
        List<String> newSessionVocabularyList = new ArrayList<>(activity.sourcePairList);
        newSessionVocabularyList.remove(0);
        activity.sessionVocabulary.setVocabularyWords(newSessionVocabularyList);
        currentWordPosition = 0;
        //activity.getSupportFragmentManager().beginTransaction().replace(fragmentContainerId, passCurrentPositionFragment()).commit();
        swapLayout(false);
        updateLayoutWithText();
    }

    private void nextModule() {
        getWordListFromDB(moduleStringList.get(moduleStringList.size() - 2 >= nextModulePosition ? nextModulePosition : 0));
        activity.sessionVocabulary = new Vocabulary(wordList);

        moduleCall();
    }

    private void moduleCall() {
        final String iterationWord = activity.sessionVocabulary.getRandomVocabularyWord();
        if (NO_WORD_IN_LIST.equals(iterationWord)) {
            activity.displayDialog("Error", getResources().getString(R.string.popup_no_word_inside_exists), R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
        } else {
            if (activity.isNetworkAvailable()) {
                if (View.VISIBLE != pbProgressBarFwg.getVisibility()) {
                    pbProgressBarFwg.setVisibility(View.VISIBLE);
                }
                makeModuleCall(iterationWord);
            } else {
                activity.displayDialog("Error", "No internet availableHARDCODEDDDD", R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
            }
        }
    }

    private void makeModuleCall(String iterationWord) {
        Call<BaseResponse> call = activity.apiInterface.getWordTranslation(iterationWord);

        try {
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    pbProgressBarFwg.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        //activity.sourcePairList.remove(0);
                        //activity.translationPairList.remove(0);
                        if (response.body().getData().get(0).getJapanese().get(0).getReading().isEmpty()) {
                            timesFailedGetTranslation++;

                            activity.sessionVocabulary.removeWord(iterationWord);
                            if (5 == timesFailedGetTranslation) {
                                //display refresh message
                                activity.displayDialog("Error", getResources().getString(R.string.popup_fail_cannot_get_translation), R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
                            } else {
                                moduleCall();
                            }
                        } else {
                            timesFailedGetTranslation = 0;

                            /*activity.sourcePairList.add(iterationWord);
                            activity.translationPairList.add(response.body().getData().get(0).getJapanese().get(0).getReading());
                            activity.sessionVocabulary.removeWord(iterationWord);*/
                            activity.prepareDisplayLists(true, iterationWord, response.body().getData().get(0).getJapanese().get(0).getReading());
                            //activity.currentMainFragment = WORD_GUESS_FRAGMENT_TAG;
                            if (nextModulePosition + 1 > moduleStringList.size() - 1) {
                                nextModulePosition = 0;
                            }
                            getActivity().getSupportFragmentManager().beginTransaction().replace(fragmentContainerId, prepareFragment(nextModulePosition), WORD_GUESS_FRAGMENT_TAG).addToBackStack(null).commit();
                        }
                    } else {
                        //onClickMoveToNextWord();
                        activity.displayDialog("Error", "DIDNT get translation", R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    //something wrong with internet
                    pbProgressBarFwg.setVisibility(View.GONE);
                    activity.displayDialog("Error", "DIDNT get translation. Refresh", R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void makeCallForNextWord(String iterationWord) {
        if (activity.isNetworkAvailable()) {
            pbProgressBarFwg.setVisibility(View.VISIBLE);

            final APIInterface apiInterface = JsonGetter.buildService(APIInterface.class);
            Call<BaseResponse> call = apiInterface.getWordTranslation(iterationWord);

            try {
                call.enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        pbProgressBarFwg.setVisibility(View.GONE);
                        if (response.isSuccessful()/*MainActivity.APICode.SUCCESS.getCode() == response.body().getCode()*/) {
                            //activity.sourcePairList.remove(0);
                            //activity.translationPairList.remove(0);

                            //if there is no reading, then pop a fast one and call again. if in 5 times it is not getting anything then display refresh message?
                            if (response.body().getData().get(0).getJapanese().get(0).getReading().isEmpty()) {
                                timesFailedGetTranslation++;

                                activity.sessionVocabulary.removeWord(iterationWord);
                                if (5 == timesFailedGetTranslation) {
                                    //display refresh message
                                    activity.displayDialog("Error", getResources().getString(R.string.popup_fail_cannot_get_translation), R.drawable.pdlg_icon_close, R.color.pdlg_color_red);
                                } else {
                                    onClickMoveToNextWord();
                                }
                            } else {
                                timesFailedGetTranslation = 0;

                                activity.prepareDisplayLists(false, iterationWord, response.body().getData().get(0).getJapanese().get(0).getReading());
                                /*activity.sourcePairList.add(iterationWord);
                                activity.translationPairList.add(response.body().getData().get(0).getJapanese().get(0).getReading());
                                activity.sessionVocabulary.removeWord(iterationWord);*/
                                currentWordPosition++;

                                //getActivity().getSupportFragmentManager().beginTransaction().replace(fragmentContainerId, passCurrentPositionFragment()).addToBackStack("WORD_GUESS_FROM_MENU").commit();
                                updateLayoutWithText();
                            }
                        } else {
                            //TODO: handle fail response
                            //onClickMoveToNextWord();
                        }

                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        //something wrong with internet
                        pbProgressBarFwg.setVisibility(View.GONE);
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

    private WordGuessFragment passCurrentPositionFragment() {
        WordGuessFragment fragment = new WordGuessFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("currentWordPosition", currentWordPosition);

        fragment.setArguments(bundle);
        return fragment;
    }

    private void updateLayoutWithText() {
        //DONE: Make animation and show text
        //TODO: next fragment do it with swipe (no button)
        animationSource = YoYo.with(Techniques.FadeOutLeft)
                .duration(500)
                .withListener(new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (!activity.translationPairList.isEmpty()) {
                            tvSourceWordFwg.setText(activity.sourcePairList.get(currentWordPosition));
                            YoYo.with(Techniques.FadeInRight)
                                    .duration(500)
                                    .playOn(tvSourceWordFwg);
                        }
                    }
                })
                .playOn(tvSourceWordFwg);

        animationCurtain = YoYo.with(Techniques.FadeInRight)
                .duration(1000)
                .playOn(ivCurtainOfWordFwg);

        animationDestination = YoYo.with(Techniques.FadeOutLeft)
                .duration(500)
                .withListener(new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (!activity.translationPairList.isEmpty()) {
                            tvDestinationWordFwg.setText(activity.translationPairList.get(currentWordPosition));
                            YoYo.with(Techniques.FadeInRight)
                                    .duration(500)
                                    .playOn(tvDestinationWordFwg);
                        } else {
                            //display that there is no word available. should refresh (this can go either here or on the source if^^^ it is the same
                        }
                    }
                })
                .playOn(tvDestinationWordFwg);
    }

    private void initializeTextViews() {
        if (!activity.translationPairList.isEmpty()) {
            tvSourceWordFwg.setText(activity.sourcePairList.get(currentWordPosition));
            tvDestinationWordFwg.setText(activity.translationPairList.get(currentWordPosition));
        } else {
            //display that there is no word available. should refresh
        }
    }

    private void setModuleList() {
        new Thread(() -> {
            try {
                moduleStringList = activity.DBInstance.wordDao().getAllModules();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                getActivity().runOnUiThread(() -> {
                    moduleStringList.add("New Module");
                });
            }
        }).start();
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select Module");
        //https://developer.android.com/reference/android/view/Menu#add(int,%20int,%20int,%20int)
        for (int counter = 0; counter <= moduleStringList.size() - 1; counter++) {
            contextMenu.add(0, counter, 0, moduleStringList.get(counter));
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        int clickedItemPosition = item.getOrder(); // plays with getAdapterposition that is commented out in adapter
        int clickedOption = item.getItemId(); // is it edit or delete procedure
        String moduleName;
        if (clickedOption == moduleStringList.size() - 1) {
            //create module
            AtomicBoolean moduleExists = new AtomicBoolean(false);
            moduleName = "New Module" + ThreadLocalRandom.current().nextInt(1, 700);
            new Thread(() -> {
                try {
                    //TODO: improve naming validation
                    if (activity.DBInstance.wordDao().checkIfDuplicate(moduleName)) {
                        moduleExists.set(true);
                    } else {
                        activity.DBInstance.wordDao().insertAllModule(Collections.singletonList(new Word(activity.wordToBeAddedToModule, moduleName)));
                        activity.displaySnackBar(getResources().getString(R.string.snackbar_create_module_success), Snackbar.LENGTH_LONG);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    getActivity().runOnUiThread(() -> {
                        if (moduleExists.get()) {
                            activity.displayDialog("Oops", getResources().getString(R.string.popup_no_module_name_exists), R.drawable.pdlg_icon_info, R.color.pdlg_color_blue);
                        } else {
                            //display message for succesfully adding the word to modules
                        }
                    });
                }
            }).start();
        } else {
            //add word to module
            moduleName = moduleStringList.get(clickedOption);
            new Thread(() -> {
                try {
                    activity.DBInstance.wordDao().insertAllModule(Collections.singletonList(new Word(activity.wordToBeAddedToModule, moduleName)));
                    activity.displaySnackBar(getResources().getString(R.string.snackbar_create_module_success), Snackbar.LENGTH_LONG);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    getActivity().runOnUiThread(() -> {
                        //display message for succesfully adding the word to modules
                    });
                }
            }).start();
        }
        return super.onContextItemSelected(item);
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

    private WordGuessFragment prepareFragment(int modulePosition) {
        Bundle bundle = new Bundle();
        bundle.putInt("SELECTED_MODULE_POSITION_KEY", modulePosition);
        WordGuessFragment wordGuessFragment = new WordGuessFragment();
        wordGuessFragment.setArguments(bundle);

        return wordGuessFragment;
    }

    private List<String> ConvertWordToString(List<Word> wordList) {
        return wordList.stream()
                .map(item -> item.getWord())
                .collect(Collectors.toList());
    }
}
