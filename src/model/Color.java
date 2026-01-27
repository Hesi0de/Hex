package model;

/**
 * Enumération représentant les couleurs des joueurs.
 */
public enum Color {
    RED,
    BLUE,
    EMPTY;

    /**
     * methode pour retourner la couleur de l'opposant
     * @return de l'opposant
     */
    public Color opponnetColor() {
        switch (this) {
            case RED:
                return BLUE;
            case BLUE:
                return RED;
            default:
                return EMPTY;
        }
    }
}
