# Intérprete de Applesoft Basic

En este trabajo práctico se pide desarrollar, en el lenguaje Clojure, un intérprete de Applesoft BASIC.

El intérprete a desarrollar debe ofrecer los dos modos de ejecución de Applesoft BASIC: ejecución inmediata y ejecución diferida.

Deberá estar basado en un REPL (read-eval-print-loop) que acepte, además de sentencias de Applesoft BASIC, dos comandos de Apple DOS 3.3 (LOAD y SAVE).

No será necesario utilizar espacios para separar los distintos símbolos del lenguaje.
Soportará tres tipos de datos:
* números enteros
* números de punto flotante
* cadenas de caracteres

## Ejemplos de programas en Applesoft BASIC:

### `SINE.BAS`:
```bas
100 PRINT "X","SIN(X)"
110 PRINT "---","------"
120 FOR I = 1 TO 19 : PRINT " "; : NEXT I
130 FOR A = 0 TO 8 * ATN(1) STEP 0.1
140 PRINT "*"
150 PRINT INT (A * 100) / 100, "   ";INT (SIN(A) * 100000) / 100000
160 FOR I = 1 TO 19 + SIN(A) * 20 : PRINT " "; : NEXT I,A
170 PRINT "*" : A = 8 * ATN(1)
180 PRINT INT (A * 100) / 100, "   ";INT (SIN(A) * 100000) / 100000
190 FOR I = 1 TO 19 : PRINT " "; : NEXT I : PRINT "*"
```

Se pueden encontrar mas ejemplos en la carpeta `samples`

### Ejecución:
```bash
clj

user=>(load-file "basic.clj")

user=>(driver-loop)

] LOAD SINE.BAS

] RUN
```