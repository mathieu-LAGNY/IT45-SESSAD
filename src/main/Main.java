package main;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int nb_generation = 10000;
        int taille_population = 100;
        double taux_croisement = 0.8;
        double taux_mutation = 0.5;

        // variable pour tester l'efficacité des paramètres
        int nb_test = 50;
        JavaIG javaIG = new JavaIG();

        Ae algo;
        Chromosome best;

        double[] fit = new double [nb_test];
        double moy = 0.0;

        algo = new Ae(nb_generation, taille_population, taux_croisement, taux_mutation, javaIG);
        best = algo.optimiser();
        moy += best.getFitness();
        fit[0] = best.getFitness();
        double min = best.getFitness();
        for (int i = 1; i < nb_test; i++) {
            // lance l'algorithme évolutionniste
            algo = new Ae(nb_generation, taille_population, taux_croisement, taux_mutation, javaIG);
            best = algo.optimiser();
            moy += best.getFitness();
            fit[i] = best.getFitness();
            if (best.getFitness() < min) min = best.getFitness();
            // affiche la fitness du meilleur individu trouvé
            // System.out.println("La meilleure solution trouvee est : ");
            // best.affichageSolution();
        }
        System.out.println(Arrays.toString(fit));

        moy /= nb_test;
        System.out.println(moy);

        double ecart = 0;
        for (double f : fit)
            ecart += Math.pow(f - moy, 2);
        ecart /= nb_test;
        ecart = Math.sqrt(ecart);
        System.out.println(ecart);

        System.out.println(moy-min);
    }
}
