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

public class MainMenuFragment extends Fragment {

    public enum TranslationMode {
        JapaneseToEnglish, EnglishToJapanese;
    }

    private int fragmentContainerId;

    private Button  btStartMmf, btSettingsMmf, btExitMmf;
    private ImageButton ibSettingsInformationMmf;
    private TranslationMode translationMode;
    private TextView tvTooltipTextMmf;

    public void initialize(View view, ViewGroup container) {
        btStartMmf = view.findViewById(R.id.btStartMmf);
        btSettingsMmf = view.findViewById(R.id.btSettingsMmf);
        btExitMmf = view.findViewById(R.id.btExitMmf);

        tvTooltipTextMmf = view.findViewById(R.id.tvTooltipTextMmf);

        ibSettingsInformationMmf = view.findViewById(R.id.ibSettingsInformationMmf);

        translationMode = TranslationMode.EnglishToJapanese;

        fragmentContainerId = container.getId();
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
                //TODO: initialize next fragment, close this one
                //translationMode in main activity maybe
                Bundle startButtonBundle = new Bundle();
                startButtonBundle.putSerializable("translationMode", translationMode);
                MainMenuFragment mainMenuFragment = new MainMenuFragment();
                mainMenuFragment.setArguments(startButtonBundle);
                //getActivity().getSupportFragmentManager().beginTransaction().replace(fragmentContainerId, mainMenuFragment).commit();
                //make it as modular as possible, here comes the new curent fragment declaration, but is there another way besides ((MainActivity)getActivity().....
            }
        });

        btSettingsMmf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: swap mode of vocabulary practice
                btSettingsMmf.setText(btSettingsMmf.getText().toString().equals(getString(R.string.fragment_main_menu_english_to_japanese))
                        ? getString(R.string.fragment_main_menu_japanese_to_english)
                        : getString(R.string.fragment_main_menu_english_to_japanese));

                translationMode = translationMode == TranslationMode.JapaneseToEnglish
                        ? TranslationMode.EnglishToJapanese
                        : TranslationMode.JapaneseToEnglish;
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
                //TODO: show tooltip *Research*
                if(View.VISIBLE != tvTooltipTextMmf.getVisibility()) {
                    tvTooltipTextMmf.setVisibility(View.VISIBLE);
                    Dialog dialog = new Dialog(getContext());
                    //make overlay dialog
                }
            }
        });

        return view;
    }
}
