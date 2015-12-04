package com.makingdevs;

public interface CacheDeCalculadora {
  void persistirSuma(Integer a, Integer b, Integer r);
  boolean existeElResultadoDeSumar(Integer a, Integer b);
  Integer obtenerElResultadoDeSumar(Integer a, Integer b);

  void persistirResta(Integer a, Integer b, Integer r);
  boolean existeElResultadoDeResta(Integer a, Integer b);
  Integer obtenerElResultadoDeResta(Integer a, Integer b);
}




