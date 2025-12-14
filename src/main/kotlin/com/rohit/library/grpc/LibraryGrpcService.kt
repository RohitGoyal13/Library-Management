package com.rohit.library.grpc

import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService

@GrpcService
class LibraryGrpcService
    : LibraryServiceGrpc.LibraryServiceImplBase() {

    override fun ping(
        request: PingRequest,
        responseObserver: StreamObserver<PingResponse>
    ) {
        val response = PingResponse.newBuilder()
            .setReply("Pong from Library Service: ${request.message}")
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}
