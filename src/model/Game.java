package model;
import java.util.*;

public class Game {

    private Board board;
    private Player currentPlayer;
    private List<Player> players;

    public Game(Board board, Player currentPlayer, List<Player>){
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.players = players;
    }

    public Player getCurrentPlayer();

    public Player resetPlayer();

    public boolean isOver();

    public Player nextPlayer();

    public boolean isValid();

    public Player getWinner();

    public Board getBoard();
}