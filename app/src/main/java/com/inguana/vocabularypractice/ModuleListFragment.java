package com.inguana.vocabularypractice;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.inguana.vocabularypractice.CustomExtensions.WordRecyclerViewArrayAdapter.ADD_BUTTON_NAME_INDICATOR;

public class ModuleListFragment extends Fragment {

    private int fragmentContainerId;

    private Dialog overlayDialog;
    private MainActivity activity;
    private Button btStartModMof, btCreateModMof, btEditModMof;
    private RecyclerView rvModuleListMlf;
    private CircularProgressView pbProgressBarMmf;
    private LinearLayoutManager layoutManager;
    private ModuleRecyclerViewArrayAdapter recyclerViewArrayAdapter;
    private List<String> stringList;
    private IconicsImageView iivNewModuleIconMlf;
    private ConstraintLayout clNewModuleMlf;

    public void initialize(View view, ViewGroup container) {
        fragmentContainerId = container.getId();

        overlayDialog = new Dialog(getContext(), R.style.Theme_AppCompat_Dialog_Transparent);
        overlayDialog.setCancelable(true);

        pbProgressBarMmf = view.findViewById(R.id.pbProgressBarMmf);
        rvModuleListMlf = view.findViewById(R.id.rvModuleListMlf);
        clNewModuleMlf = view.findViewById(R.id.clNewModuleMlf);

        iivNewModuleIconMlf = view.findViewById(R.id.iivNewModuleIconMlf);

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

        clNewModuleMlf.setOnClickListener(view1 -> getActivity().getSupportFragmentManager().beginTransaction().replace(fragmentContainerId, new CreateModuleFragment()).commit());

        setModuleList();

        return view;
    }

    private void setModuleList() {
        new Thread(() -> {
            try {
                stringList = activity.DBInstance.wordDao().getAllModules();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                getActivity().runOnUiThread(() -> {
                    if(stringList.isEmpty()){
                        //TODO: show no module msg
                    } else {
                        recyclerViewArrayAdapter = new ModuleRecyclerViewArrayAdapter(stringList, this);
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
                //TODO: do edit
                activity.displaySnackBar("Clicked Edit option", Snackbar.LENGTH_SHORT);
                break;
            }
            case 1: {
                //TODO: do delete
                activity.displaySnackBar("Clicked Delete option", Snackbar.LENGTH_SHORT);
                break;
            }
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
}
