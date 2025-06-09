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
        return repo.obtenerDeudasPendientes(codigoEstudiante).stream().map(deuda -> {
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
