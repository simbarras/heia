package ch.heia.isc.si.siiitp1groupe6spring2223;

import ch.heia.isc.si.siiitp1groupe6spring2223.model.Game;
import ch.heia.isc.si.siiitp1groupe6spring2223.model.Player;
import ch.heia.isc.si.siiitp1groupe6spring2223.service.GameService;
import ch.heia.isc.si.siiitp1groupe6spring2223.service.LeagueService;
import ch.heia.isc.si.siiitp1groupe6spring2223.service.PlayerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SiIiTp1Groupe6Spring2223ApplicationTests {

    @Autowired
    GameService gameService;

    @Autowired
    PlayerService playerService;

    @Autowired
    LeagueService leagueService;

    @Autowired
    MockMvc mockMvc;


    Player pToAdd;

    // Test for reading all games
    @Test
    @Order(1)
    void testGetAllGames() throws Exception {
        mockMvc.perform(get("/games"))
                .andExpect(status().isOk());
    }

    // Test get all leagues
    @Test
    @Order(2)
    void testGetAllLeagues() throws Exception {
        mockMvc.perform(get("/leagues"))
                .andExpect(status().isOk());
    }

    // Test for the add player
    @Test
    @Order(3)
    void testAddPlayer() throws Exception {
        pToAdd = new Player();
        pToAdd.setFirstname("John");
        pToAdd.setLastname("Doe");
        pToAdd.setBirthdate(LocalDate.of(2000, 1, 1));
        pToAdd.setFavoriteTeam("Manchester United");

        // Serialize the object to JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(pToAdd);

        mockMvc.perform(post("/player/add").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk());
    }

    // Tes for the add league
    @Test
    @Order(4)
    void testAddLeague() throws Exception {
        String name = "Premier league";

        mockMvc.perform(get("/league/add/" + name))
                .andExpect(status().isOk());
    }

    // Test for the add game
    @Test
    @Order(5)
    void testAddGame() throws Exception {
        Game game = new Game();
        game.setHomeTeam("Manchester United");
        game.setAwayTeam("Manchester City");
        game.setDate(LocalDate.of(2021, 1, 1));
        game.setLocation("Old Trafford");
        System.out.println(leagueService.getAllLeagues().size());
        // Get the last league added
        int leagueId = leagueService.getAllLeagues().get(leagueService.getAllLeagues().size() - 1).getId();
        game.setLeagueid(leagueId);

        // Serialize the object to JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(game);

        mockMvc.perform(post("/game/add").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk());
    }


    // Test add player to a league
    @Test
    @Order(6)
    void testAddPlayerToLeague() throws Exception {
        // Get the last league added
        int leagueId = leagueService.getAllLeagues().get(leagueService.getAllLeagues().size() - 1).getId();
        // Get the last player added
        int idPlayer = playerService.getAllPlayers().get(playerService.getAllPlayers().size() - 1).getId();

        String body = "{\"leagueid\": " + leagueId + ", \"playerid\": " + idPlayer + "}";

        mockMvc.perform(post("/league/add/player").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    void testDeleteLeague() throws Exception {
        // Get the last league added
        int leagueId = leagueService.getAllLeagues().get(leagueService.getAllLeagues().size() - 1).getId();
        // Retrieve the league
        mockMvc.perform(get("/league/delete/" + leagueId))
                .andExpect(status().isOk());
    }
}
