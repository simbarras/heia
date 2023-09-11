package ch.heia.isc.si.siiitp1groupe6spring2223.controller;

import ch.heia.isc.si.siiitp1groupe6spring2223.model.League;
import ch.heia.isc.si.siiitp1groupe6spring2223.service.LeagueService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;
import java.util.Objects;

@RestController
@ApplicationScope
public class LeagueController {

    private final LeagueService leagueService;

    @Autowired
    public LeagueController(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    /**
     * Get all leagues
     *
     * @return List of leagues
     */
    @GetMapping("/leagues")
    public List<League> leagues() {
        return leagueService.getAllLeagues();
    }

    /**
     * Get a league by id
     *
     * @param id League id
     * @return League
     */
    @GetMapping("/league/{id}")
    public League league(@PathVariable int id) {
        return leagueService.getLeague(id);
    }


    /**
     * Add a game to a league
     *
     * @param name name of the league
     * @return
     */
    @GetMapping("/league/add/{name}")
    public void addLeague(@PathVariable String name) {
        League l1 = new League();
        l1.setName(name);

        this.leagueService.addLeague(l1);
    }

    /**
     * Delete a league
     *
     * @param id id of the league
     * @return true if the league has been deleted
     */
    @GetMapping("/league/delete/{id}")
    public void deleteLeague(@PathVariable String id) {
        System.out.println("delete league: " + id);

        this.leagueService.deleteLeague(id);

    }

    /**
     * Add a player to a league
     * @param body
     */
    @PostMapping("/league/add/player")
    public void addPlayerToLeague(@RequestBody String body) throws ParseException {
        System.out.println("add player to league: " + body);
        JSONParser parser = new JSONParser();
        JSONObject jsonBody = (JSONObject) parser.parse(body);

        int leagueId = Integer.parseInt(Objects.requireNonNull(jsonBody.get("leagueid")).toString());
        int playerId = Integer.parseInt(Objects.requireNonNull(jsonBody.get("playerid")).toString());

        this.leagueService.addPlayerToLeague(leagueId, playerId);

    }

    /**
     * Delete a player from a league
     * @param body
     */
    @PostMapping("/league/delete/player")
    public void deletePlayerFromLeague(@RequestBody String body) throws ParseException {
        System.out.println("delete player from league: " + body);
        JSONParser parser = new JSONParser();
        JSONObject jsonBody = (JSONObject) parser.parse(body);

        int leagueId = Integer.parseInt(Objects.requireNonNull(jsonBody.get("leagueid")).toString());
        int playerId = Integer.parseInt(Objects.requireNonNull(jsonBody.get("playerid")).toString());

        this.leagueService.deletePlayerFromLeague(leagueId, playerId);

    }



}
