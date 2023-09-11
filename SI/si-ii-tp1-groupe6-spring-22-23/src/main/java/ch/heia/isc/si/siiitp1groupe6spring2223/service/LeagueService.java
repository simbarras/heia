package ch.heia.isc.si.siiitp1groupe6spring2223.service;

import ch.heia.isc.si.siiitp1groupe6spring2223.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;

@Service
@ApplicationScope
public class LeagueService {

    @Autowired
    LeagueRepository leagueRepository;

    @Autowired
    PlayerRepository playerRepository;

    public List<League> getAllLeagues() {
        return leagueRepository.findAll();
    }

    public void addLeague(League league) {
        leagueRepository.save(league);
    }

    public boolean deleteLeague(String leagueId) {
        //Convert String to int
        int id = Integer.parseInt(leagueId);
        if(leagueRepository.existsById(id)) {
            leagueRepository.deleteById(id);
            return true;
        }
        return false;
    }



    public void addPlayerToLeague(int leagueId, int playerId) {
        //Convert String to int

        League league = leagueRepository.findById(leagueId).get();
        Player player = playerRepository.findById(playerId).get();
        league.addPlayer(player);
        leagueRepository.save(league);
    }

    public League getLeague(int id) {
        return leagueRepository.findById(id).get();
    }

    public void deletePlayerFromLeague(int leagueId, int playerId) {
        League league = leagueRepository.findById(leagueId).get();
        Player player = playerRepository.findById(playerId).get();
        league.removePlayer(player);
        leagueRepository.save(league);
    }
}
