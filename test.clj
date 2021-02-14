(require '[clojure.test :refer [is deftest run-tests]])

(load-file "basic.clj")

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
	(is (= (preprocesar-expresion '(X$ + " MUNDO" + Z$) ['((10 (PRINT X))) [10 1] [] [] [] 0 '{X$ "HOLA"}]) '("HOLA" + " MUNDO" + "")))
	(is (= (preprocesar-expresion '(X + . / Y% * Z) ['((10 (PRINT X))) [10 1] [] [] [] 0 '{X 5 Y% 2}]) '(5 + 0 / 2 * 0)))
)

(deftest test-anular-invalidos
	(is (= (anular-invalidos '(IF X & * Y < 12 THEN LET ! X = 0)) '(IF X nil * Y < 12 THEN LET nil X = 0)))
)

(deftest test-palabra-reservada?
	(is (= (palabra-reservada? 'THEN) true))
	(is (= (palabra-reservada? 'SPACE) false))
	(is (= (palabra-reservada? 'REM) true))
)

(run-tests)