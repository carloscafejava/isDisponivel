package com.isdisponivel.ready.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "produto_ent")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"usuario", "categoria"})
public class Gastos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Descrição não pode ser vazia")
    @Column(nullable = false, length = 255)
    private String descricao;

    @NotBlank(message = "Forma de pagamento é obrigatória")
    @Column(nullable = false, length = 50)
    private String formaDePagamento;

    @NotNull(message = "Valor não pode ser nulo")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private Boolean pago = false;

    @Column(nullable = false)
    private Boolean atrasado = false;

    @Column(nullable = false)
    private Boolean recorrente = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    @Version
    @Column(nullable = false)
    private Long versao = 0L;

    // Relacionamentos (preparados para expansão futura)
    // @ManyToOne
    // @JoinColumn(name = "usuario_id", nullable = false)
    // private Usuario usuario;

    // @ManyToOne
    // @JoinColumn(name = "categoria_id")
    // private Categoria categoria;

}