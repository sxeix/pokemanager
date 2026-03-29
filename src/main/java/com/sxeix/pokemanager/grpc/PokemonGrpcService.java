package com.sxeix.pokemanager.grpc;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sxeix.pokemanager.AddPokemonRequest;
import com.sxeix.pokemanager.AddPokemonResponse;
import com.sxeix.pokemanager.PokemonServiceGrpc;
import com.sxeix.pokemanager.domain.exception.TeamFullException;
import com.sxeix.pokemanager.domain.service.IdempotencyService;
import com.sxeix.pokemanager.domain.service.UserPokemonService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class PokemonGrpcService extends PokemonServiceGrpc.PokemonServiceImplBase {

    private final IdempotencyService idempotencyService;
    private final UserPokemonService pokemonService;

    @Override
    public void addPokemon(AddPokemonRequest request, StreamObserver<AddPokemonResponse> responseObserver) {

        var idempotencyKey = !request.getIdempotencyKey().isBlank() ? request.getIdempotencyKey() : UUID.randomUUID().toString();
        var idempotencyRecord = idempotencyService.find(idempotencyKey);
        if (idempotencyRecord.isPresent() && idempotencyRecord.get().getStatus() == com.sxeix.pokemanager.domain.enums.Status.COMPLETED) {
            try {
                responseObserver.onNext(AddPokemonResponse.parseFrom(idempotencyRecord.get().getResponseBody()));
                responseObserver.onCompleted();
                return;
            } catch (InvalidProtocolBufferException e) {
                idempotencyService.fail(idempotencyKey);
                responseObserver.onError(Status.INTERNAL.withDescription("Idempotent response failed").asException());
                return;
            }
        } else {
            idempotencyService.create(idempotencyKey);
        }

        try {
            pokemonService.initiateAddPokemon(request);
            AddPokemonResponse response = AddPokemonResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (TeamFullException e) {
            idempotencyService.fail(idempotencyKey);
            responseObserver.onError(Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asException());
        } catch (Exception e) {
            idempotencyService.fail(idempotencyKey);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asException());
        }

    }

}
