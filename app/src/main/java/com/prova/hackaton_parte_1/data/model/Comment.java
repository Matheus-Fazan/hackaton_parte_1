package com.prova.hackaton_parte_1.data.model;

public class Comment {
    private String author;
    private String text;
    private long createdAt;

    public Comment() { }

    public Comment(String author, String text) {
        this.author = author;
        this.text = text;
        createdAt = System.currentTimeMillis();
    }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
