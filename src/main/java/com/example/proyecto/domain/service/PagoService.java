package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Pago;
import com.example.proyecto.domain.entity.Reserva;
import com.example.proyecto.dto.PagoDTO;
import com.example.proyecto.dto.PagoRequestDTO;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.PagoRepository;
import com.example.proyecto.infrastructure.ReservaRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PagoService {
    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PagoService(PagoRepository pagoRepository,
                       ReservaRepository reservaRepository,
                       ModelMapper modelMapper) {
        this.pagoRepository = pagoRepository;
        this.reservaRepository = reservaRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public PagoDTO procesarPago(Long reservaId, PagoRequestDTO dto) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", reservaId));
        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setMonto(dto.getMonto());
        pago.setEstado(Enum.valueOf(com.example.proyecto.domain.enumerates.EstadoPago.class, dto.getEstado()));
        Pago saved = pagoRepository.save(pago);
        reserva.setPago(saved);
        reservaRepository.save(reserva);
        return modelMapper.map(saved, PagoDTO.class);
    }
}
