package ch.heia.isc.si.siiitp1groupe6spring2223.model;

import javax.persistence.*;
import java.util.List;


@Entity
public class League {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;


    @OneToMany
    private List<Player> players;

    @OneToMany
    private List<Game> games;

    public League() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void addGame(Game game) {
        this.games.add(game);
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public void addPlayer(String idPlayer) {

    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }
}
