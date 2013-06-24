package com.makingdevs;

import static org.junit.Assert.*;
import org.junit.Test;

public class CalculadoraTests {

  @Test
  public void pruebaSumaDeDosNumeros(){
    Calculadora calculadora = new Calculadora();
    Integer resultado = calculadora.suma(4,5);
    assertTrue(9 == resultado);
  }
}
