package com.sxeix.pokemanager.infrastructure.persistence;

import com.sxeix.pokemanager.domain.enums.Status;
import com.sxeix.pokemanager.domain.model.UserPokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface JpaUserPokemonRepository extends JpaRepository<UserPokemon, Integer> {

    @Query("select count(up) from UserPokemon up where up.user.id = :userId and up.status in :statuses")
    long countAllByUserIdAndStatus(@Param("userId") Integer userId, @Param("statuses") Collection<Status> statuses);

}
