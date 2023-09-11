package ch.heia.isc.ol.simulife;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TestREST {
    @Autowired
    MockMvc mockMvc;


    @Test
    void testAllRoutes() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/universes"))
                .andExpect(status().isOk())
                .andReturn();

        // Get content as UUID from json string response
        String uuid = mvcResult.getResponse().getContentAsString();
        uuid = uuid.substring(1, uuid.length() - 1);

        System.out.println("UUID of the created universe: " + uuid);

        System.out.println("Try to start the simulation");

        // Start the simulation using the UUID
        mockMvc.perform(put("/simulation/" + uuid).contentType(MediaType.APPLICATION_JSON)
                .content("{\"mode\":\"run\"}"));

        System.out.println("Simulation started");

        System.out.println("Try to stop the simulation");
        // Stop the simulation using the UUID
        mockMvc.perform(put("/simulation/" + uuid).contentType(MediaType.APPLICATION_JSON)
                .content("{\"mode\":\"stop\"}"));
        System.out.println("Simulation stopped");


        System.out.println("Try to step forward the simulation");
        // Step the simulation using the UUID
        mockMvc.perform(get("/step/" + uuid))
                .andExpect(status().isOk());
        System.out.println("Simulation stepped forward");

        System.out.println("Try to step back the simulation");

        // Step back the simulation using the UUID
        mockMvc.perform(get("/stepBack/" + uuid))
                .andExpect(status().isOk());
        System.out.println("Simulation stepped back");

        System.out.println("Try to create another universe");
        // Create a new universe

        // Add an element to the universe
        mockMvc.perform(post("/universes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"width\":15,\"height\":15}"))
                .andExpect(status().isOk());


        System.out.println("Universe created");

        System.out.println("Try to get the list of universes");
        // Get all universes

        MvcResult res = mockMvc.perform(get("/allUniverses"))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("All universes: " + res.getResponse().getContentAsString());

        System.out.println("Try to get the list of elements supportedof the universe");
        // Get the possible elements
        MvcResult resEts = mockMvc.perform(get("/supportedTypes/" + uuid))
                .andExpect(status().isOk()).andReturn();

        System.out.println("All elements: " + resEts.getResponse().getContentAsString());

        System.out.println("Try to add an element to the universe");

        // Add an element to the universe
        mockMvc.perform(post("/elements/" + uuid).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"cops\",\"x\":0,\"y\":0}"))
                .andExpect(status().isOk());

        System.out.println("Element added");

        System.out.println("Try to delete an element from the universe");
        // Delete an element from the universe
        mockMvc.perform(delete("/elements/" + uuid).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"x\":0,\"y\":0}"))
                .andExpect(status().isOk());

        System.out.println("Element deleted");
        System.out.println("Try to delete the universe");

        // Delete the universe
        mockMvc.perform(delete("/universes/" + uuid))
                .andExpect(status().isOk());

        System.out.println("Universe deleted");
    }
}
