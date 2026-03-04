package com.sxeix.pokemanager.domain.model;

import com.sxeix.pokemanager.domain.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_pokemon")
@Getter
@Setter
@NoArgsConstructor
public class UserPokemon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "pokemon_num", nullable = false)
    private Integer pokemonNum;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

//    @JdbcTypeCode(SqlTypes.JSON)
//    @Column(columnDefinition = "jsonb")
    @Column(name = "pokemon_details")
    private String pokemonDetails;

    @Column(name = "failure_reason")
    private String failureReason;

}