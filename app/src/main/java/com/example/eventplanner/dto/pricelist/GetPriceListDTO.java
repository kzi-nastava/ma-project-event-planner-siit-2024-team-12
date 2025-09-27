package com.example.eventplanner.dto.pricelist;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class GetPriceListDTO implements Serializable{

    private Long id;
    private LocalDate startDate;
    private List<GetPriceListItemDTO> priceListItems;

    public GetPriceListDTO() {
    }

    public GetPriceListDTO(Long id, LocalDate startDate, List<GetPriceListItemDTO> priceListItems) {
        this.id = id;
        this.startDate = startDate;
        this.priceListItems = priceListItems;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public List<GetPriceListItemDTO> getPriceListItems() {
        return priceListItems;
    }

    public void setPriceListItems(List<GetPriceListItemDTO> priceListItems) {
        this.priceListItems = priceListItems;
    }
}
