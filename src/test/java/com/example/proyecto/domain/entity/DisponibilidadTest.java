package com.example.proyecto.domain.entity;

import com.example.proyecto.domain.enums.DiaSemana;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DisponibilidadTest {

  @Test
  void gettersAndSetters_shouldWork() {
      Disponibilidad disponibilidad = new Disponibilidad();

      disponibilidad.setId(1L);
      disponibilidad.setDiaSemana(DiaSemana.LUNES);
      disponibilidad.setHoraInicio(LocalTime.of(9, 0));
      disponibilidad.setHoraFin(LocalTime.of(17, 0));
      Servicio servicio = new Servicio();
      disponibilidad.setServicio(servicio);

      assertAll(
          () -> assertEquals(1L, disponibilidad.getId()),
          () -> assertEquals(DiaSemana.LUNES, disponibilidad.getDiaSemana()),
          () -> assertEquals(LocalTime.of(9, 0), disponibilidad.getHoraInicio()),
          () -> assertEquals(LocalTime.of(17, 0), disponibilidad.getHoraFin()),
          () -> assertEquals(servicio, disponibilidad.getServicio())
      );
  }
}
