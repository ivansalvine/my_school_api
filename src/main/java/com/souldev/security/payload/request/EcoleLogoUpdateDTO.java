package com.souldev.security.payload.request;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

public class EcoleLogoUpdateDTO {
    @NotBlank
    private String ecoleId;

    private MultipartFile logoFile;

    // Getters & Setters

    public String getEcoleId() {
        return ecoleId;
    }

    public void setEcoleId(String ecoleId) {
        this.ecoleId = ecoleId;
    }

    public MultipartFile getLogoFile() {
        return logoFile;
    }

    public void setLogoFile(MultipartFile logoFile) {
        this.logoFile = logoFile;
    }
}