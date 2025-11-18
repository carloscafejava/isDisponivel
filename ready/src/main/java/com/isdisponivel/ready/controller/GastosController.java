package com.isdisponivel.ready.controller;

import com.isdisponivel.ready.dto.GastosDTO;
import com.isdisponivel.ready.model.Gastos;
import com.isdisponivel.ready.service.GastosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/gastos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class GastosController {

    private final GastosService gastosService;

    // ==================== CRIAR ====================

    @PostMapping
    public ResponseEntity<Gastos> criar(@Valid @RequestBody GastosDTO gastosDTO) {
        log.info("Recebido request para criar gasto: {}", gastosDTO.getDescricao());

        Gastos gastos = construirGastosDoDTO(gastosDTO);
        Gastos gastosCriado = gastosService.criar(gastos);

        return ResponseEntity.status(HttpStatus.CREATED).body(gastosCriado);
    }

    // ==================== LEITURA ====================

    @GetMapping
    public ResponseEntity<List<Gastos>> listarTodos() {
        log.info("Listando todos os gastos");
        List<Gastos> gastos = gastosService.listarTodos();
        return ResponseEntity.ok(gastos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gastos> obterPorId(@PathVariable Long id) {
        log.info("Buscando gasto com ID: {}", id);
        Gastos gastos = gastosService.obterPorId(id);
        return ResponseEntity.ok(gastos);
    }

    @GetMapping("/forma-pagamento/{forma}")
    public ResponseEntity<List<Gastos>> buscarPorFormaDePagamento(@PathVariable String forma) {
        log.info("Buscando gastos por forma de pagamento: {}", forma);
        List<Gastos> gastos = gastosService.buscarPorFormaDePagamento(forma);
        return ResponseEntity.ok(gastos);
    }

    @GetMapping("/atrasados")
    public ResponseEntity<List<Gastos>> buscarAtrasados() {
        log.info("Buscando gastos atrasados");
        List<Gastos> gastos = gastosService.buscarAtrasados();
        return ResponseEntity.ok(gastos);
    }

    @GetMapping("/nao-pagos")
    public ResponseEntity<List<Gastos>> buscarNaoPagos() {
        log.info("Buscando gastos não pagos");
        List<Gastos> gastos = gastosService.buscarNaoPagos();
        return ResponseEntity.ok(gastos);
    }

    @GetMapping("/recorrentes")
    public ResponseEntity<List<Gastos>> buscarRecorrentes() {
        log.info("Buscando gastos recorrentes");
        List<Gastos> gastos = gastosService.buscarRecorrentes();
        return ResponseEntity.ok(gastos);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Gastos>> buscarPorDescricao(@RequestParam String termo) {
        log.info("Buscando gastos por descrição: {}", termo);

        if (termo == null || termo.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        List<Gastos> gastos = gastosService.buscarPorDescricao(termo);
        return ResponseEntity.ok(gastos);
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<Gastos>> buscarPorPeriodo(
            @RequestParam LocalDateTime dataInicio,
            @RequestParam LocalDateTime dataFim) {
        log.info("Buscando gastos entre {} e {}", dataInicio, dataFim);

        if (dataInicio.isAfter(dataFim)) {
            return ResponseEntity.badRequest().build();
        }

        List<Gastos> gastos = gastosService.buscarPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(gastos);
    }

    // ==================== ATUALIZAR ====================

    @PutMapping("/{id}")
    public ResponseEntity<Gastos> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody GastosDTO gastosDTO) {
        log.info("Atualizando gasto com ID: {}", id);

        Gastos gastosAtualizado = construirGastosDoDTO(gastosDTO);
        Gastos gastos = gastosService.atualizar(id, gastosAtualizado);

        return ResponseEntity.ok(gastos);
    }

    @PatchMapping("/{id}/marcar-pago")
    public ResponseEntity<Gastos> marcarComoPago(@PathVariable Long id) {
        log.info("Marcando gasto com ID {} como pago", id);
        Gastos gastos = gastosService.marcarComoPago(id);
        return ResponseEntity.ok(gastos);
    }

    @PatchMapping("/{id}/marcar-nao-pago")
    public ResponseEntity<Gastos> marcarComoNaoPago(@PathVariable Long id) {
        log.info("Marcando gasto com ID {} como não pago", id);
        Gastos gastos = gastosService.marcarComoNaoPago(id);
        return ResponseEntity.ok(gastos);
    }

    @PatchMapping("/{id}/marcar-atrasado")
    public ResponseEntity<Gastos> marcarComoAtrasado(@PathVariable Long id) {
        log.info("Marcando gasto com ID {} como atrasado", id);
        Gastos gastos = gastosService.marcarComoAtrasado(id);
        return ResponseEntity.ok(gastos);
    }

    // ==================== DELETAR ====================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        log.info("Deletando gasto com ID: {}", id);
        gastosService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== ESTATÍSTICAS ====================

    @GetMapping("/estatisticas/total")
    public ResponseEntity<BigDecimal> calcularTotalGastos() {
        log.info("Calculando total de gastos");
        BigDecimal total = gastosService.calcularTotalGastos();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/estatisticas/total-atrasado")
    public ResponseEntity<BigDecimal> calcularTotalAtrasado() {
        log.info("Calculando total de gastos atrasados");
        BigDecimal totalAtrasado = gastosService.calcularTotalAtrasado();
        return ResponseEntity.ok(totalAtrasado);
    }

    @GetMapping("/estatisticas/total-nao-pago")
    public ResponseEntity<BigDecimal> calcularTotalNaoPago() {
        log.info("Calculando total de gastos não pagos");
        BigDecimal totalNaoPago = gastosService.calcularTotalNaoPago();
        return ResponseEntity.ok(totalNaoPago);
    }

    @GetMapping("/estatisticas/contar-nao-pagos")
    public ResponseEntity<Long> contarGastosNaoPagos() {
        log.info("Contando gastos não pagos");
        Long total = gastosService.contarGastosNaoPagos();
        return ResponseEntity.ok(total);
    }

    // ==================== HELPER ====================

    private Gastos construirGastosDoDTO(GastosDTO dto) {
        return Gastos.builder()
                .descricao(dto.getDescricao())
                .formaDePagamento(dto.getFormaDePagamento())
                .valor(dto.getValor())
                .pago(dto.getPago())
                .atrasado(dto.getAtrasado())
                .recorrente(dto.getRecorrente())
                .build();
    }

}