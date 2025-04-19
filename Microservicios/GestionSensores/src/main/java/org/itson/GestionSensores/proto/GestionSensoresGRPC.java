//package org.itson.GestionSensores.proto;
//
//import org.itson.GestionSensores.persistence.IGestionSensoresRepository;
//import org.itson.GestionSensores.persistence.IInvernaderosRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class GestionSensoresGRPC extends GestionSensoresProto.SensorServiceImplBase {
//    @Autowired
//    private IGestionSensoresRepository gestionSensoresRepository;
//
//    @Autowired
//    private IInvernaderosRepository invernaderosRepository;
//
//    @Override
//    public void getSensorByMac(SensorRequest request, StreamObserver<SensorResponse> responseObserver) {
//        // Lógica para buscar el sensor y responder
//    }
//}
