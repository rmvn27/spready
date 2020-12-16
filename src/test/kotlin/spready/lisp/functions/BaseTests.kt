package spready.lisp.functions

import org.junit.jupiter.api.Nested
import spready.lisp.Cons
import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.Func
import spready.lisp.Nil
import spready.lisp.Num
import spready.lisp.Symbol
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BaseTests {

    private var env = Environment()

    @BeforeTest
    fun resetEnv() {
        env = Environment()
    }

    @Test
    fun `registerSExpr normal`() {
        val expected = Num(123)
        val sym = Symbol("123")
        val expr = Symbol("456")
        env[expr] = expected

        assertEquals(expected, registerSExpr(sym, expr, env))
        assertEquals(expected, env[sym])
    }

    @Test
    fun `toListCheckSize normal`() {
        val input = Cons(Num(1), Cons(Num(2), Nil))
        val expected = listOf(Num(1), Num(2))

        assertEquals(expected, input.toListCheckSize(2))
    }

    @Test
    fun `toListCheckSize fail`() {
        val input = Cons(Num(1), Cons(Num(2), Nil))

        assertFailsWith<EvalException> {
            input.toListCheckSize(5)
        }
    }

    @Test
    fun `createLambda overrideVal`() {
        val lambda = createLambda("test", listOf(Symbol("x")), Symbol("x"))

        env[Symbol("x")] = Num(3)

        assertEquals(Num(5), lambda(env, Cons(Num(5), Nil)))
    }

    @Test
    fun `createLambda empty args`() {
        val lambda = createLambda("test", listOf(), Symbol("x"))

        env[Symbol("x")] = Num(3)

        assertEquals(Num(3), lambda(env, Cons(Num(3), Nil)))
    }

    @Nested
    inner class LambdaTest {
        @Test
        fun `Lambda normal`() {
            val lambdaFunc =
                Lambda(
                    env,
                    Cons(Cons(Symbol("x"), Nil), Cons(Symbol("x"), Nil))
                ) as Func

            assertEquals(Num(3), lambdaFunc(env, Cons(Num(3), Nil)))
        }

        @Test
        fun `Lambda Empty args`() {
            val lambdaFunc =
                Lambda(env, Cons(Nil, Cons(Num(3), Nil))) as Func

            assertEquals(Num(3), lambdaFunc(env, Cons(Nil, Nil)))
        }

        @Test
        fun `Lambda fail head with other than Symbol`() {
            assertFailsWith<EvalException> {
                Lambda(
                    env,
                    Cons(
                        Cons(Symbol("x"), Cons(Num(4), Nil)),
                        Cons(Symbol("x"), Nil)
                    )
                )
            }
        }
    }

    @Nested
    inner class ValEvalTest {
        @Test
        fun `ValEval normal`() {
            env[Symbol("y")] = Symbol("x")

            assertEquals(Num(3), ValEval(env, Cons(Symbol("y"), Cons(Num(3), Nil))))
            assertEquals(Num(3), env[Symbol("x")])
        }

        @Test
        fun `ValEval fail`() {
            assertFailsWith<EvalException> {
                ValEval(env, Cons(Symbol("y"), Cons(Num(3), Nil)))
            }
        }
    }

    @Nested
    inner class ValTest {
        @Test
        fun `Val normal`() {
            assertEquals(Num(3), Val(env, Cons(Symbol("y"), Cons(Num(3), Nil))))
            assertEquals(Num(3), env[Symbol("y")])
        }

        @Test
        fun `Val fail`() {
            // TODO: Check message
            assertFailsWith<EvalException> {
                Val(env, Cons(Num(3), Cons(Num(3), Nil)))
            }
        }
    }

    @Nested
    inner class FunTest {
        @Test
        fun `FuncExpr normal`() {
            val funcSymbol = FunExpr(
                env,
                Cons(
                    Symbol("test"),
                    Cons(Cons(Symbol("x"), Nil), Cons(Symbol("x"), Nil))
                )
            )

            assertEquals(Symbol("test"), funcSymbol)
            assertEquals(Num(3), env.eval(Cons(Symbol("test"), Cons(Num(3), Nil))))
        }

        @Test
        fun `FuncExpr empty args`() {
            val funcSymbol = FunExpr(
                env,
                Cons(
                    Symbol("test"),
                    Cons(Nil, Cons(Num(3), Nil))
                )
            )

            assertEquals(Symbol("test"), funcSymbol)
            assertEquals(Num(3), env.eval(Cons(Symbol("test"), Cons(Nil, Nil))))
        }

        @Test
        fun `FuncExpr fail not Symbol`() {
            assertFailsWith<EvalException> {
                FunExpr(
                    env,
                    Cons(
                        Num(3),
                        Cons(Nil, Cons(Num(3), Nil))
                    )
                )
            }
        }
    }
}
