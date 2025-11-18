package com.isdisponivel.ready.service;

import com.isdisponivel.ready.exception.GastosNotFoundException;
import com.isdisponivel.ready.model.Gastos;
import com.isdisponivel.ready.repository.GastosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GastosService {

    private final GastosRepository gastosRepository;

    // ==================== CRIAR ====================

    public Gastos criar(Gastos gastos) {
        log.info("Criando novo gasto: {}", gastos.getDescricao());
        
        validarGastosObrigatorios(gastos);
        
        Gastos gastosCriado = gastosRepository.save(gastos);
        log.info("Gasto criado com sucesso. ID: {}", gastosCriado.getId());
        
        return gastosCriado;
    }

    // ==================== LEITURA ====================

    @Transactional(readOnly = true)
    public Gastos obterPorId(Long id) {
        log.debug("Buscando gasto com ID: {}", id);
        
        return gastosRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Gasto não encontrado. ID: {}", id);
                    return new GastosNotFoundException(id);
                });
    }

    @Transactional(readOnly = true)
    public List<Gastos> listarTodos() {
        log.debug("Listando todos os gastos");
        return gastosRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Gastos> buscarPorFormaDePagamento(String formaDePagamento) {
        log.debug("Buscando gastos por forma de pagamento: {}", formaDePagamento);
        return gastosRepository.findByFormaDePagamento(formaDePagamento);
    }

    @Transactional(readOnly = true)
    public List<Gastos> buscarAtrasados() {
        log.debug("Buscando gastos atrasados");
        return gastosRepository.findByAtrasadoTrue();
    }

    @Transactional(readOnly = true)
    public List<Gastos> buscarNaoPagos() {
        log.debug("Buscando gastos não pagos");
        return gastosRepository.findByPagoFalse();
    }

    @Transactional(readOnly = true)
    public List<Gastos> buscarRecorrentes() {
        log.debug("Buscando gastos recorrentes");
        return gastosRepository.findByRecorrenteTrueOrderByDataCriacaoDesc();
    }

    @Transactional(readOnly = true)
    public List<Gastos> buscarPorDescricao(String termo) {
        log.debug("Buscando gastos por descrição: {}", termo);
        return gastosRepository.buscarPorDescricao(termo);
    }

    @Transactional(readOnly = true)
    public List<Gastos> buscarPorStatusPagamento(Boolean pago) {
        log.debug("Buscando gastos por status de pagamento: {}", pago);
        return gastosRepository.buscarPorStatusPagamento(pago);
    }

    @Transactional(readOnly = true)
    public List<Gastos> buscarPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        log.debug("Buscando gastos entre {} e {}", dataInicio, dataFim);
        return gastosRepository.buscarPorPeriodo(dataInicio, dataFim);
    }

    // ==================== ATUALIZAR ====================

    public Gastos atualizar(Long id, Gastos gastosAtualizado) {
        log.info("Atualizando gasto com ID: {}", id);
        
        Gastos gastos = obterPorId(id);
        
        validarGastosObrigatorios(gastosAtualizado);
        
        gastos.setDescricao(gastosAtualizado.getDescricao());
        gastos.setFormaDePagamento(gastosAtualizado.getFormaDePagamento());
        gastos.setValor(gastosAtualizado.getValor());
        gastos.setPago(gastosAtualizado.getPago());
        gastos.setAtrasado(gastosAtualizado.getAtrasado());
        gastos.setRecorrente(gastosAtualizado.getRecorrente());
        
        Gastos gastosSalvo = gastosRepository.save(gastos);
        log.info("Gasto atualizado com sucesso. ID: {}", id);
        
        return gastosSalvo;
    }

    public Gastos marcarComoPago(Long id) {
        log.info("Marcando gasto com ID {} como pago", id);
        
        Gastos gastos = obterPorId(id);
        gastos.setPago(true);
        gastos.setAtrasado(false);
        
        Gastos gastosSalvo = gastosRepository.save(gastos);
        log.info("Gasto marcado como pago. ID: {}", id);
        
        return gastosSalvo;
    }

    public Gastos marcarComoNaoPago(Long id) {
        log.info("Marcando gasto com ID {} como não pago", id);
        
        Gastos gastos = obterPorId(id);
        gastos.setPago(false);
        
        Gastos gastosSalvo = gastosRepository.save(gastos);
        log.info("Gasto marcado como não pago. ID: {}", id);
        
        return gastosSalvo;
    }

    public Gastos marcarComoAtrasado(Long id) {
        log.info("Marcando gasto com ID {} como atrasado", id);
        
        Gastos gastos = obterPorId(id);
        gastos.setAtrasado(true);
        
        Gastos gastosSalvo = gastosRepository.save(gastos);
        log.info("Gasto marcado como atrasado. ID: {}", id);
        
        return gastosSalvo;
    }

    // ==================== DELETAR ====================

    public void deletar(Long id) {
        log.info("Deletando gasto com ID: {}", id);
        
        Gastos gastos = obterPorId(id);
        gastosRepository.delete(gastos);
        
        log.info("Gasto deletado com sucesso. ID: {}", id);
    }

    // ==================== ESTATÍSTICAS ====================

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalGastos() {
        log.debug("Calculando total de gastos");
        return listarTodos().stream()
                .map(Gastos::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalAtrasado() {
        log.debug("Calculando total de gastos atrasados");
        return buscarAtrasados().stream()
                .map(Gastos::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalNaoPago() {
        log.debug("Calculando total de gastos não pagos");
        return buscarNaoPagos().stream()
                .map(Gastos::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public Long contarGastosNaoPagos() {
        log.debug("Contando gastos não pagos");
        return buscarNaoPagos().stream().count();
    }

    // ==================== VALIDAÇÕES ====================

    private void validarGastosObrigatorios(Gastos gastos) {
        if (gastos == null) {
            log.error("Tentativa de salvar gasto nulo");
            throw new IllegalArgumentException("Gasto não pode ser nulo");
        }

        if (gastos.getDescricao() == null || gastos.getDescricao().isBlank()) {
            log.warn("Descrição do gasto vazia ou nula");
            throw new IllegalArgumentException("Descrição é obrigatória");
        }

        if (gastos.getFormaDePagamento() == null || gastos.getFormaDePagamento().isBlank()) {
            log.warn("Forma de pagamento vazia ou nula");
            throw new IllegalArgumentException("Forma de pagamento é obrigatória");
        }

        if (gastos.getValor() == null || gastos.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Valor inválido: {}", gastos.getValor());
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }
    }

}