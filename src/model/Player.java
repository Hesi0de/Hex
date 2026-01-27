// classe abstraite représentant un joueur dans un jeu de Hex

package model;

/**
 * Classe abstraite représentant un joueur dans un jeu de Hex.
 */
public abstract class Player {
    /**
     * Nom du joueur.
     */
    private String name;
    /**
     * Couleur du joueur.
     */
    private Color color;

    private MoveStrategy strategy;

    /**
     * Constructeur de la classe Player.
     *
     * @param name  Nom du joueur.
     * @param color Couleur du joueur.
     */
    public Player(String name, Color color, MoveStrategy strategy) { 
        this.name = name;
        this.color = color;
        this.strategy = strategy;
    }

    /**
     * Getter pour le nom du joueur.
     * @return Le nom du joueur.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter pour la couleur du joueur.
     * @return La couleur du joueur.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Setter pour le nom du joueur.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter pour la couleur du joueur.
     * @param color
     */
    public void setColor(Color color) {
        this.color = color;
    } 

    /**
     * Méthode pour jouer un coup.
     * @param game
     */
    public void play(Game game) {
        strategy.chooseMove(game);
    }
}