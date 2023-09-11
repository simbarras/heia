package ch.heia.isc.si.siiitp1groupe6spring2223.service;

import ch.heia.isc.si.siiitp1groupe6spring2223.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;

@Service
@ApplicationScope
public class PlayerService {

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    LeagueRepository leagueRepository;

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public void addPlayer(Player player) {
        playerRepository.save(player);
    }

    public void addGameToLeague(int leagueId, int gameId) {
        League league = leagueRepository.findById(leagueId).get();
        Player player = playerRepository.findById(gameId).get();
        league.addPlayer(player);
        leagueRepository.save(league);
    }

}
