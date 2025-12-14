package com.d3f4ult.kanbanapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * BoardAdapter - Custom RecyclerView adapter for displaying boards
 */
public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {

    private List<Board> boardList;
    private OnBoardClickListener clickListener;

    // Interface for click events
    public interface OnBoardClickListener {
        void onBoardClick(Board board);
    }

    public BoardAdapter(List<Board> boardList, OnBoardClickListener clickListener) {
        this.boardList = boardList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_board, parent, false);
        return new BoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardViewHolder holder, int position) {
        Board board = boardList.get(position);
        holder.bind(board, clickListener);
    }

    @Override
    public int getItemCount() {
        return boardList.size();
    }

    /**
     * ViewHolder for individual board items
     */
    public static class BoardViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDate;

        public BoardViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewBoardTitle);
            textViewDate = itemView.findViewById(R.id.textViewBoardDate);
        }

        public void bind(Board board, OnBoardClickListener clickListener) {
            textViewTitle.setText(board.getTitle());

            // Format and display creation date
            if (board.getCreatedAt() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(board.getCreatedAt());
                textViewDate.setText("Created: " + formattedDate);
            }

            // Handle item click
            itemView.setOnClickListener(v -> clickListener.onBoardClick(board));
        }
    }
}
