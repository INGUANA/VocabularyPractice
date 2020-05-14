package com.inguana.vocabularypractice;

import android.animation.Animator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.inguana.vocabularypractice.CustomExtensions.CustomAnimatorListener;
import com.inguana.vocabularypractice.rest.response.BaseResponse;
import com.mikepenz.iconics.view.IconicsTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.inguana.vocabularypractice.Vocabulary.NO_WORD_IN_LIST;

public class WordGuessFragment extends Fragment {

    private TextView tvSourceWordFwg, tvDestinationWordFwg;
    private CircularProgressView pbProgressBarFwg;
    private Button btMoveNextWordFwg, btDisplayWordFwg, btRedoFwg, btChooseModuleFwg;
    private ImageView ivCurtainOfWordFwg;
    private MainActivity activity;
    private IconicsTextView itvNoModulesMlf;
    private View vLineSeparatorFwg;
    private int fragmentContainerId, currentWordPosition;
    private YoYo.YoYoString animationSource, animationCurtain, animationDestination;

    private void initialize(View view, ViewGroup container) {
        tvSourceWordFwg = view.findViewById(R.id.tvSourceWordFwg);
        tvDestinationWordFwg = view.findViewById(R.id.tvDestinationWordFwg);

        btMoveNextWordFwg = view.findViewById(R.id.btMoveNextWordFwg);
        btDisplayWordFwg = view.findViewById(R.id.btDisplayWordFwg);
        btRedoFwg = view.findViewById(R.id.btRedoFwg);
        btChooseModuleFwg = view.findViewById(R.id.btChooseModuleFwg);

        pbProgressBarFwg = view.findViewById((R.id.pbProgressBarFwg));

        ivCurtainOfWordFwg = view.findViewById(R.id.ivCurtainOfWordFwg);
        itvNoModulesMlf = view.findViewById(R.id.itvNoModulesMlf);
        vLineSeparatorFwg = view.findViewById(R.id.vLineSeparatorFwg);

        fragmentContainerId = container.getId();
        currentWordPosition = null != getArguments() ? getArguments().getInt("currentWordPosition") : 0;

        activity = ((MainActivity) getActivity());
    }

    //TODO: Fade inOut animation with 2 curtains (one on left one on down)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_guess, container, false);
        initialize(view, container);
        //initializeTextViews();
        updateLayoutWithText();

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

        return view;
    }

    private void onClickMoveToNextWord() {

        final String iterationWord = activity.sessionVocabulary.getRandomVocabularyWord();
        if (NO_WORD_IN_LIST.equals(iterationWord)) {
            //TODO: make end credits, and return to where u came from button
            itvNoModulesMlf.setVisibility(View.VISIBLE);
            btRedoFwg.setVisibility(View.VISIBLE);
            btChooseModuleFwg.setVisibility(View.VISIBLE);

            tvSourceWordFwg.setVisibility(View.GONE);
            vLineSeparatorFwg.setVisibility(View.GONE);
            btMoveNextWordFwg.setVisibility(View.GONE);
            tvDestinationWordFwg.setVisibility(View.GONE);
            ivCurtainOfWordFwg.setVisibility(View.GONE);
            btDisplayWordFwg.setVisibility(View.GONE);
        } else if (activity.sourcePairList.contains(iterationWord)) {
            displayNextWord(iterationWord);
        } else {
            makeCallForNextWord(iterationWord);
        }

    }

    private void displayNextWord(String iterationWord) {
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
        updateLayoutWithText();
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

                            activity.sourcePairList.add(iterationWord);
                            activity.translationPairList.add(response.body().getData().get(0).getJapanese().get(0).getReading());
                            activity.sessionVocabulary.removeWord(iterationWord);
                            currentWordPosition++;
                            //TODO: try pop backstack or edit transaction

                            //getActivity().getSupportFragmentManager().beginTransaction().replace(fragmentContainerId, passCurrentPositionFragment()).addToBackStack("WORD_GUESS_FROM_MENU").commit();
                            updateLayoutWithText();
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
        //TODO: Make animation and show text
        //TODO: next fragment do it with swipe (no button)
        animationSource = YoYo.with(Techniques.FadeOutRight)
                .duration(500)
                .withListener(new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (!activity.translationPairList.isEmpty()) {
                            tvSourceWordFwg.setText(activity.sourcePairList.get(currentWordPosition));
                            YoYo.with(Techniques.FadeInLeft)
                                    .duration(500)
                                    .playOn(tvSourceWordFwg);
                        }
                    }
                })
                .playOn(tvSourceWordFwg);

        animationCurtain = YoYo.with(Techniques.FadeInRight)
                .duration(1000)
                .playOn(ivCurtainOfWordFwg);

        animationDestination = YoYo.with(Techniques.FadeOutRight)
                .duration(500)
                .withListener(new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (!activity.translationPairList.isEmpty()) {
                            tvDestinationWordFwg.setText(activity.translationPairList.get(currentWordPosition));
                            YoYo.with(Techniques.FadeInLeft)
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
}
