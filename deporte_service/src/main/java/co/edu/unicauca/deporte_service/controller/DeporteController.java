package co.edu.unicauca.deporte_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.deporte_service.DTOs.PendienteDeporteDTO;
import co.edu.unicauca.deporte_service.DTOs.SolicitudDTO;
import co.edu.unicauca.deporte_service.Service.DeporteService;

@RestController
@RequestMapping("/api/deporte")
@CrossOrigin
public class DeporteController {

    private final DeporteService servicio;

    @Autowired
    public DeporteController(DeporteService servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/pendientes")
    public List<PendienteDeporteDTO> obtenerPendientes(@RequestBody SolicitudDTO solicitud) {
        return servicio.consultarPendientes(solicitud.getCodigoEstudiante());
    }


    @DeleteMapping("/pendientes")
    public Map<String, String> eliminarPendientes(@RequestBody SolicitudDTO solicitud) {
        servicio.eliminarPendientes(solicitud.getCodigoEstudiante());
        return Map.of("mensaje", "Implementos eliminados con éxito");
    }
}

