package ch.heia.isc.si.siiitp1groupe6spring2223.service;

import ch.heia.isc.si.siiitp1groupe6spring2223.model.Game;
import ch.heia.isc.si.siiitp1groupe6spring2223.model.GameRepository;
import ch.heia.isc.si.siiitp1groupe6spring2223.model.League;
import ch.heia.isc.si.siiitp1groupe6spring2223.model.LeagueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;

@Service
@ApplicationScope
public class GameService {
    @Autowired
    GameRepository gameRepository;

    @Autowired
    LeagueRepository leagueRepository;
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public void addGame(Game game) {
        gameRepository.save(game);
        addGameToLeague(game.getLeagueid(), game.getId());
    }

    public void addGameToLeague(int leagueId, int gameId) {
        League league = leagueRepository.findById(leagueId).get();
        Game game = gameRepository.findById(gameId).get();
        league.addGame(game);
        leagueRepository.save(league);
    }

}
