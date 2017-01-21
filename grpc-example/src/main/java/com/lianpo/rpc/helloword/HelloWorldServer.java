package com.lianpo.rpc.helloword;

import io.grpc.Server;
import io.grpc.ServerBuilder;

/**
 * Created by liz on 2017/1/20.
 *
 * @auther liz
 */
public class HelloWorldServer {
    private Server server;

    private void start(){
        /*int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(null)
                .build()
                .start();   
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {

            }
        });*/
    }


    /*static class GreeterImpl extends GreeterGrpc.GreeterImplBase {

        @Override
        public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
            HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }*/

}
