(require '[clojure.test :refer [is deftest run-tests]])

(load-file "../src/basic.clj")

(deftest test-variable-float?
	(is (= (variable-float? 'X%) false))
	(is (= (variable-float? 'X) true))
	(is (= (variable-float? 'X$) false))
)

(deftest test-variable-integer?
	(is (= (variable-integer? 'X%) true))
	(is (= (variable-integer? 'X) false))
	(is (= (variable-integer? 'X$) false))
)

(deftest test-variable-string?
	(is (= (variable-string? 'X%) false))
	(is (= (variable-string? 'X) false))
	(is (= (variable-string? 'X$) true))
)

(deftest test-expandir-nexts
	(is 
		(= 
			(expandir-nexts 
				(list 
					'(PRINT 1)
					(list 
						(quote NEXT)
						(quote A)
						(symbol ",")
						(quote B)
						(symbol ",")
						(quote C)
					)
				)
			) 
			'((PRINT 1) (NEXT A) (NEXT B) (NEXT C))
		)
	)
	(is (= (expandir-nexts (list '(PRINT 1)(list (quote NEXT) (quote A)))) 
		'((PRINT 1) (NEXT A)))
	)
	(is (= (expandir-nexts (list '(PRINT 1)(list (quote NEXT))))
		'((PRINT 1) (NEXT)))
	)
)

(deftest test-operador?
	(is (= (operador? '+) true))
	(is (= (operador? '%) false))
)

(deftest test-eliminar-cero-entero
	(is (= (eliminar-cero-entero nil) nil))
	(is (= (eliminar-cero-entero 'A) "A"))
	(is (= (eliminar-cero-entero 0) "0"))
	(is (= (eliminar-cero-entero 1.5) "1.5"))
	(is (= (eliminar-cero-entero 1) "1"))
	(is (= (eliminar-cero-entero -1) "-1"))
	(is (= (eliminar-cero-entero -1.5) "-1.5"))
	(is (= (eliminar-cero-entero 0.5) ".5"))
	(is (= (eliminar-cero-entero -0.5) "-.5"))
	(is (= (eliminar-cero-entero -10.5) "-10.5"))
	(is (= (eliminar-cero-entero 3020.501) "3020.501"))
)

(deftest test-eliminar-cero-decimal
	(is (= (eliminar-cero-decimal 1.5) 1.5))
	(is (= (eliminar-cero-decimal 1.50) 1.5))
	(is (= (eliminar-cero-decimal 1.0) 1))
	(is (= (eliminar-cero-decimal 'A) 'A))
	(is (= (eliminar-cero-decimal 100) 100))
)

(deftest test-preprocesar-expresion
	(is (= 
		(preprocesar-expresion '(X$ + " MUNDO" + Z$) ['((10 (PRINT X))) [10 1] [] [] [] 0 '{X$ "HOLA"}])
		'("HOLA" + " MUNDO" + "")))
	(is (= 
		(preprocesar-expresion '(X + . / Y% * Z) ['((10 (PRINT X))) [10 1] [] [] [] 0 '{X 5 Y% 2}])
		'(5 + 0 / 2 * 0)))
)

(deftest test-anular-invalidos
	(is (= (anular-invalidos '(IF X & * Y < 12 THEN LET ! X = 0)) '(IF X nil * Y < 12 THEN LET nil X = 0)))
)

(deftest test-palabra-reservada?
	(is (= (palabra-reservada? 'THEN) true))
	(is (= (palabra-reservada? 'SPACE) false))
	(is (= (palabra-reservada? 'REM) true))
)

(deftest test-extraer-data
	(is (= 
			(extraer-data (list '(10 (PRINT X) (REM ESTE NO) (DATA 30)) '(20 (DATA HOLA)) (list 100 (list 'DATA 'MUNDO (symbol ",") 10 (symbol ",") 20))))
			'("HOLA" "MUNDO" 10 20)
		)
	)
	(is (= 
			(extraer-data (list '(10 (PRINT X) (REM ESTE NO) (DATA 30)) '(20 (DATA HOLA)) (list 100 (list 'DATA 'MUNDO (symbol ",") 10 (symbol ",") 20))))
			'("HOLA" "MUNDO" 10 20)
		)
	)
	(is (= 
		(extraer-data '(()))
		'()
	))
)

(deftest test-contar-sentencias
	(let [n [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [10 1] [] [] [] 0 {}]]
		(is (= (contar-sentencias 10 n) 2))
		(is (= (contar-sentencias 15 n) 1))
		(is (= (contar-sentencias 20 n) 2))
	)
)

(deftest test-aridad
	(is (= (aridad 'MID$) 2))
	(is (= (aridad 'MID3$) 3))
	(is (= (aridad '+) 2))
	(is (= (aridad '*) 2))
	(is (= (aridad 'THEN) 0))
	(is (= (aridad 'SIN) 1))
	(is (= (aridad 'ATN) 1))
)


(deftest test-cargar-linea
	(is (= 
		(cargar-linea '(10 (PRINT X)) [() [:ejecucion-inmediata 0] [] [] [] 0 {}])
		'[((10 (PRINT X))) [:ejecucion-inmediata 0] [] [] [] 0 {}]))
	(is (= 
		(cargar-linea '(20 (X = 100)) ['((10 (PRINT X))) [:ejecucion-inmediata 0] [] [] [] 0 {}])
		'[((10 (PRINT X)) (20 (X = 100))) [:ejecucion-inmediata 0] [] [] [] 0 {}]))
	(is (= 
		(cargar-linea '(15 (X = X + 1)) ['((10 (PRINT X)) (20 (X = 100))) [:ejecucion-inmediata 0] [] [] [] 0 {}])
		'[((10 (PRINT X)) (15 (X = X + 1)) (20 (X = 100))) [:ejecucion-inmediata 0] [] [] [] 0 {}]))
	(is (= 
		(cargar-linea '(15 (X = X - 1)) ['((10 (PRINT X)) (15 (X = X + 1)) (20 (X = 100))) [:ejecucion-inmediata 0] [] [] [] 0 {}])
		'[((10 (PRINT X)) (15 (X = X - 1)) (20 (X = 100))) [:ejecucion-inmediata 0] [] [] [] 0 {}]))
)


(deftest test-ejecutar-asignacion
	(is (=
		(ejecutar-asignacion '(X = 5) ['((10 (PRINT X))) [10 1] [] [] [] 0 {}])
		'[((10 (PRINT X))) [10 1] [] [] [] 0 {X 5}]))
	(is (=
		(ejecutar-asignacion '(X = 5) ['((10 (PRINT X))) [10 1] [] [] [] 0 '{X 2}])
		'[((10 (PRINT X))) [10 1] [] [] [] 0 {X 5}]))
	(is (=
		(ejecutar-asignacion '(X = X + 1) ['((10 (PRINT X))) [10 1] [] [] [] 0 '{X 2}])
		'[((10 (PRINT X))) [10 1] [] [] [] 0 {X 3}]))
	(is (=
		(ejecutar-asignacion '(X$ = X$ + " MUNDO") ['((10 (PRINT X))) [10 1] [] [] [] 0 '{X$ "HOLA"}])
		'[((10 (PRINT X))) [10 1] [] [] [] 0 {X$ "HOLA MUNDO"}]))
)

(deftest test-es-variable?
	(is (=
		(es-variable? 'X%)
		true))
	(is (=
		(es-variable? 'X$)
		true))
	(is (=
		(es-variable? 'X)
		true))
	(is (=
		(es-variable? (symbol "\"HOLA MUNDO\""))
		false))
	(is (=
		(es-variable? 'ASDX%)
		true))
	(is (=
		(es-variable? '1.0)
		false))
)

(deftest test-desambiguar
	(is (=
		(desambiguar (list '- 2 '* (symbol "(") '- 3 '+ 5 '- (symbol "(") '+ 2 '/ 7 (symbol ")") (symbol ")")))
		(list '-u 2 '* (symbol "(") '-u 3 '+ 5 '- (symbol "(") '2 '/ 7 (symbol ")") (symbol ")"))))
	(is (=
		(desambiguar (list 'MID$ (symbol "(") 1 (symbol ",") 2 (symbol ")")))
		(list 'MID$ (symbol "(") 1 (symbol ",") 2 (symbol ")"))))
	(is (=
		(desambiguar (list 'MID$ (symbol "(") 1 (symbol ",") 2 (symbol ",") 3 (symbol ")")))
		(list 'MID3$ (symbol "(") 1 (symbol ",") 2 (symbol ",") 3 (symbol ")"))))
	(is (=
		(desambiguar (list 'MID$ (symbol "(") 1 (symbol ",") '- 2 '+ 'K (symbol ",") 3 (symbol ")")))
		(list 'MID3$ (symbol "(") 1 (symbol ",") '-u 2 '+ 'K (symbol ",") 3 (symbol ")"))))
)

(deftest test-precedencia
	(is (=
		(precedencia 'OR)
		1))
	(is (=
		(precedencia 'AND)
		2))
	(is (=
		(precedencia '*)
		6))
	(is (=
		(precedencia '-u)
		7))
	(is (=
		(precedencia 'MID$)
		9))
)

(deftest test-dar-error
	(with-out-str 
		(is (=
			(dar-error 16 [:ejecucion-inmediata 4])
			nil))
	"?SYNTAX ERROR")
	(with-out-str 
		(is (=
			(dar-error "?ERROR DISK FULL" [:ejecucion-inmediata 4])
			nil))
	"?ERROR DISK FULL")
	(with-out-str 
		(is (=
			(dar-error 16 [100 3])
			nil))
	"?SYNTAX ERROR IN 100")
	(with-out-str 
		(is (=
			(dar-error "?ERROR DISK FULL" [100 3])
			nil))
	"?ERROR DISK FULL IN 100")
)


(deftest test-buscar-lineas-restantes
(is (=
	(buscar-lineas-restantes [() [:ejecucion-inmediata 0] [] [] [] 0 {}])
	nil))
(is (=
	(buscar-lineas-restantes ['((PRINT X) (PRINT Y)) [:ejecucion-inmediata 2] [] [] [] 0 {}])
	nil))
(is (=
	(buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [10 2] [] [] [] 0 {}])
	(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J)))))
(is (=
	(buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [10 1] [] [] [] 0 {}])
	(list '(10 (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J)))))
(is (=
	(buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [10 0] [] [] [] 0 {}])
	(list '(10) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J)))))
(is (=
	(buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [15 1] [] [] [] 0 {}])
	(list '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J)))))
(is (=
	(buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [15 0] [] [] [] 0 {}])
	(list '(15) (list 20 (list 'NEXT 'I (symbol ",") 'J)))))
(is (=
	(buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 3] [] [] [] 0 {}])
	'((20 (NEXT I) (NEXT J))) ))
(is (=
	(buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 2] [] [] [] 0 {}])
	'((20 (NEXT I) (NEXT J))) ))
(is (=
	(buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 1] [] [] [] 0 {}])
	'((20 (NEXT J))) ))
(is (=
	(buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 0] [] [] [] 0 {}])
	'((20)) ))
(is (=
	(buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 -1] [] [] [] 0 {}])
	'((20)) ))
(is (=
	(buscar-lineas-restantes [(list '(10 (PRINT X) (PRINT Y)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [25 0] [] [] [] 0 {}])
	nil))
)

(deftest test-continuar-linea
	(with-out-str 
		(is (=
			(continuar-linea [(list '(10 (PRINT X)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 3] [] [] [] 0 {}])
			[nil [(list '(10 (PRINT X)) '(15 (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 3] [] [] [] 0 {}]]))
		"?RETURN WITHOUT GOSUB ERROR IN 20")
	(is (=
		(continuar-linea [(list '(10 (PRINT X)) '(15 (GOSUB 100) (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [20 3] [[15 2]] [] [] 0 {}])
		[:omitir-restante [(list '(10 (PRINT X)) '(15 (GOSUB 100) (X = X + 1)) (list 20 (list 'NEXT 'I (symbol ",") 'J))) [15 1] [] [] [] 0 {}]]))
)

(run-tests)