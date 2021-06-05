package main;

public class Main {
    public static void main(String[] args) {
        int nb_generation     = 50;
        int taille_population = 20;
        double taux_croisement = 0.8;
        double taux_mutation   = 0.5;

        JavaIG javaIG = new JavaIG();

        //Ae algo(nb_generation, taille_population, taux_croisement, taux_mutation, taille_chromosome);

        // lance l'algorithme évolutionniste
        // Chromosome best = algo.optimiser();

        // affiche la fitness du meilleur individu trouvé
        // System.out.println("La meilleure solution trouvee est : " + best);
    }
}
