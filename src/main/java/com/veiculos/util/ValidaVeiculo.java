package com.veiculos.util; // Define o pacote utilitario da aplicacao

import java.util.regex.Pattern; // Importa a classe Pattern para uso de expressoes regulares

import com.veiculos.dto.VeiculoDTO;

/**
 * Utilitario para validacao de placas de veiculos brasileiras.
 * Suporta:
 * - Formato antigo: AAA1234 (3 letras + 4 digitos)
 * - Formato Mercosul: AAA1A23 (3 letras + 1 digito + 1 letra + 2 digitos)
 */
public final class ValidaVeiculo { // Classe final (nao pode ser estendida) contendo metodos utilitarios

    private static final Pattern PADRAO_ANTIGO = Pattern.compile("^[A-Z]{3}[0-9]{4}$"); // Pattern.compile pre-compila a regex (3 letras seguidas de 4 digitos) para reuso eficiente; ^ e $ ancoram inicio/fim
    private static final Pattern PADRAO_MERCOSUL = Pattern.compile("^[A-Z]{3}[0-9][A-Z][0-9]{2}$"); // Regex Mercosul: 3 letras, 1 digito, 1 letra, 2 digitos; tambem ancorada para exigir correspondencia total

    private ValidaVeiculo() { // Construtor privado para impedir instancia
        // Classe utilitaria, nao deve ser instanciada
    }

    /**
     * Valida se a placa informada esta em um dos formatos aceitos.
     * Aceita letras minusculas (serao normalizadas para maiusculas) e ignora espacos ao redor.
     * @param dto objeto VeiculoDTO contendo a placa (pode ser null)
     * @return true se formato antigo ou Mercosul valido; false caso contrario
     */

    // Overload para DTO (nao altera o objeto recebido)
    public static boolean isPlacaValida(VeiculoDTO dto) {
        dto.setPlaca(dto.getPlaca().replaceAll(" ", "").trim().toUpperCase());
        if (dto.getPlaca().length() == 7) {
            return PADRAO_ANTIGO.matcher(dto.getPlaca()).matches() ||
                   PADRAO_MERCOSUL.matcher(dto.getPlaca()).matches();
        }
        return false;
    }   
} // Fim da classe ValidaVeiculo
