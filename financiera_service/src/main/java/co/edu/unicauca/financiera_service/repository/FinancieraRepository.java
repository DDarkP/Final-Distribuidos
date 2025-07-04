package co.edu.unicauca.financiera_service.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import co.edu.unicauca.financiera_service.model.Deuda;
import jakarta.annotation.PostConstruct;

@Repository
public class FinancieraRepository {

    private final List<Deuda> deudas = new ArrayList<>();

    @PostConstruct
    public void init() {
        deudas.add(new Deuda("1", 150000, "Pérdida de material", LocalDate.of(2024, 3, 1), LocalDate.of(2024, 4, 1),
                "pendiente"));
        deudas.add(new Deuda("1", 200000, "Mora en pago de matrícula", LocalDate.of(2024, 2, 15),
                LocalDate.of(2024, 3, 15), "pendiente"));
        deudas.add(new Deuda("2", 100000, "Pérdida de carnét", LocalDate.of(2024, 4, 10), LocalDate.of(2024, 5, 10),
                "pagada"));
        deudas.add(new Deuda("3", 100000, "Pérdida de carnét", LocalDate.of(2024, 4, 10), LocalDate.of(2024, 5, 10),
                "pendiente"));
        deudas.add(new Deuda("4", 100000, "Pérdida de carnét", LocalDate.of(2024, 4, 10), LocalDate.of(2024, 5, 10),
                "pagada"));
        deudas.add(new Deuda("5", 100000, "Pérdida de carnét", LocalDate.of(2024, 4, 10), LocalDate.of(2024, 5, 10),
                "pendiente"));
                
    }

    public List<Deuda> obtenerDeudasPendientes(String codigoEstudiante) {
        return deudas.stream()
                .filter(d -> d.getCodigoEstudiante().equals(codigoEstudiante)
                        && d.getEstado().equalsIgnoreCase("pendiente"))
                .collect(Collectors.toList());
    }

    public void eliminarDeudasPendientes(String codigoEstudiante) {
        deudas.removeIf(
                d -> d.getCodigoEstudiante().equals(codigoEstudiante) && d.getEstado().equalsIgnoreCase("pendiente"));
    }

    public boolean existeEstudiante(String codigoEstudiante) {
        return deudas.stream().anyMatch(d -> d.getCodigoEstudiante().equals(codigoEstudiante));
    }

}
