package co.edu.unicauca.deporte_service.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unicauca.deporte_service.DTOs.PendienteDeporteDTO;
import co.edu.unicauca.deporte_service.Repository.DeporteRepository;

@Service
public class DeporteService {

    private final DeporteRepository repo;

    @Autowired
    public DeporteService(DeporteRepository repo) {
        this.repo = repo;
    }

    public List<PendienteDeporteDTO> consultarPendientes(String codigoEstudiante) {
        return repo.obtenerPendientes(codigoEstudiante).stream().map(p -> {
            PendienteDeporteDTO dto = new PendienteDeporteDTO();
            dto.setImplemento(p.getImplemento());
            dto.setFechaPrestamo(p.getFechaPrestamo());
            dto.setFechaEstimada(p.getFechaEstimada());
            dto.setFechaReal(p.getFechaReal());
            return dto;
        }).collect(Collectors.toList());
    }

    public void eliminarPendientes(String codigoEstudiante) {
        repo.eliminarPendientes(codigoEstudiante);
    }
}

