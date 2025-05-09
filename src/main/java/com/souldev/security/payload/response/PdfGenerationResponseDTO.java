package com.souldev.security.payload.response;

public class PdfGenerationResponseDTO {
    private String eleveId;
    private String bulletinId;
    private String pdfFileName;
    private String downloadUrl;

    public PdfGenerationResponseDTO(
            String eleveId,
            String bulletinId,
            String pdfFileName,
            String downloadUrl
    ) {
        this.eleveId = eleveId;
        this.bulletinId = bulletinId;
        this.pdfFileName = pdfFileName;
        this.downloadUrl = downloadUrl;
    }

    // Getters et setters
    public String getEleveId() {
        return eleveId;
    }

    public void setEleveId(String eleveId) {
        this.eleveId = eleveId;
    }

    public String getBulletinId() {
        return bulletinId;
    }

    public void setBulletinId(String bulletinId) {
        this.bulletinId = bulletinId;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }

    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}