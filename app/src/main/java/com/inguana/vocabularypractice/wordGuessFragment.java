package com.inguana.vocabularypractice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class wordGuessFragment extends Fragment {

    private Button btMainMenuMmf, btRevealMmf;
    private TextView tvSourceWordMmf, tvTranslatedWordMmf;
    private int fragmentContainerId;

    private void initialize(View view, ViewGroup container) {
        btMainMenuMmf = view.findViewById(R.id.btMainMenuMmf);
        btRevealMmf = view.findViewById(R.id.btRevealMmf);

        tvSourceWordMmf = view.findViewById(R.id.tvSourceWordMmf);
        tvTranslatedWordMmf = view.findViewById(R.id.tvTranslatedWordMmf);

        fragmentContainerId = container.getId();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_word_guess, container, false);
        initialize(view, container);

        return view;

        // TODO: set button listeners
    }

    //TODO: define swipe gesture to move to next word
}
