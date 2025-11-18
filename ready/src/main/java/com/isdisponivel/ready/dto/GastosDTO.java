package com.isdisponivel.ready.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GastosDTO {

    @NotBlank(message = "Descrição não pode ser vazia")
    @Size(min = 3, max = 255, message = "Descrição deve ter entre 3 e 255 caracteres")
    private String descricao;

    @NotBlank(message = "Forma de pagamento é obrigatória")
    @Size(min = 3, max = 50, message = "Forma de pagamento deve ter entre 3 e 50 caracteres")
    private String formaDePagamento;

    @NotNull(message = "Valor não pode ser nulo")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    @DecimalMax(value = "999999.99", message = "Valor não pode ser maior que 999.999,99")
    private BigDecimal valor;

    @NotNull(message = "Status de pagamento é obrigatório")
    private Boolean pago = false;

    @NotNull(message = "Status de atraso é obrigatório")
    private Boolean atrasado = false;

    @NotNull(message = "Status de recorrência é obrigatório")
    private Boolean recorrente = false;

}