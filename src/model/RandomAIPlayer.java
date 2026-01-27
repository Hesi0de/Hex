package model;

/**
 * Classe représentant un joueur IA qui choisit des coups aléatoires.
 */
public class RandomAIPlayer implements MoveStrategy {
   /**
    * Constructeur de la classe RandomAIPlayer.
    * @param name
    * @param color
    */
    // public RandomAIPlayer(String name, Color color) {
    //     super(name, color);
    // }

    /**
     * Méthode pour choisir un coup aléatoire.
     * @return Un tableau de deux entiers représentant les coordonnées du coup choisi.
     */
    @Override
    public int[] chooseMove(Game game) {
        int boardSize = 14; // Taille de la grille(temporaire) 
        //choix de coup aléatoire
        int row = (int) (Math.random() * boardSize);
        int col = (int) (Math.random() * boardSize);
       return new int[] {row, col};
    }
}