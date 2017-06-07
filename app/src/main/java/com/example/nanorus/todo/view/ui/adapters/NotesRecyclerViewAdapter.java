package com.example.nanorus.todo.view.ui.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
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

        return  new NotesRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotesRecyclerViewHolder holder, int position) {
        int priority = mNotes.get(position).getPriority();
        String name = mNotes.get(position).getName();
        holder.name.setText(name);
        holder.priority.setText(String.valueOf(priority));
        int color;
        switch (priority) {
            case 1:
                color = Color.parseColor("#E91E63");
                break;
            case 2:
                color = Color.parseColor("#9C27B0");
                break;
            case 3:
                color = Color.parseColor("#2196F3");
                break;
            default:
                color = Color.parseColor("#2196F3");
                break;
        }

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(name.charAt(0)), color);

        holder.drawText.setImageDrawable(drawable);

    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    class NotesRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView priority;
        ImageView drawText;

        public NotesRecyclerViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.list_item_note_name);
            priority = (TextView) itemView.findViewById(R.id.list_item_note_tv_priority);
            drawText = (ImageView) itemView.findViewById(R.id.list_item_note_iv_drawText);
        }
    }
}
