package co.edu.unicauca.laboratorio_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unicauca.laboratorio_service.model.Prestamo;
import co.edu.unicauca.laboratorio_service.repository.LaboratorioRepository;

@Service
public class LaboratorioService {

    private final LaboratorioRepository laboratorioRepository;

    @Autowired
    public LaboratorioService(LaboratorioRepository laboratorioRepository) {
        this.laboratorioRepository = laboratorioRepository;
    }

    public List<Prestamo> obtenerPrestamosPendientes(String codigoEstudiante) {
        if (!laboratorioRepository.existeEstudiante(codigoEstudiante)) {
            return null;
        }

        return laboratorioRepository.obtenerPendientes(codigoEstudiante);
    }

    // public List<Prestamo> obtenerPrestamosPendientes(String codigoEstudiante) {
    // return laboratorioRepository.findByCodigoEstudiante(codigoEstudiante);
    // }

    public void eliminarPendientes(String codigoEstudiante) {
        laboratorioRepository.eliminarPrestamos(codigoEstudiante);
    }

}
