package com.d3f4ult.kanbanapp;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {
    
    private String id;
    private String title;
    private String description;
    private TaskStatus status;
    private String assignedTo;
    private Date createdDate;
    private Date dueDate;
    private int priority; // 1=Basse, 2=Moyenne, 3=Haute
    
    // Énumération pour les statuts des tâches
    public enum TaskStatus {
        TODO("À faire"),
        IN_PROGRESS("En cours"),
        DONE("Terminé");
        
        private String displayName;
        
        TaskStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Constructeur par défaut
    public Task() {
        this.id = generateId();
        this.createdDate = new Date();
        this.status = TaskStatus.TODO;
        this.priority = 2; // Moyenne par défaut
    }
    
    // Constructeur avec paramètres
    public Task(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }
    
    // Constructeur complet
    public Task(String title, String description, TaskStatus status, String assignedTo, int priority) {
        this();
        this.title = title;
        this.description = description;
        this.status = status;
        this.assignedTo = assignedTo;
        this.priority = priority;
    }
    
    // Génération d'un ID unique
    private String generateId() {
        return "TASK_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
    
    // Getters
    public String getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public TaskStatus getStatus() {
        return status;
    }
    
    public String getAssignedTo() {
        return assignedTo;
    }
    
    public Date getCreatedDate() {
        return createdDate;
    }
    
    public Date getDueDate() {
        return dueDate;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public String getPriorityText() {
        switch (priority) {
            case 1: return "Basse";
            case 2: return "Moyenne";
            case 3: return "Haute";
            default: return "Non définie";
        }
    }
    
    // Setters
    public void setId(String id) {
        this.id = id;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    
    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
    
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    // Méthodes utilitaires
    public void moveToNextStatus() {
        switch (status) {
            case TODO:
                status = TaskStatus.IN_PROGRESS;
                break;
            case IN_PROGRESS:
                status = TaskStatus.DONE;
                break;
            case DONE:
                // Reste à DONE
                break;
        }
    }
    
    public void moveToPreviousStatus() {
        switch (status) {
            case DONE:
                status = TaskStatus.IN_PROGRESS;
                break;
            case IN_PROGRESS:
                status = TaskStatus.TODO;
                break;
            case TODO:
                // Reste à todo
                break;
        }
    }
    
    public boolean isOverdue() {
        if (dueDate == null || status == TaskStatus.DONE) {
            return false;
        }
        return dueDate.before(new Date());
    }
    
    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                '}';
    }
}