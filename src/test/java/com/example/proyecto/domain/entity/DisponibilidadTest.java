package com.example.proyecto.domain.entity;

import com.example.proyecto.domain.enums.DiaSemana;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import static org.assertj.core.api.Assertions.*;

class DisponibilidadTest {

    @Test
    void gettersAndSettersWork() {
        Disponibilidad d = new Disponibilidad();
        d.setId(5L);
        d.setDiaSemana(DiaSemana.LUNES);
        LocalTime inicio = LocalTime.of(9, 0);
        LocalTime fin    = LocalTime.of(17, 30);
        d.setHoraInicio(inicio);
        d.setHoraFin(fin);

        Servicio s = new Servicio();
        s.setId(10L);
        d.setServicio(s);

        assertThat(d.getId()).isEqualTo(5L);
        assertThat(d.getDiaSemana()).isEqualTo(DiaSemana.LUNES);
        assertThat(d.getHoraInicio()).isEqualTo(inicio);
        assertThat(d.getHoraFin()).isEqualTo(fin);
        assertThat(d.getServicio()).isSameAs(s);
    }

    @Test
    void equalsAndHashCode_considerAllFields() {
        LocalTime inicio = LocalTime.of(8, 0);
        LocalTime fin    = LocalTime.of(12, 0);

        Servicio s = new Servicio();
        s.setId(2L);

        Disponibilidad d1 = new Disponibilidad();
        d1.setId(1L);
        d1.setDiaSemana(DiaSemana.MARTES);
        d1.setHoraInicio(inicio);
        d1.setHoraFin(fin);
        d1.setServicio(s);

        Disponibilidad d2 = new Disponibilidad();
        d2.setId(1L);
        d2.setDiaSemana(DiaSemana.MARTES);
        d2.setHoraInicio(inicio);
        d2.setHoraFin(fin);
        d2.setServicio(s);

        assertThat(d1).isEqualTo(d2);
        assertThat(d1.hashCode()).isEqualTo(d2.hashCode());
    }

    @Test
    void toString_includesKeyFields() {
        Disponibilidad d = new Disponibilidad();
        d.setId(7L);
        d.setDiaSemana(DiaSemana.MIERCOLES);
        d.setHoraInicio(LocalTime.of(14, 0));
        d.setHoraFin(LocalTime.of(18, 0));

        String str = d.toString();
        assertThat(str)
                .contains("id=7")
                .contains("diaSemana=MIERCOLES")
                .contains("horaInicio=14:00")
                .contains("horaFin=18:00");
    }

    @Test
    void defaultValuesAreNull() {
        Disponibilidad d = new Disponibilidad();
        assertThat(d.getId()).isNull();
        assertThat(d.getDiaSemana()).isNull();
        assertThat(d.getHoraInicio()).isNull();
        assertThat(d.getHoraFin()).isNull();
        assertThat(d.getServicio()).isNull();
    }
}
