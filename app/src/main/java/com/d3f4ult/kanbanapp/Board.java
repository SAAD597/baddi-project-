package com.d3f4ult.kanbanapp;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

/**
 * Board Model - Represents a Kanban board/project
 */
public class Board {
    private String projectId;
    private String title;
    private String ownerId;
    @ServerTimestamp
    private Date createdAt;

    // Default constructor (required for Firestore deserialization)
    public Board() {
    }

    // Constructor
    public Board(String title, String ownerId) {
        this.title = title;
        this.ownerId = ownerId;
        this.createdAt = new Date();
    }

    // Getters and Setters
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
