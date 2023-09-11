package ch.heia.isc.si.siiitp1groupe6spring2223.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface PlayerRepository extends CrudRepository<Player, Integer> {
    List<Player> findAll();

}
