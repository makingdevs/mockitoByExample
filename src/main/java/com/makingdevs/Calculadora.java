package com.makingdevs;

public class Calculadora {

  CacheDeCalculadora cacheDeCalculadora;

  public Integer suma(Integer a, Integer b){
    Integer resultado = new Integer(0);
    if(cacheDeCalculadora.existeElResultadoDeSumar(a,b)){
      resultado = cacheDeCalculadora.obtenerElResultadoDeSumar(a,b);
    } else {
      resultado = new Integer(a.intValue() + b.intValue()); 
      cacheDeCalculadora.persistirSuma(a,b,resultado);
    }
    return resultado;
  }
}
