package com.example.green;

public class HistoryItem {
    private int id;
    private String resultText;
    private String imagePath;
    private String timestamp;

    public HistoryItem(int id, String resultText, String imagePath, String timestamp) {
        this.id = id;
        this.resultText = resultText;
        this.imagePath = imagePath;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public String getResultText() { return resultText; }
    public String getImagePath() { return imagePath; }
    public String getTimestamp() { return timestamp; }
}
