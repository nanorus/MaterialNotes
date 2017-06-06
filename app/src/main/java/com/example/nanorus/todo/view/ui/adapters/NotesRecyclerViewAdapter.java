package com.example.nanorus.todo.view.ui.adapters;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        int priority = mNotes.get(position).getPriority();
        holder.name.setText(mNotes.get(position).getName());
        holder.priority.setText(String.valueOf(priority));
        switch (priority){
            case 1:
                holder.priority_color.setBackgroundColor(Color.parseColor("#E91E63"));
                break;
            case 2:
                holder.priority_color.setBackgroundColor(Color.parseColor("#9C27B0"));
                break;
            case 3:
                holder.priority_color.setBackgroundColor(Color.parseColor("#2196F3"));
                break;
            default:
                holder.priority_color.setBackgroundColor(Color.parseColor("#2196F3"));
                break;
        }


    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    class NotesRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView priority;
        ImageView priority_color;

        public NotesRecyclerViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.list_item_note_name);
            priority = (TextView) itemView.findViewById(R.id.list_item_note_tv_priority);
            priority_color = (ImageView) itemView.findViewById(R.id.list_item_note_iv_priority_color);

        }
    }
}
