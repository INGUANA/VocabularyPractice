package com.inguana.vocabularypractice.CustomExtensions;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.inguana.vocabularypractice.R;
import com.inguana.vocabularypractice.Room.Word;

import java.util.List;
import java.util.stream.Collectors;

public class ModuleRecyclerViewArrayAdapter extends RecyclerView.Adapter<ModuleRecyclerViewArrayAdapter.CustomViewHolder> {

    private List<String> moduleList;
    private boolean isFirstHolder;
    private Fragment fragment;

    public List<String> getModuleList() {
        return moduleList;
    }

    //DO I NEED THIS????
    public List<Word> getCleanWordList(String moduleName) {
        return moduleList.stream()
                .skip(moduleList.size() - 1)
                .map(item -> new Word(item, moduleName))
                .collect(Collectors.toList());
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView textView;
        WordRecyclerViewArrayAdapter currentRVAdapter;
        Fragment fragment;

        // Provide a suitable constructor (depends on the kind of dataset)
        CustomViewHolder(View view, List<String> wordList, Fragment fragment) {
            super(view);
            this.fragment = fragment;

            textView = view.findViewById(R.id.tvModuleNameRvti);

            view.setOnCreateContextMenuListener(this);
            textView.setOnClickListener(view1 -> {
                //TODO: Start module
                int i = 0;
            });
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select Action");
            //https://developer.android.com/reference/android/view/Menu#add(int,%20int,%20int,%20int)
            contextMenu.add(0, 0, getAdapterPosition(), fragment.getResources().getString(R.string.fragment_module_list_edit_module));
            contextMenu.add(0, 1, getAdapterPosition(), fragment.getResources().getString(R.string.fragment_module_list_delete_module));
        }
    }

    public ModuleRecyclerViewArrayAdapter(List<String> moduleList, Fragment fragment) {
        this.moduleList = moduleList;
        isFirstHolder = true;
        this.fragment = fragment;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override// Create new views (invoked by the layout manager)
    public ModuleRecyclerViewArrayAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        ModuleRecyclerViewArrayAdapter.CustomViewHolder viewHolder;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_textview_item, parent, false);
        viewHolder = new ModuleRecyclerViewArrayAdapter.CustomViewHolder(view, moduleList, fragment);

        return viewHolder;
    }

    @Override// Replace the contents of a view (invoked by the layout manager)
    public void onBindViewHolder(@NonNull ModuleRecyclerViewArrayAdapter.CustomViewHolder holder, int position) {
        holder.textView.setText(moduleList.get(position));
    }

    @Override// Return the size of your dataset (invoked by the layout manager)
    public int getItemCount() {
        return moduleList.size();
    }
}
