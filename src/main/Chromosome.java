package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Chromosome {
    private int[] genes; // les gènes du chromosome/solution
    private int taille; // la taille du chromosome = nombre de gènes
    private double fitness; // la valeur de la fonction objectif (fitness) de la solution

    Random rand = new Random();

    // constructeur aléatoire
    public Chromosome(int nbFormation, int nbInterface) {
        int a;
        boolean recommence = true;
        taille = nbFormation;
        // un chromosome est composé de 'taille' gènes,
        // les gènes sont caratérisé par un entier compris entre 0 et 'taille-1'
        genes = new int[taille];
        //  Arbitrairement, on choisit de toujours commencer un chromosome par le gène 0
        genes[0] = 0;
        for(int i=1; i<taille; i++) {
            genes[i] = rand.nextInt(taille);
        }
    }

    public Chromosome(Chromosome c) {
        for(int i=0; i<c.taille; i++)
            genes[i] = c.genes[i];
        this.taille = c.taille;
        this.fitness = c.fitness;
    }

    public void evaluer(float[] distances, int nbFormation, int penalite) {
        float moy = 0;
        float somme = 0;
        for (int i = 0; i<distances.length; i++)
            somme += distances[i];
        moy = somme / distances.length;

        double ecart = 0;
        for (int i = 0; i<distances.length; i++)
            ecart += Math.pow(distances[i]-moy,2);
        ecart /= distances.length;
        ecart = Math.sqrt(ecart);

        float f = somme / nbFormation;

        this.fitness = 0.5 * (moy + ecart) + 0.5 * f * penalite;
    }
    // fonction d'évaluation du chromosome (c-à-d calcul la fitness)
    //   Elle doit etre lancée à la creation des solution et apres
    //   l'exécution des operateurs de mutation et de croisement

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chromosome that = (Chromosome) o;
        return taille == that.taille && Double.compare(that.fitness, fitness) == 0 && Arrays.equals(genes, that.genes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(taille, fitness);
        result = 31 * result + Arrays.hashCode(genes);
        return result;
    }

    // OPERATEURS DE MUTATION
}
