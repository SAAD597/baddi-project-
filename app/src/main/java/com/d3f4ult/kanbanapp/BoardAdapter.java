package com.d3f4ult.kanbanapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {

    private List<Board> boardsList;
    private OnBoardClickListener listener;
    private SimpleDateFormat dateFormat;

    // Interface pour gérer les clics
    public interface OnBoardClickListener {
        void onBoardClick(Board board);
    }

    public BoardAdapter(List<Board> boardsList, OnBoardClickListener listener) {
        this.boardsList = boardsList;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.FRENCH);
    }

    @NonNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_board, parent, false);
        return new BoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardViewHolder holder, int position) {
        Board board = boardsList.get(position);

        // Bind des données
        holder.textTitle.setText(board.getTitle());
        holder.textDate.setText(dateFormat.format(board.getCreatedAt()));
        holder.textStatus.setText(board.getStatus());

        // Couleur du statut
        int statusColor = getStatusColor(board.getStatus());
        holder.textStatus.setTextColor(statusColor);

        // Gestion du clic
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBoardClick(board);
            }
        });
    }

    @Override
    public int getItemCount() {
        return boardsList != null ? boardsList.size() : 0;
    }

    /**
     * Retourne la couleur selon le statut
     */
    private int getStatusColor(String status) {
        switch (status) {
            case "En cours":
                return 0xFF4CAF50; // Vert
            case "Terminé":
                return 0xFF2196F3; // Bleu
            case "En attente":
                return 0xFFFF9800; // Orange
            default:
                return 0xFF9E9E9E; // Gris
        }
    }

    /**
     * Met à jour la liste des boards
     */
    public void updateBoards(List<Board> newBoards) {
        this.boardsList = newBoards;
        notifyDataSetChanged();
    }

    static class BoardViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textTitle;
        TextView textDate;
        TextView textStatus;

        public BoardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewBoard);
            textTitle = itemView.findViewById(R.id.textBoardTitle);
            textDate = itemView.findViewById(R.id.textBoardDate);
            textStatus = itemView.findViewById(R.id.textBoardStatus);
        }
    }
}
