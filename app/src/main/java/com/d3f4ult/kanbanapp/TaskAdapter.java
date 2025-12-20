package com.d3f4ult.kanbanapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    
    private List<Task> tasks;
    private OnTaskClickListener listener;
    
    public interface OnTaskClickListener {
        void onTaskClick(Task task);
        void onTaskLongClick(Task task);
        void onDeleteClick(Task task);
        void onEditClick(Task task);
        void onStatusChangeClick(Task task);
    }
    
    public TaskAdapter(OnTaskClickListener listener) {
        this.tasks = new ArrayList<>();
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }
    
    @Override
    public int getItemCount() {
        return tasks.size();
    }
    
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void addTask(Task task) {
        tasks.add(task);
        notifyItemInserted(tasks.size() - 1);
    }
    
    public void updateTask(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(task.getId())) {
                tasks.set(i, task);
                notifyItemChanged(i);
                break;
            }
        }
    }
    
    public void removeTask(Task task) {
        int position = tasks.indexOf(task);
        if (position != -1) {
            tasks.remove(position);
            notifyItemRemoved(position);
        }
    }
    
    public List<Task> getTasks() {
        return tasks;
    }
    
    class TaskViewHolder extends RecyclerView.ViewHolder {
        
        private CardView cardView;
        private TextView textTitle;
        private TextView textDescription;
        private TextView textStatus;
        private TextView textPriority;
        private TextView textAssignedTo;
        private TextView textDate;
        private ImageButton buttonEdit;
        private ImageButton buttonDelete;
        private ImageButton buttonChangeStatus;
        private View priorityIndicator;
        
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = itemView.findViewById(R.id.cardViewTask);
            textTitle = itemView.findViewById(R.id.textTaskTitle);
            textDescription = itemView.findViewById(R.id.textTaskDescription);
            textStatus = itemView.findViewById(R.id.textTaskStatus);
            textPriority = itemView.findViewById(R.id.textTaskPriority);
            textAssignedTo = itemView.findViewById(R.id.textTaskAssignedTo);
            textDate = itemView.findViewById(R.id.textTaskDate);
            buttonEdit = itemView.findViewById(R.id.buttonEditTask);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteTask);
            buttonChangeStatus = itemView.findViewById(R.id.buttonChangeStatus);
            priorityIndicator = itemView.findViewById(R.id.priorityIndicator);
        }
        
        public void bind(Task task) {
            textTitle.setText(task.getTitle());
            textDescription.setText(task.getDescription());
            textStatus.setText(task.getStatus().getDisplayName());
            textPriority.setText("Priorité: " + task.getPriorityText());
            
            // Afficher l'assignation si elle existe
            if (task.getAssignedTo() != null && !task.getAssignedTo().isEmpty()) {
                textAssignedTo.setVisibility(View.VISIBLE);
                textAssignedTo.setText("Assigné à: " + task.getAssignedTo());
            } else {
                textAssignedTo.setVisibility(View.GONE);
            }
            
            // Formater et afficher la date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
            if (task.getDueDate() != null) {
                textDate.setVisibility(View.VISIBLE);
                textDate.setText("Échéance: " + dateFormat.format(task.getDueDate()));
                
                // Marquer en rouge si la tâche est en retard
                if (task.isOverdue()) {
                    textDate.setTextColor(Color.RED);
                } else {
                    textDate.setTextColor(Color.GRAY);
                }
            } else {
                textDate.setVisibility(View.GONE);
            }
            
            // Colorer selon le statut
            int statusColor;
            switch (task.getStatus()) {
                case TODO:
                    statusColor = Color.parseColor("#FF6B6B"); // Rouge clair
                    break;
                case IN_PROGRESS:
                    statusColor = Color.parseColor("#4ECDC4"); // Bleu-vert
                    break;
                case DONE:
                    statusColor = Color.parseColor("#95E1D3"); // Vert clair
                    break;
                default:
                    statusColor = Color.GRAY;
            }
            textStatus.setTextColor(statusColor);
            
            // Indicateur de priorité
            int priorityColor;
            switch (task.getPriority()) {
                case 3: // Haute
                    priorityColor = Color.parseColor("#E74C3C");
                    break;
                case 2: // Moyenne
                    priorityColor = Color.parseColor("#F39C12");
                    break;
                case 1: // Basse
                    priorityColor = Color.parseColor("#3498DB");
                    break;
                default:
                    priorityColor = Color.GRAY;
            }
            priorityIndicator.setBackgroundColor(priorityColor);
            
            // Listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(task);
                }
            });
            
            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onTaskLongClick(task);
                }
                return true;
            });
            
            buttonEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(task);
                }
            });
            
            buttonDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(task);
                }
            });
            
            buttonChangeStatus.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStatusChangeClick(task);
                }
            });
        }
    }
}