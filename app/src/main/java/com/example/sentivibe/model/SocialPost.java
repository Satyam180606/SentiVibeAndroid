package com.example.sentivibe.model;

/**
 * A simple Plain Old Java Object (POJO) to represent the data
 * fetched from a social media post.
 */
public class SocialPost {
    private String author;
    private String content;

    public SocialPost(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
