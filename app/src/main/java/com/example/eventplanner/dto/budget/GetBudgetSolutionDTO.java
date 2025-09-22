package com.example.eventplanner.dto.budget;

public class GetBudgetSolutionDTO {
    private String name;
    private String mainImageUrl;
    private Long id;
    private String type;

    public GetBudgetSolutionDTO() {
    }

    public GetBudgetSolutionDTO(String name, String mainImageUrl, Long id, String type) {
        this.name = name;
        this.mainImageUrl = mainImageUrl;
        this.id = id;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
