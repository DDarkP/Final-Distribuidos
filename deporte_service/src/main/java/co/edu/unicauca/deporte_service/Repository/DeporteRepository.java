package co.edu.unicauca.deporte_service.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import co.edu.unicauca.deporte_service.Model.PrestamoDeporte;

@Repository
public class DeporteRepository {

        private final List<PrestamoDeporte> prestamos = new ArrayList<>();

        public DeporteRepository() {
                // Datos precargados
                prestamos.add(new PrestamoDeporte("1", "Raqueta", LocalDate.of(2024, 5, 1),
                                LocalDate.of(2024, 5, 10), null));
                prestamos.add(new PrestamoDeporte("1", "Red de voleibol", LocalDate.of(2024, 5, 3),
                                LocalDate.of(2024, 5, 15), null));

                prestamos.add(new PrestamoDeporte("2", "Balón de fútbol", LocalDate.of(2024, 4, 20),
                                LocalDate.of(2024, 4, 25), LocalDate.of(2024, 4, 24)));

                prestamos.add(new PrestamoDeporte("3", "Disco de frisbee", LocalDate.of(2024, 5, 5),
                                LocalDate.of(2024, 5, 12), null));

                prestamos.add(new PrestamoDeporte("4", "Cuerda para saltar", LocalDate.of(2024, 5, 1),
                                LocalDate.of(2024, 5, 7), null));
                prestamos.add(new PrestamoDeporte("5", "Cuerda para saltar", LocalDate.of(2024, 5, 1),
                                LocalDate.of(2024, 5, 7), LocalDate.of(2024, 4, 24)));
        }

        public List<PrestamoDeporte> obtenerPendientes(String codigoEstudiante) {
                return prestamos.stream()
                                .filter(p -> p.getCodigoEstudiante().equals(codigoEstudiante)
                                                && p.getFechaReal() == null)
                                .collect(Collectors.toList());
        }

        public void eliminarPendientes(String codigoEstudiante) {
                prestamos.removeIf(p -> p.getCodigoEstudiante().equals(codigoEstudiante) && p.getFechaReal() == null);
        }

        public boolean estudianteRegistrado(String codigoEstudiante) {
                return prestamos.stream().anyMatch(p -> p.getCodigoEstudiante().equals(codigoEstudiante));
        }

}
