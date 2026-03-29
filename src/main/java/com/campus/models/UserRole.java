package com.campus.models;

public enum UserRole {
    ADMIN("Administrateur"),
    CHEF_BATIMENT("Chef de bâtiment"),
    ETUDIANT("Étudiant");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
