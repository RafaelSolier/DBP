package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Pago;
import com.example.proyecto.domain.entity.Reserva;
import com.example.proyecto.dto.PagoDTO;
import com.example.proyecto.dto.PagoRequestDTO;
import com.example.proyecto.dto.ReservaDTO;
import com.example.proyecto.email.events.PaymentEmailEvent;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.ClienteRepository;
import com.example.proyecto.infrastructure.PagoRepository;
import com.example.proyecto.infrastructure.ReservaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PagoService {
    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ClienteRepository clienteRepository;
    private final ReservaService reservaService;

    @Transactional
    public PagoDTO procesarPago(Long reservaId, PagoRequestDTO dto) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la Reserva con ID: "+ reservaId));
        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setMonto(dto.getMonto());
        pago.setEstado(Enum.valueOf(com.example.proyecto.domain.enums.EstadoPago.class, dto.getEstado()));
        Pago saved = pagoRepository.save(pago);
        reserva.setPago(saved);
        reservaRepository.save(reserva);
        // Enviar correo al cliente
        eventPublisher.publishEvent(new PaymentEmailEvent(this, reserva.getCliente().getNombre(),
                reserva.getCliente().getUser().getEmail(),
                pago.getMonto(),
                reserva.getServicio().getProveedor().getUser().getEmail(),
                reserva.getFechaReserva(),
                reserva.getDireccion(),
                reserva.getServicio().getNombre()));
        // Enviar correo al proveedor
        eventPublisher.publishEvent(new PaymentEmailEvent(this, reserva.getCliente().getNombre(),
                reserva.getServicio().getProveedor().getUser().getEmail(),
                pago.getMonto(),
                reserva.getServicio().getProveedor().getNombre(),
                reserva.getFechaReserva(),
                reserva.getDireccion(),
                reserva.getServicio().getNombre()));

        return modelMapper.map(saved, PagoDTO.class);
    }

    @Transactional
    public List<PagoDTO> getPagosByClienteId(Long clienteId) {
        if (!clienteRepository.findById(clienteId).isPresent()){
            throw new ResourceNotFoundException("Cliente no encontrado");
        }
        // Buscar las reservas del cliente
        List<ReservaDTO> reservas = reservaService.obtenerReservasPorClienteId(clienteId);
        // Buscar los pagos por esas reservas
        List<PagoDTO> pagosDTO = new ArrayList<>();
        for (ReservaDTO reservaDTO : reservas) {
            Reserva res = reservaRepository.findById(reservaDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
            Pago pago = res.getPago();
            pagosDTO.add(modelMapper.map(pago, PagoDTO.class));
        }
        return pagosDTO;
    }

}
