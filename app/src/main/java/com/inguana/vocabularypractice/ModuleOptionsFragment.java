package com.inguana.vocabularypractice;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ModuleOptionsFragment extends Fragment {

    private int fragmentContainerId;

    private Dialog overlayDialog;
    private MainActivity activity;
    private Button btStartModMof, btCreateModMof, btUpdateModMof;


    public void initialize(View view, ViewGroup container) {
        btStartModMof = view.findViewById(R.id.btStartModMof);
        btCreateModMof = view.findViewById(R.id.btCreateModMof);
        btUpdateModMof = view.findViewById(R.id.btUpdateModMof);

        fragmentContainerId = container.getId();

        overlayDialog = new Dialog(getContext(), R.style.Theme_AppCompat_Dialog_Transparent);
        overlayDialog.setCancelable(true);

        activity = ((MainActivity) getActivity());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_module_options, container, false);
        initialize(view, container);

        btCreateModMof.setOnClickListener(view1 -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(fragmentContainerId, new CreateModuleFragment()).commit();
        });

        return view;
    }


}
