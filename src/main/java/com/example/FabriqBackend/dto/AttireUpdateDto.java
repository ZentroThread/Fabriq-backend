package com.example.FabriqBackend.dto;

import com.example.FabriqBackend.model.Attire;
import lombok.Data;

@Data
public class AttireUpdateDto {

    private String attireCode;
    private String attireName;
    private String attireDescription;
    private Double attirePrice;
    private String attireStatus;
    private Integer categoryId;
    private Integer attireStock;

    public void applyTo(Attire attire) {
        if (this.attireCode != null) attire.setAttireCode(this.attireCode);
        if (this.attireName != null) attire.setAttireName(this.attireName);
        if (this.attireDescription != null) attire.setAttireDescription(this.attireDescription);
        if (this.attirePrice != null) attire.setAttirePrice(this.attirePrice);
        if (this.attireStatus != null) attire.setAttireStatus(this.attireStatus);
        if (this.attireStock != null) attire.setAttireStock(this.attireStock);
    }
}
