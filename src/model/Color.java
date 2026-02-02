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
     * 
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

    /*
     * public String toSymbol(){
     * return this == EMPTY ? "." : this == RED ? "R" : "B";
     * }
     */
    public String toSymbol() {
        return this == EMPTY ? "\u001B[37m"+"⬢" : this == RED ? "\u001B[1;31m" + "⬢" :"\u001B[1;34m" + "⬢";
    }
}
