package co.edu.unicauca.laboratorio_service.repository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import co.edu.unicauca.laboratorio_service.model.Prestamo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class LaboratorioRepository {

    private final List<Prestamo> prestamos = new ArrayList<>();

    @PostConstruct
    public void init() {
        prestamos.add(new Prestamo("1001", LocalDate.now().minusDays(10), LocalDate.now().minusDays(5), null, "vencido", "Microscopio"));
        prestamos.add(new Prestamo("1001", LocalDate.now().minusDays(3), LocalDate.now().plusDays(2), null, "activo", "Osciloscopio"));
        prestamos.add(new Prestamo("1003", LocalDate.now().minusDays(6), LocalDate.now().minusDays(1), LocalDate.now().minusDays(1), "devuelto", "Computador de laboratorio"));
    }

    public List<Prestamo> findByCodigoEstudiante(String codigo) {
        return prestamos.stream()
                .filter(p -> p.getCodigoEstudiante().equals(codigo) && !p.getEstado().equalsIgnoreCase("devuelto"))
                .collect(Collectors.toList());
    }

    public void eliminarPrestamos(String codigo) {
        prestamos.removeIf(p -> p.getCodigoEstudiante().equals(codigo) && !p.getEstado().equalsIgnoreCase("devuelto"));
    }
}
