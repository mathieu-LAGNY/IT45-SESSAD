package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

// La classe population englobe plusieurs solution potentielle du probleme
public class Population {
    private ArrayList<Chromosome> individus;
    private final int taille_pop;
    private int[] ordre;

    private Random rand;

    public Population(int taille_pop, JavaIG instance) {
        rand = new Random();
        this.taille_pop = taille_pop;
        this.individus = new ArrayList<>();
        this.ordre = new int[taille_pop];
        for(int i=0; i<taille_pop; i++) {
            ordre[i] = i;
            Chromosome c = new Chromosome(instance);
            this.individus.add(c);
        }
        ordonner();
    }

    public Population (Population p) {
        rand = new Random();
        this.taille_pop = p.taille_pop;
        this.individus = new ArrayList<>();
        this.ordre = new int[taille_pop];
        for(int i=0; i<taille_pop; i++) {
            ordre[i] = i;
            Chromosome c = p.individus.get(i);
            this.individus.add(c);
        }
        ordonner();
    }

    // tri par ordre croissant de fitness
    public void ordonner() {
        int inter;
        for(int i=0; i<taille_pop-1; i++)
            for(int j=i+1; j<taille_pop; j++)
                if(individus.get(ordre[i]).getFitness() > individus.get(ordre[j]).getFitness()) {
                    inter    = ordre[i];
                    ordre[i] = ordre[j];
                    ordre[j] = inter;
                }
    }

    // evalue tous les individus
    public void evaluer() {
        for (Chromosome individu: individus) {
            individu.evaluer();
        }
    }

    // teste la validité de tous les individus
    public boolean valide() {
        boolean val = true;
        for (Chromosome individu: individus) {
            if(!individu.valide()) {
                val = false;
            }
        }
        return val;
    }

    public double getBestFitness() {
        return individus.get(ordre[0]).getFitness();
    }

    public Chromosome getBestChromosome() {
        return individus.get(ordre[0]);
    }

    private void quickSort(double[] arr, int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end);

            quickSort(arr, begin, partitionIndex-1);
            quickSort(arr, partitionIndex+1, end);
        }
    }

    private int partition(double[] arr, int begin, int end) {
        double pivot = arr[end];
        double swapTemp;
        int i = (begin-1);
        for (int j = begin; j < end; j++) {
            if (arr[j] > pivot) {
                i++;

                swapTemp = arr[i];
                arr[i] =  arr[j];
                arr[j] = swapTemp;
            }
        }
        swapTemp = arr[i + 1];
        arr[i + 1] =  arr[end];
        arr[end] = swapTemp;

        return i+1;
    }

    // SELECTION PAR ROULETTE BIAISEE
//opérateur de sélection basé sur la fonction fitness
    public Chromosome selection_roulette() {
        double somme_fitness = individus.get(0).getFitness();
        double fitness_max   = individus.get(0).getFitness();
        double somme_portion;

        for(int i=1; i<taille_pop; i++) {
            somme_fitness += individus.get(i).getFitness();
            if (fitness_max < individus.get(i).getFitness())
            fitness_max = individus.get(i).getFitness();
        }
        somme_portion = fitness_max*taille_pop - somme_fitness;

        double variable_alea = rand.nextInt(1000)/1000.0;

        int ind = 0;
        double portion = (fitness_max - individus.get(0).getFitness()) /somme_portion;
        while ((ind<taille_pop-1) && (variable_alea>=portion)) {
            ind++;
            portion += (fitness_max - individus.get(ind).getFitness()) /somme_portion;
        }
        return individus.get(ind);
    }

// opérateur de remplacement basé sur la roulette biaisée d'un individu de la population
//   par un nouveau individu donné en argument
    public void remplacement_roulette(Chromosome individu) {
        double somme_fitness = individus.get(0).getFitness();
        for(int i=1; i<taille_pop; i++)
            somme_fitness += individus.get(i).getFitness();

        double variable_alea;
        int ind = ordre[0];
        double portion;

        // on garde le meilleur individu
        while (ordre[0]==ind) {
            variable_alea = rand.nextInt(1000)/1000.0;
            ind = 0;
            portion = individus.get(0).getFitness() /somme_fitness;
            while ((ind<taille_pop-1) && (variable_alea>portion)) {
                ind++;
                portion += individus.get(ind).getFitness() /somme_fitness;
            }
        }
        individus.set(ind, new Chromosome(individu));
    }

// SELECTION ALEATOIRE
//opérateur de sélection aléatoire
    public Chromosome selection_aleatoire() {
        int ind_alea = rand.nextInt(taille_pop);
        return individus.get(ind_alea);
    }

// opérateur de remplacement alétoire d'un individu de la population
//   par un nouveau individu donné en argument
    public void remplacement_aleatoire(Chromosome individu) {
        // on conserve le meilleur individu
        int ind_alea = rand.nextInt(taille_pop-1)+1;
        individus.set(ordre[ind_alea], new Chromosome(individu));
    }

// SELECTION PAR RANKING
// opérateur de sélection basé sur le ranking, les individus de la
//   population sont ordonnés de façon à ce que le meilleur est le rang 0
//   le taux de ranking permet de régler la préssion de sélection :
//   0 forte pression et +INFINI faible pression (probabilite = 1/nb qlq soit l'individu)
    public Chromosome selection_ranking(float taux_ranking) {
        ordonner();

        double variable_aleatoire = rand.nextInt(1000)/1000.0;
        int nb = taille_pop;
        int i = 0;
        taux_ranking = taux_ranking/100;

        double portion = (((nb-i)*2.0/(nb*(nb+1)))+taux_ranking)/(1.0+nb*taux_ranking);

        while(variable_aleatoire>portion) {
            i++;
            portion += (((nb-i)*2.0/(nb*(nb+1)))+taux_ranking)/(1.0+nb*taux_ranking);
        }

        if(i>=nb)
            i = rand.nextInt(nb);
        return individus.get(ordre[i]);
    }

// opérateur de remplacement basé sur le ranking d'un individu de la population
//   par un nouveau individu donné en argument
    public void remplacement_ranking(Chromosome individu, float taux_ranking) {
        double variable_aleatoire = rand.nextInt(1000)/1000.0;
        int T = taille_pop;
        int i = 0;
        taux_ranking   = taux_ranking/100;
        double portion = ((i*2.0/(T*(T-1)))+taux_ranking)/(1.0+T*taux_ranking);

        while(variable_aleatoire>portion) {
            i++;
            portion += ((i*2.0/(T*(T-1)))+taux_ranking)/(1.0+T*taux_ranking);
        }
        // on garde le meilleur individu
        if(i>=T || i == 0)
            i = rand.nextInt(T-1)+1;
        int ind = ordre[i];
        individus.set(ind, new Chromosome(individu));
    }

    @Override
    public String toString() {
        String string = "Population de " + taille_pop + " individus :\n";
        for (int i=0; i<taille_pop; i++) {
            string += "i= " + i + " " + individus.get(ordre[i]).getFitness() + "\n";
        }
        return string;
    }

    public void afficher() {
        for (Chromosome c: this.individus)
            System.out.println(Arrays.toString(c.getGenes()));
    }

    // statistiques sur la population
    public void statistiques() {
        this.ordonner();
        double moyenne    = 0;
        double ecart_type = 0;

        for (int i=0; i<taille_pop; i++)
            moyenne += individus.get(i).getFitness();
        moyenne = moyenne / taille_pop;
        for (int i=0; i<taille_pop; i++)
            ecart_type += Math.pow(individus.get(i).getFitness() - moyenne, 2);
        ecart_type /= taille_pop;
        ecart_type = Math.sqrt(ecart_type);

        System.out.println("fitness : (moyenne, ecart_type) -> ("
                +  moyenne + " , "  + ecart_type + ")\n");
        System.out.println("fitness : [meilleure, mediane, pire] -> ["
                + individus.get(ordre[0]).getFitness() + " , "
            + individus.get(ordre[(taille_pop / 2)]).getFitness() + " , "
            + individus.get(ordre[taille_pop - 1]).getFitness() + "]\n");
    }

    // Similitude de la population
    public void similitude() {
        int nb_ind_id_1, nb_ind_id_2, nb_ind_id_3;
        nb_ind_id_1 = nb_solutions_similaires(individus.get(ordre[0]));
        System.out.println("Nombre d'individus de la population identique ayant la fitness = " + individus.get(ordre[0]).getFitness() + " : " + nb_ind_id_1 + " / " + taille_pop + "\n");
        if (nb_ind_id_1<taille_pop)
        {
            nb_ind_id_2 = nb_solutions_similaires(individus.get(ordre[nb_ind_id_1]));
            System.out.println("Nombre d'individus de la population identique ayant la fitness = " + individus.get(ordre[nb_ind_id_1]).getFitness() + " : " + nb_ind_id_2 + " / " + taille_pop + "\n");
            if (nb_ind_id_1+nb_ind_id_2<taille_pop)
            {
                nb_ind_id_3 = nb_solutions_similaires(individus.get(ordre[nb_ind_id_1 + nb_ind_id_2]));
                System.out.println("Nombre d'individus de la population identique ayant la fitness = " + individus.get(ordre[nb_ind_id_1 + nb_ind_id_2]).getFitness() + " : " + nb_ind_id_3 + " / " + taille_pop + "\n");
            }
        }
    }

    // compte le nombre de chromosomes similaires
    public int nb_chromosomes_similaires(Chromosome chro) {
        int nb = 0;
        for (int i=0; i<taille_pop; i++) {
            if (Arrays.equals(chro.getGenes(), individus.get(i).getGenes())) {
                nb++;
            }
        }
        return nb;
    }

    // compte le nombre de solutions avec une fitness similaire
    public int nb_solutions_similaires(Chromosome chro) {
        int nb = 0;
        for (int i=0; i<taille_pop; i++) {
            if (Math.abs(chro.getFitness() - individus.get(i).getFitness()) < 0.0001) {
                nb++;
            }
        }
        return nb;
    }

    public int getTaille_pop() {
        return this.individus.size();
    }
}
