package com.example.eventplanner.dto;

import java.util.List;

public class PageResponse<T> {
    private List<T> content;
    private int totalPages;
    private int totalElements;
    private int size;
    private int number;

    public List<T> getContent() {
        return content;
    }
    public void setContent(List<T> content) { this.content = content; }

    public int getTotalPages() { return totalPages; }

    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public int getTotalElements() { return totalElements; }

    public void setTotalElements(int totalElements) { this.totalElements = totalElements; }

    private int getSize() { return size; }

    private void setSize(int size) { this.size = size; }

    private int getNumber() { return number; }

    private void setNumber(int number) { this.number = number; }
}

