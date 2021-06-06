package main;

import java.util.ArrayList;
import java.util.Arrays;

public class Interface {
    private final int competence_signes;
    private final int comptence_codage;
    private final int[] specialites;
    private final ArrayList<Float> coordinates;

    public Interface(int competence_signes, int comptence_codage, float x, float y, int[] specialites) {
        this.competence_signes = competence_signes;
        this.comptence_codage = comptence_codage;
        this.coordinates = new ArrayList<>(Arrays.asList(x, y));
        this.specialites = specialites;
    }

    public ArrayList<Float> getCoordinates() {
        return new ArrayList<>(coordinates);
    }

    @Override
    public String toString() {
        String string = "signes = " + competence_signes + ", codage = " + comptence_codage + ", specialites = [";
        for (int i = 0; i < specialites.length-1; i++) {
            string += specialites[i] + ", ";
        }
        string += specialites[specialites.length-1] + "]";
        return string;
    }

    public boolean signes() {
        return competence_signes == 1;
    }

    public boolean codage() {
        return comptence_codage == 1;
    }

    public boolean competent(int i) {
        if (i == 0 && competence_signes == 1) return true;
        return i == 1 && comptence_codage == 1;
    }

    public boolean specialise(int i) {
        return specialites[i] == 1;
    }
}
