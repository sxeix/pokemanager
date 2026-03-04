package com.sxeix.pokemanager.grpc;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sxeix.pokemanager.*;
import com.sxeix.pokemanager.domain.service.IdempotencyService;
import com.sxeix.pokemanager.domain.service.UserService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final IdempotencyService idempotencyService;
    private final UserService userService;

    @Override
    public void getUser(GetUserRequest getUserRequest, StreamObserver<GetUserResponse> responseStreamObserver) {

        var response = userService.findUserById(getUserRequest.getId())
                .map(value -> GetUserResponse.newBuilder()
                        .setId(value.getId())
                        .setUsername(value.getUsername())
                        .setEmail(value.getEmail())
                        .build()).orElseGet(() -> GetUserResponse.newBuilder().build());

        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {

        var idempotencyKey = !request.getIdempotencyKey().isBlank() ? request.getIdempotencyKey() : UUID.randomUUID().toString();
        var idempotencyRecord = idempotencyService.find(idempotencyKey);
        if (idempotencyRecord.isPresent() && idempotencyRecord.get().getStatus() == com.sxeix.pokemanager.domain.enums.Status.COMPLETED) {
            try {
                responseObserver.onNext(CreateUserResponse.parseFrom(idempotencyRecord.get().getResponseBody()));
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
            CreateUserResponse response = userService.create(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            idempotencyService.fail(idempotencyKey);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asException());
        }
    }

}
