package com.prova.hackaton_parte_1.data.model;

import com.google.firebase.firestore.DocumentId;

public class Post {
    @DocumentId private String id;
    private String imageUrl;
    private String caption;
    private long likes;
    private long dislikes;
    private long comments;
    private long createdAt;

    public Post() { }

    public Post(String imageUrl, String caption) {
        this.imageUrl = imageUrl;
        this.caption = caption;
        createdAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    public long getLikes() { return likes; }
    public void setLikes(long likes) { this.likes = likes; }
    public long getDislikes() { return dislikes; }
    public void setDislikes(long dislikes) { this.dislikes = dislikes; }
    public long getComments() { return comments; }
    public void setComments(long comments) { this.comments = comments; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
