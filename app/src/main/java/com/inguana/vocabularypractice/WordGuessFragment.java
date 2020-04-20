package com.inguana.vocabularypractice;

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

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.inguana.vocabularypractice.rest.response.BaseResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WordGuessFragment extends Fragment {

    private TextView tvSourceWordFwg, tvDestinationWordFwg;
    private CircularProgressView pbProgressBarFwg;
    private Button btMoveNextWordFwg, btDisplayWordFwg;
    private ImageView ivCurtainOfWordFwg;
    private MainActivity activity;
    private int fragmentContainerId;

    private void initialize(View view, ViewGroup container) {
        tvSourceWordFwg = view.findViewById(R.id.tvSourceWordFwg);
        tvDestinationWordFwg = view.findViewById(R.id.tvDestinationWordFwg);

        btMoveNextWordFwg = view.findViewById(R.id.btMoveNextWordFwg);
        btDisplayWordFwg = view.findViewById(R.id.btDisplayWordFwg);

        pbProgressBarFwg = view.findViewById((R.id.pbProgressBarFwg));

        ivCurtainOfWordFwg = view.findViewById(R.id.ivCurtainOfWordFwg);

        fragmentContainerId = container.getId();

        activity = ((MainActivity) getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_guess, container, false);
        initialize(view, container);
        initializeTextViews();

        btDisplayWordFwg.setOnClickListener(view1 -> {
            ivCurtainOfWordFwg.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_down));
            ivCurtainOfWordFwg.setVisibility(View.GONE);

        });

        btMoveNextWordFwg.setOnClickListener(view12 -> onClickMoveToNextWord());


        return view;
    }

    private void onClickMoveToNextWord() {
        //check which word is on display, remove it from list.
        //activity.translationPairList.remove();

        //make async call for word, check if it is jap word, move on to same fragment.

        if(activity.isNetworkAvailable()) {
            pbProgressBarFwg.setVisibility(View.VISIBLE);

            final APIInterface apiInterface = JsonGetter.buildService(APIInterface.class);

            final String iterationWord = activity.sessionVocabulary.getRandomVocabularyWord();
            Call<BaseResponse> call = apiInterface.getWordTranslation(iterationWord);

            try {
                call.enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        pbProgressBarFwg.setVisibility(View.GONE);
                        if (response.isSuccessful()/*MainActivity.APICode.SUCCESS.getCode() == response.body().getCode()*/) {
                            activity.sourcePairList.remove(0);
                            activity.translationPairList.remove(0);

                            activity.sourcePairList.add(iterationWord);
                            activity.translationPairList.add(response.body().getData().get(0).getJapanese().get(0).getReading());
                            activity.sessionVocabulary.removeWord(iterationWord);

                            getActivity().getSupportFragmentManager().beginTransaction().replace(fragmentContainerId, new WordGuessFragment()).commit();
                        } else {
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

    private void  initializeTextViews() {
        if(!activity.translationPairList.isEmpty()) {
            tvSourceWordFwg.setText(activity.sourcePairList.get(0));
            tvDestinationWordFwg.setText(activity.translationPairList.get(0));
        } else {
            //display that there is no word available. should refresh
        }
    }
}
