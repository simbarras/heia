package ch.heia.isc.si.siiitp1groupe6spring2223.controller;

import ch.heia.isc.si.siiitp1groupe6spring2223.model.Player;
import ch.heia.isc.si.siiitp1groupe6spring2223.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;

@RestController
@ApplicationScope
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    public List<Player> players() {
        return playerService.getAllPlayers();
    }


    @PostMapping("/player/add")
    public void addPlayer(@RequestBody Player player) {
        this.playerService.addPlayer(player);

    }
}
