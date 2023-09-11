package ch.heia.isc.si.siiitp1groupe6spring2223.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    private LocalDate date;
    private String homeTeam;
    private String awayTeam;
    private String location;

    private int leagueid;


    public Game() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String team1) {
        this.homeTeam = team1;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String team2) {
        this.awayTeam = team2;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public int getLeagueid() {
        return leagueid;
    }

    public void setLeagueid(int leagueid) {
        this.leagueid = leagueid;
    }


}
