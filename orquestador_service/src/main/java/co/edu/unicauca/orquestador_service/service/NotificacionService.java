package co.edu.unicauca.orquestador_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificacionService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void enviarNotificacionGeneral(String mensaje) {
        messagingTemplate.convertAndSend("/notificaciones/general", mensaje);
    }

    public void enviarNotificacionPorArea(String area, String mensaje) {
        messagingTemplate.convertAndSend("/notificaciones/" + area, mensaje); // 👈 Así
    }
}