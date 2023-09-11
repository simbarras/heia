package ch.heia.isc.si.siiitp1groupe6spring2223.controller;

import ch.heia.isc.si.siiitp1groupe6spring2223.model.Game;
import ch.heia.isc.si.siiitp1groupe6spring2223.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;

@RestController
@ApplicationScope
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }


    @GetMapping("/games")
    public List<Game> games() {
        return gameService.getAllGames();
    }

    @PostMapping("/game/add")
    public void addGame(@RequestBody Game game) {
        this.gameService.addGame(game);
    }

}