Ultimo intento....

Otro mas....

Nuevo cambio despues de un tiempo....

Cambio.....

En esta ocasión, con la finalidad de aportar algo a la comunidad(y ver si de paso me gano un pase) les quiero poner un tema que les de a conocer una herramienta que puedan usar los que están haciendo desarrollo en Java.

Les platico brevemente el escenario que me ha puesto a escribir de este tema: hace un par de meses me asignaron a la tarea de hacer el mentoring de un grupo de desarrolladores, el objetivo era "aumentar la calidad del software que se desarrollaba", y esto lo fui elaborando a través de varias técnicas: programación en pares, dojos, control de versiones, mediciones de tiempos, pomodoros, etc., y la guía técnica para ir creciendo un proyecto con Java; yo vengo desarrollado Groovy desde hace mucho tiempo y esto se me hizo interesante para medir nuevamente el tiempo de desarrollo de un proyecto con Java. El factor común de todos era Maven y por supuesto Java(en varios niveles de experiencia), lo cual lo hacía más interesante.

En mi humilde opinión, uno de los tantos factores para mejorar la calidad del software que se escribe son las pruebas, y para este equipo eran un mundo desconocido(a excepción de uno de ellos), lo cual fue bueno por que podrían aprenderlas sin vicios y diferenciarlas desde un principio, y malo por que no tenían pruebas de ningún software. Su proceso de desarrollo para determinar si un fragmento de código funcionaba era tardado, y gracias a esto, también fuimos tomando también un mejor ritmo.

Siempre se confunden las pruebas de integración con las pruebas de unidad, yo lo hice, y supongo que fue por el nombre del framework: JUnit. Creo que la parte de Unit es la que nos indica que el framework hace pruebas de "unidad", y las hace realmente, solamente que nuestra concepción debe ampliarse más para entender los preceptos de la terminología. Para ayudarme a explicar esto voy a escribir mi concepción(muy simple) de tres términos:

<ul>
  <li>Pruebas de unidad: Son del tipo que determinan si un componente funciona de cierta forma esperada, bajos ciertos escenarios controlados y bien definidos, y excluyen el buen o mal funcionamiento de los elementos que colaboran con el mismo, pues no es de la incumbencia del elemento bajo ejecución, pero deben indicarnos que los mensajes a dichos elementos ajenos y externos deben ser enviados.(Aquí recomiendo una referencia al libro de Pragmatic Programmer capítulos 21, 23 y 34).</li>
  <li>Pruebas de integración: Son aquellas que determinan que un componente se ejecuta correctamente incluyendo las interacciones con sus componentes asociados, los cuales también deben estar listos para ser invocados.</li>
  <li>Pruebas de sistema: Son aquellas que ayudan a determinar si un flujo de negocio se ejecuto de manera correcta dadas ciertas condiciones y ambientes, y por lo general incluyen a varios componentes en su estructura.</li>
</ul>

Siendo esto muy breve, quiero enfocarme en las primeras, las pruebas de unidad, y me voy a basar en un ejemplo, dado que tengo la siguiente prueba:

<code>
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
</code>

El código que lo resuelve es muy sencillo:

<code>
package com.makingdevs;

public class Calculadora {
  public Integer suma(Integer a, Integer b){
    return a+b;
  }
}
</code>

Como ven, no hay necesidad de llamar a nada más que un método sencillo de un objeto. Hasta aquí nada complicado, pero, <b>¿que pasa si deseamos hacer uso de un caché?</b>, y esto va con el afán de que ustedes puedan extrapolar este conocimiento a sus componentes de aplicaciones que estén desarrollando y probando, especialmente servicios de negocio y controladores de frameworks MVC.

Supongase que ahora tenemos la siguiente prueba:

<code>
  @Test
  public void pruebaSumaDeDosNumerosGuardandoEnCache(){
    Calculadora calculadora = new Calculadora();
    Integer resultado = calculadora.suma(4,5);
    assertTrue(9 == resultado);
    // ¿Como comprobamos la llamada a un servicio de cache? 
  }
</code>

Siguiendo la inercia del desarrollo podremos hacer algo como lo siguiente:
<code>

  public Integer suma(Integer a, Integer b){
    // Pon aquí todo el código del caché
    return a+b;
  }
</code>

Y hacer mediciones de tiempo para determinar que el caché es más rapido que la operación per se, etc.; sin embargo, seguiremos sin conocer si estamos llamando a algún componente que me haga el caché. 

NOTA: Todos los elementos o componentes que están involucrados en el código que se esta probando de forma unitaria son conocidos como colaboradores.

Por lo tanto siguiendo un poco de diseño de software simple(Single Responsability), determinemos que necesitamos un colaborador que me permita guardar en caché la operación en cuestión. Y tenemos más de una prueba ahora, por lo tanto podemos reestructurar nuestra prueba de la siguiente manera:
<code>
  Calculadora calculadora = new Calculadora();

  @Test
  public void pruebaSumaDeDosNumeros(){
    Integer resultado = calculadora.suma(4,5);
    assertTrue(9 == resultado);
  }
  
  @Test
  public void pruebaSumaDeDosNumerosGuardandoEnCache(){
    Integer resultado = calculadora.suma(6,2);
    assertTrue(8 == resultado);
    // ¿Como comprobamos la llamada a un servicio de cache? 
  }
</code>

Introduzcamos a Mockito como nuestro bartender en esta prueba, e identifiquemos que la clase que esta en la lupa es Calculadora y que necesita un elemento colaborador de caché, el cual llamaremos CacheDeCalculadora, adicionalmente, necesitaremos usar algún método(que todavía no existe) de dicho colaborador que "ejecute" la persistencia en el caché.

Hagamos uso de Mockito en nuestra prueba:
<code>
package com.makingdevs;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnit44Runner;
import org.mockito.Mock;
import org.mockito.InjectMocks;

@RunWith(MockitoJUnit44Runner.class) // Nos ayudamos de este runner en JUnit para usar las anotaciones de Mockito
public class CalculadoraTests {

  @Mock CacheDeCalculadora cacheDeCalculadora; // Este es mi colaborador

  @InjectMocks Calculadora calculadora = new Calculadora(); // Clase principal a probar

  @Test
  public void pruebaSumaDeDosNumeros(){
    Integer resultado = calculadora.suma(4,5);
    assertTrue(9 == resultado);
  }
  
  @Test
  public void pruebaSumaDeDosNumerosGuardandoEnCache(){
    Integer resultado = calculadora.suma(6,2);
    assertTrue(8 == resultado);
    verify(cacheDeCalculadora).persistirSuma(6,2,8); // ¿En verdad se llamo a este método? 
  }
}
</code>

Si ejecutamos esta prueba, con dichos cambios, saltarán errores de compilación obvios, como el de no se encuentra dicha clase CacheDeCalculadora, y claro, por que no se ha creado. Simplemente agregando una interfaz y el método con dicha firma ejecutaré la prueba para dar pie a las bondades que me ofrece mockito.
<code>
package com.makingdevs;

public interface CacheDeCalculadora {
  void persistirSuma(Integer a, Integer b, Integer resultado);
}
</code>

Ahora bien, el error que tengo es el siguiente:

<i>
Wanted but not invoked:
cacheDeCalculadora.persistirSuma(6, 2, 8);
-> at com.makingdevs.CalculadoraTests.pruebaSumaDeDosNumerosGuardandoEnCache(CalculadoraTests.java:27)
Actually, there were zero interactions with this mock.
</i>

El cual me indica que deberé de llamar a un elemento colaborador en cierto método, ¿que debo hacer? declararlo y llamarlo.
<code>
package com.makingdevs;

public class Calculadora {

  CacheDeCalculadora cacheDeCalculadora;

  public Integer suma(Integer a, Integer b){
    cacheDeCalculadora.persistirSuma(a,b,a+b);
    return a+b;
  }
}
</code>

Una vez que ejecute esta prueba el resultado deberá ser satisfactorio.

Ahora bien, la lógica de como funciona la persistencia o bien en donde lo está haciendo de momento no me importa, pues sólo quiero corroborar que llame al método, eso es lo que me importa.

Para concluir con la lógica completa abarcaré un caso más, el cuál es entregar el resultado del caché en dado caso de que previamente se hubiese ejecutado, verificando que en dicho caso no persista la operación suma que estoy haciendo pues sería inútil y sin congruencia con la lógica que estoy queriendo hacer. Lo cual me quedaría algo así:

<code>
  @Test
  public void pruebaSumaDeDosNumerosObteniendoResultadoDeCache(){
    when(cacheDeCalculadora.existeElResultadoDeSumar(10,4)).thenReturn(true); // Simulamos el comportamiento de un método
    when(cacheDeCalculadora.obtenerElResultadoDeSumar(10,4)).thenReturn(14); // Aislamos el componente 
    Integer resultadoEnSegundaEjecucion = calculadora.suma(10,4);
    assertTrue(14 == resultadoEnSegundaEjecucion);
    verify(cacheDeCalculadora,never()).persistirSuma(10,4,14); // Este método no debe ejecutarse
    verify(cacheDeCalculadora).existeElResultadoDeSumar(10,4);
    verify(cacheDeCalculadora).obtenerElResultadoDeSumar(10,4);
  }
</code>

Obsérverse que hemos agregado comportamiento aislado y controlado del colaborador, y como nota extra estamos dando pie a los elementos que necesitamos agregar en nuestra implementación. Corriendo esto nos mandará los respectivo errores de compilación por que dichos métodos no existen. Sólo hay que agregarlos:

<code>
package com.makingdevs;

public interface CacheDeCalculadora {
  void persistirSuma(Integer a, Integer b, Integer r);
  boolean existeElResultadoDeSumar(Integer a, Integer b);
  Integer obtenerElResultadoDeSumar(Integer a, Integer b);
}
</code>

Una vez ejecutado obtenemos un resultado interesante:

<i>
Wanted but not invoked:
cacheDeCalculadora.existeElResultadoDeSumar(
    10,
    4
);
-> at com.makingdevs.CalculadoraTests.pruebaSumaDeDosNumerosObteniendoResultadoDeCache(CalculadoraTests.java:38)

However, there were other interactions with this mock:
-> at com.makingdevs.Calculadora.suma(Calculadora.java:8)>
</i>

Para hacer que esta prueba pase tendremos que hacer dicha validación, lo cual quedaría como lo siguiente:
<code>
  public Integer suma(Integer a, Integer b){
    Integer resultado = 0;
    if(cacheDeCalculadora.existeElResultadoDeSumar(a,b)){
      resultado = cacheDeCalculadora.obtenerElResultadoDeSumar(a,b);
    } else {
      resultado = a + b; 
      cacheDeCalculadora.persistirSuma(a,b,resultado);
    }
    return resultado;
  }
</code>

Ejecutando esta prueba habremos de tener todo corriendo de manera satisfactoria. Y aunque no es de importancia para este caso como es que se va a guardar el caché tengo la certeza de que dicho método en esta lógica serña llamado en los casos en los que no exista dicho componente.

Me gustaría que el lector pudiera extrapolar este funcionamiento con DAO's u otros métodos de servicios que colaboran en la lógica de algún componente importante de su aplicación, en donde tenemos que desarrollar la lógica más elaborada para resolver un problema, pasar por ciertas condicionantes o bien saber que ciertos componentes deberán ser invocados en cierta cantidad de veces, o bien, no deberían llamarse, como ha sido este caso.

Sin lugar a dudas, hay muchas más cosas que mockito puede hacer para ayudarnos a determinar los comportamientos que ciertos componentes de nuestra aplicación debeierán tener, sin embargo, les recomiendo le echen un ojo al sitio de Mockito y a la documentación. https://code.google.com/p/mockito/

Para mejor referencia les dejo el repositorio de código en mi cuenta de github para que lo descarguen y puedan probarlo por ustedes mismos. Las ejecuciones s epueden hacer usando gradle o maven. Y también les recomiendo un artículo escrito por Alfredo Chávez en el sitio de Artesano.de/software, muy recomendable: TDD Cómo y porqué: Una guía para los no iniciados http://artesanos.de/software/2012/02/13/tdd-como-y-porque-una-guia-para-los-no-iniciados/

Espero que les sirva tanto como a mi me sirvió.

Repositorio de Github: https://github.com/makingdevs/mockitoByExample 

Saludos a todos
