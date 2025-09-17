package com.veiculos.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import com.veiculos.dto.VeiculoDTO;

class ValidaVeiculoTest {

    @Nested
    @DisplayName("Casos Gerais")
    class Gerais {
        @Test
        void deveAceitarMinusculas() {
            VeiculoDTO d1 = new VeiculoDTO(); d1.setPlaca("abc1234");
            VeiculoDTO d2 = new VeiculoDTO(); d2.setPlaca("bra1a23");
            assertTrue(ValidaVeiculo.isPlacaValida(d1));
            assertTrue(ValidaVeiculo.isPlacaValida(d2));
        }

        @Test
        void deveRejeitarNullOuVazio() {
            VeiculoDTO d1 = new VeiculoDTO(); d1.setPlaca("");
            VeiculoDTO d2 = new VeiculoDTO(); d2.setPlaca("   ");
            assertFalse(ValidaVeiculo.isPlacaValida((VeiculoDTO) null));
            assertFalse(ValidaVeiculo.isPlacaValida(d1));
            assertFalse(ValidaVeiculo.isPlacaValida(d2));
        }

        @Test
        void deveRejeitarTamanhoIncorreto() {
            VeiculoDTO d1 = new VeiculoDTO(); d1.setPlaca("AB123");
            VeiculoDTO d2 = new VeiculoDTO(); d2.setPlaca("ABCD1234");
            assertFalse(ValidaVeiculo.isPlacaValida(d1));
            assertFalse(ValidaVeiculo.isPlacaValida(d2));
        }

        @Test
        void deveRejeitarCaracteresEspeciais() {
            VeiculoDTO d1 = new VeiculoDTO(); d1.setPlaca("AB@1234");
            VeiculoDTO d2 = new VeiculoDTO(); d2.setPlaca("ABC1-23");
            assertFalse(ValidaVeiculo.isPlacaValida(d1));
            assertFalse(ValidaVeiculo.isPlacaValida(d2));
        }
    }

}
