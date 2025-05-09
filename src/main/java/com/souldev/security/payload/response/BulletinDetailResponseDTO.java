package com.souldev.security.payload.response;

import java.util.List;

public class BulletinDetailResponseDTO {
    private String id;
    private String eleveNom;
    private String semestreNom;
    private String classeNom;
    private List<TypeMatiereDetail> typeMatiereDetails; // Regroupement par type
    private double moyenneGenerale;
    private int rang;
    private String appreciation;

    public BulletinDetailResponseDTO(String id, String eleveNom, String semestreNom, String classeNom,
                                     List<TypeMatiereDetail> typeMatiereDetails, double moyenneGenerale, int rang, String appreciation) {
        this.id = id;
        this.eleveNom = eleveNom;
        this.semestreNom = semestreNom;
        this.classeNom = classeNom;
        this.typeMatiereDetails = typeMatiereDetails;
        this.moyenneGenerale = moyenneGenerale;
        this.rang = rang;
        this.appreciation = appreciation;
    }

    // Getters et setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEleveNom() { return eleveNom; }
    public void setEleveNom(String eleveNom) { this.eleveNom = eleveNom; }
    public String getSemestreNom() { return semestreNom; }
    public void setSemestreNom(String semestreNom) { this.semestreNom = semestreNom; }
    public String getClasseNom() { return classeNom; }
    public void setClasseNom(String classeNom) { this.classeNom = classeNom; }
    public List<TypeMatiereDetail> getTypeMatiereDetails() { return typeMatiereDetails; }
    public void setTypeMatiereDetails(List<TypeMatiereDetail> typeMatiereDetails) { this.typeMatiereDetails = typeMatiereDetails; }
    public double getMoyenneGenerale() { return moyenneGenerale; }
    public void setMoyenneGenerale(double moyenneGenerale) { this.moyenneGenerale = moyenneGenerale; }
    public int getRang() { return rang; }
    public void setRang(int rang) { this.rang = rang; }
    public String getAppreciation() { return appreciation; }
    public void setAppreciation(String appreciation) { this.appreciation = appreciation; }

    public static class TypeMatiereDetail {
        private String typeMatiereNom;
        private double moyenne;
        private int rang;
        private List<MatiereDetail> matiereDetails;

        public TypeMatiereDetail(String typeMatiereNom, double moyenne, int rang, List<MatiereDetail> matiereDetails) {
            this.typeMatiereNom = typeMatiereNom;
            this.moyenne = moyenne;
            this.rang = rang;
            this.matiereDetails = matiereDetails;
        }

        // Getters et setters
        public String getTypeMatiereNom() { return typeMatiereNom; }
        public void setTypeMatiereNom(String typeMatiereNom) { this.typeMatiereNom = typeMatiereNom; }
        public double getMoyenne() { return moyenne; }
        public void setMoyenne(double moyenne) { this.moyenne = moyenne; }
        public int getRang() { return rang; }
        public void setRang(int rang) { this.rang = rang; }
        public List<MatiereDetail> getMatiereDetails() { return matiereDetails; }
        public void setMatiereDetails(List<MatiereDetail> matiereDetails) { this.matiereDetails = matiereDetails; }
    }

    public static class MatiereDetail {
        private String matiereNom;
        private double moyenne;
        private double coefficient;
        private double moyenneClasse;
        private String professeurNom;
        private String commentaires;

        public MatiereDetail(String matiereNom, double moyenne, double coefficient, double moyenneClasse,
                             String professeurNom, String commentaires) {
            this.matiereNom = matiereNom;
            this.moyenne = moyenne;
            this.coefficient = coefficient;
            this.moyenneClasse = moyenneClasse;
            this.professeurNom = professeurNom;
            this.commentaires = commentaires;
        }

        // Getters et setters
        public String getMatiereNom() { return matiereNom; }
        public void setMatiereNom(String matiereNom) { this.matiereNom = matiereNom; }
        public double getMoyenne() { return moyenne; }
        public void setMoyenne(double moyenne) { this.moyenne = moyenne; }
        public double getCoefficient() { return coefficient; }
        public void setCoefficient(double coefficient) { this.coefficient = coefficient; }
        public double getMoyenneClasse() { return moyenneClasse; }
        public void setMoyenneClasse(double moyenneClasse) { this.moyenneClasse = moyenneClasse; }
        public String getProfesseurNom() { return professeurNom; }
        public void setProfesseurNom(String professeurNom) { this.professeurNom = professeurNom; }
        public String getCommentaires() { return commentaires; }
        public void setCommentaires(String commentaires) { this.commentaires = commentaires; }
    }
}