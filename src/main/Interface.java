package main;

import java.util.ArrayList;
import java.util.Arrays;

public class Interface {
    private int competence_signes;
    private int comptence_codage;
    private int[] specialites;

    public Interface(int competence_signes, int comptence_codage) {
        this.competence_signes = competence_signes;
        this.comptence_codage = comptence_codage;
    }

    public void setSpecialites(int[] specialites) {
        this.specialites = specialites;
    }

    @Override
    public String toString() {
        return "    signes = " + competence_signes +
                ", codage = " + comptence_codage +
                ", specialites = " + Arrays.toString(specialites) +
                "\n";
    }
}
