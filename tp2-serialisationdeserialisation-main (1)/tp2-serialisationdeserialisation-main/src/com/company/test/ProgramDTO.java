package com.company.test;

import com.company.jsonlib.annotations.FieldName;

public class ProgramDTO {
    private String name;
    private String code;

    //Utilistation de l'annotation @FieldName pour renommer le champ lors de la sérialisation/désérialisation
    @FieldName(override = "domaine")
    private int domain;

    @FieldName(override = "limite")
    private boolean limited;

    private CoursDTO[] composition;

    // Constructeur par défaut
    public ProgramDTO() {
    }

    // Constructeur avec paramètres
    public ProgramDTO(String name, String code, int domain, boolean limited, CoursDTO[] composition) {
        this.name = name;
        this.code = code;
        this.domain = domain;
        this.limited = limited;
        this.composition = composition;
    }

    // Getters et Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getDomain() {
        return domain;
    }

    public void setDomain(int domain) {
        this.domain = domain;
    }

    public boolean isLimited() {
        return limited;
    }

    public void setLimited(boolean limited) {
        this.limited = limited;
    }

    public CoursDTO[] getComposition() {
        return composition;
    }

    public void setComposition(CoursDTO[] composition) {
        this.composition = composition;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ProgramDTO{");
        sb.append("name='").append(name).append('\'');
        sb.append(", code='").append(code).append('\'');
        sb.append(", domain=").append(domain);
        sb.append(", limited=").append(limited);
        sb.append(", composition=");
        if (composition != null) {
            sb.append("[");
            for (int i = 0; i < composition.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(composition[i]);
            }
            sb.append("]");
        } else {
            sb.append("null");
        }
        sb.append('}');
        return sb.toString();
    }
}
