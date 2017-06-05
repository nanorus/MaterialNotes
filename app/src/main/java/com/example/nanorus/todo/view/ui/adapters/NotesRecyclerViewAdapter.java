package com.example.nanorus.todo.view.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nanorus.nanojunior.R;
import com.example.nanorus.todo.model.pojo.NoteRecyclerPojo;

import java.util.List;

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<NotesRecyclerViewAdapter.NotesRecyclerViewHolder> {
    List<NoteRecyclerPojo> mNotes;

    public NotesRecyclerViewAdapter(List<NoteRecyclerPojo> notes) {
        mNotes = notes;
    }

    @Override
    public NotesRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_note, parent, false);
        NotesRecyclerViewHolder holder = new NotesRecyclerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(NotesRecyclerViewHolder holder, int position) {
        holder.name.setText(mNotes.get(position).getName());
        holder.priority.setText(String.valueOf(mNotes.get(position).getPriority()));
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    class NotesRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView priority;

        public NotesRecyclerViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.list_item_note_name);
            priority = (TextView) itemView.findViewById(R.id.list_item_note_tv_priority);
        }
    }
}
