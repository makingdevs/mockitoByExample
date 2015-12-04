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
  
  //Genera resta
  public Integer resta(Integer a, Integer b){
    Integer resultado = new Integer(0);
    if(cacheDeCalculadora.existeElResultadoDeResta(a,b)){
      resultado = cacheDeCalculadora.obtenerElResultadoDeResta(a,b);
    } else {
      //Permite hacer la operacion
      resultado = new Integer(a.intValue() - b.intValue()); 
      cacheDeCalculadora.persistirResta(a,b,resultado);
    }
    return resultado;
  }

}
