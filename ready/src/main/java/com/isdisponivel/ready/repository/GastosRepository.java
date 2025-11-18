package com.isdisponivel.ready.repository;

import com.isdisponivel.ready.model.Gastos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GastosRepository extends JpaRepository<Gastos, Long> {

    List<Gastos> findByFormaDePagamento(String formaDePagamento);

    List<Gastos> findByAtrasadoTrue();

    List<Gastos> findByPagoFalse();

    List<Gastos> findByRecorrenteTrueOrderByDataCriacaoDesc();

    @Query("SELECT g FROM Gastos g WHERE g.descricao LIKE %:termo% ORDER BY g.dataCriacao DESC")
    List<Gastos> buscarPorDescricao(@Param("termo") String termo);

    @Query("SELECT g FROM Gastos g WHERE g.pago = :pago ORDER BY g.dataCriacao DESC")
    List<Gastos> buscarPorStatusPagamento(@Param("pago") Boolean pago);

    @Query("SELECT g FROM Gastos g WHERE g.dataCriacao BETWEEN :dataInicio AND :dataFim ORDER BY g.dataCriacao DESC")
    List<Gastos> buscarPorPeriodo(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

}