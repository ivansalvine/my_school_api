package com.souldev.security.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class Utils {

    public static Date convertToDateViaInstant(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Duration returnDurationBetween(LocalDate startDate, LocalDate endDate, LocalTime time) {
        // Combine LocalDate with LocalTime to get LocalDateTime for start and end
        LocalDateTime startDateTime = LocalDateTime.of(startDate, time);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, time);

        // Return the duration between the two LocalDateTime instances
        return Duration.between(startDateTime, endDateTime);
    }

    public static Duration returnDurationBetween(LocalTime startTime, LocalTime endTime) {
        // Return the duration between the two LocalTime instances
        return Duration.between(startTime, endTime);
    }

    private Duration returnDurationBetween(LocalDate startDate, LocalDate endDate) {
        return Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay());
    }

    public static Instant toInstant(LocalDate localDate, LocalTime localTime, ZoneId zoneId) {
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        return localDateTime.atZone(zoneId).toInstant();
    }

    public static String calculateDuration(LocalTime start, LocalTime end) {
        Duration duration = Duration.between(start, end);
        long seconds = duration.getSeconds();
        return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }

    public static String calculateDaysBetween(LocalDate start, LocalDate end) {
        long days = ChronoUnit.DAYS.between(start, end);
        return String.valueOf(days);
    }

    // Convertir `Date` en `LocalDate`
    public static LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // Formater `Duration` en chaîne de caractères
    public static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }


    // Méthode pour obtenir l'extension à partir du type MIME
    public static String getExtensionFromMimeType(String mimeType) {
        switch (mimeType) {
            case "image/jpeg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";
            case "application/pdf":
                return "pdf";
            case "text/plain":
                return "txt";
            default:
                return null; // Type MIME non supporté
        }
    }


    public static final String[] PUBLIC_URLS = {
            "/auth/**",
            "/images/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/h2-console/**" // À supprimer en production
    };

    public static String generatePassword(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être null ou vide");
        }

        // Prendre les 5 premiers caractères (ou moins) en minuscules
        String nomPart = nom.trim().toLowerCase();
        nomPart = nomPart.length() > 5 ? nomPart.substring(0, 5) : nomPart;

        // Obtenir la date actuelle au format ddMMyy
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy");
        String datePart = today.format(formatter);

        // Concaténer
        return nomPart + datePart;
    }
}
