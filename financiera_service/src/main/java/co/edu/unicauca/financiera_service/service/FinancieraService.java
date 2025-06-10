package co.edu.unicauca.financiera_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unicauca.financiera_service.dto.DeudaDTO;
import co.edu.unicauca.financiera_service.repository.FinancieraRepository;

@Service
public class FinancieraService {

    private final FinancieraRepository repo;

    @Autowired
    public FinancieraService(FinancieraRepository repo) {
        this.repo = repo;
    }

    public List<DeudaDTO> obtenerPendientes(String codigoEstudiante) {
        var deudas = repo.obtenerDeudasPendientes(codigoEstudiante);

        // Si no hay deudas y tampoco hay historial de ese estudiante, retornamos null
        boolean existeHistorial = repo.existeEstudiante(codigoEstudiante);

        if (!existeHistorial) {
            return null; // Esto lo usaremos para devolver un 404 más arriba
        }

        return deudas.stream().map(deuda -> {
            DeudaDTO dto = new DeudaDTO();
            dto.setMonto(deuda.getMonto());
            dto.setMotivo(deuda.getMotivo());
            dto.setFechaGeneracion(deuda.getFechaGeneracion());
            dto.setFechaLimite(deuda.getFechaLimite());
            dto.setEstado(deuda.getEstado());
            return dto;
        }).collect(Collectors.toList());
    }

    public void eliminarPendientes(String codigoEstudiante) {
        repo.eliminarDeudasPendientes(codigoEstudiante);
    }
}
