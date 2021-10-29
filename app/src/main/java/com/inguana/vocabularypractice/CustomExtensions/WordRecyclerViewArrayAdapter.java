package com.inguana.vocabularypractice.CustomExtensions;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.inguana.vocabularypractice.R;
import com.inguana.vocabularypractice.Room.Word;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WordRecyclerViewArrayAdapter extends RecyclerView.Adapter<WordRecyclerViewArrayAdapter.CustomViewHolder> {

    private List<String> wordList;
    private Context context;
    public static final String ADD_BUTTON_NAME_INDICATOR = "###BUTTON###";

    public List<String> getWordList() {
        return wordList;
    }

    //Get the current module list with Word items and without the add button.
    public List<Word> getCleanWordList(String moduleName) {//TODO: check with empty list
        return wordList.stream()
                .limit(wordList.size() - 1)
                .filter(item -> !item.isEmpty())
                .map(item -> new Word(item, moduleName))
                .collect(Collectors.toList());
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class CustomViewHolder extends RecyclerView.ViewHolder {
        EditText editText;
        IconicsImageView iconicsImageView;
        int position;
        WordRecyclerViewArrayAdapter currentRVAdapter;

        // Provide a suitable constructor (depends on the kind of dataset)
        CustomViewHolder(View view, List<String> wordList) {
            super(view);
            editText = view.findViewById(R.id.etEditWordRvi);

            editText.setOnFocusChangeListener((activeView, hasFocus) -> {
                if (!hasFocus) {
                    wordList.set(getAdapterPosition(), editText.getText().toString());
                }
            });
        }

        CustomViewHolder(View view, List<String> wordList, Context context, WordRecyclerViewArrayAdapter currentRVAdapter) {
            super(view);
            iconicsImageView = view.findViewById(R.id.iivAddIconRvai);

            this.currentRVAdapter = currentRVAdapter;
            int adaptPos = getAdapterPosition();

            IconicsDrawable addNewWordIcon = new IconicsDrawable(context, GoogleMaterial.Icon.gmd_add_circle);
            addNewWordIcon.setColorList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.pdlg_color_blue)));
            /*IconicsDrawable(this, FontAwesome.Icon.faw_adjust).apply {
                sizeDp = 24
                paddingDp = 1
            }*/
            //addNewWordIcon.color(ContextCompat.getColor(context, R.color.pdlg_color_blue));
            iconicsImageView.setIcon(addNewWordIcon);
            iconicsImageView.setOnClickListener((View iivView) -> addWordItem(""));
        }

        private void addWordItem(String word) {
            currentRVAdapter.getWordList().add(word);

            Collections.swap(currentRVAdapter.getWordList(), getAdapterPosition(), currentRVAdapter.getWordList().size() - 1);
            currentRVAdapter.notifyItemMoved(currentRVAdapter.getWordList().size() - 2, currentRVAdapter.getWordList().size() - 1);
        }
    }

    public WordRecyclerViewArrayAdapter(List<String> wordList, Context context) {
        this.wordList = wordList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position == wordList.size() - 1 ? 0 : 1;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override// Create new views (invoked by the layout manager)
    public WordRecyclerViewArrayAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //create a new view
        View view;
        CustomViewHolder viewHolder;
        if (0 == viewType) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_add_item, parent, false);
            viewHolder = new CustomViewHolder(view, wordList, context, this);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_edittextview_item, parent, false);
            viewHolder = new CustomViewHolder(view, wordList);
        }

        return viewHolder;
    }

    @Override// Replace the contents of a view (invoked by the layout manager)
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        if (wordList.size() - 1 != position) {
            holder.editText.setTag(position);
            holder.editText.setText(wordList.get(position));
            holder.position = position;
            //holder.setIsRecyclable(false);
        } else {
            //holder.setIsRecyclable(false);
        }
    }

    @Override// Return the size of your dataset (invoked by the layout manager)
    public int getItemCount() {
        return wordList.size();
    }
}
