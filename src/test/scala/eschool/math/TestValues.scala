package eschool.math

import org.junit.Assert._
import org.junit.Test
import org.scalatest.junit.JUnitSuite


class TestValues extends JUnitSuite {
	@Test def values() {
		assert(MathValue("\\pi").get.toString === MathConstantPi.symbol)
		assertTrue(MathValue("x/2") == None)
		assertTrue(MathValue("5x") == None)
		assertTrue(MathValue("x8") == None)
		assertTrue(MathValue("y^{2}") == None)
		assertTrue(MathValue("4^{z}") == None)
		assertTrue(MathValue("a+3") == None)
		assertTrue(MathValue("a+b") == None)
		assertTrue(MathValue("3-c") == None)
		assertTrue(MathValue("a-b") == None)
		assertTrue(MathValue("x*6") == None)
		assertTrue(MathValue("x/y") == None)

		assert(MathValue("6").get === MathInteger(6))
		assert(MathValue("\\frac{5}{7}").get === MathFraction(MathInteger(5), MathInteger(7)))
		assert(MathValue("\\pi").get === MathConstantPi())
		assert(MathValue("e").get === MathConstantE())
		assert(MathValue("7+4i").get === MathComplexNumber(MathInteger(7), MathInteger(4)))
		assert(MathValue("x").get === MathVariable("x").get)
		assert(MathValue("4.56").get === MathDecimal(4.56))
		assert(MathValue("\\approx5").get === MathApproximateNumber(5))
		assert(MathValue("\\approx9").get === MathApproximateNumber(9))
	}
	@Test def integers() {
		val one: MathValue = MathValue("1").get
		assert(one.description === "MathInteger(1)")
		assert(one.toLaTeX === "1")
		val negative: MathValue = MathValue("-1").get
		assert(negative.description === "MathInteger(-1)")
		assert(negative.toLaTeX === "-1")
	}

	@Test def fractions() {
		val four: MathValue = MathValue("\\frac{4}{1}").get
		assert(four.description === "MathFraction(4, 1)")
		assert(four.toLaTeX === "\\frac{4}{1}")
		val oneHalf = MathValue("\\frac{1}{2}").get
		assert(oneHalf.description === "MathFraction(1, 2)")
		assert(oneHalf.toLaTeX === "\\frac{1}{2}")
		val bigFraction = MathValue("\\frac{4567890123456}{-1234567890123}").get
		assert(bigFraction.description === "MathFraction(BigInt(4567890123456), BigInt(-1234567890123))")
		assert(bigFraction.toLaTeX === "\\frac{4567890123456}{-1234567890123}")
		val fractionLaTeX = MathValue("\\frac{2}{3}").get
		assert(fractionLaTeX.description === "MathFraction(2, 3)")
		assert(fractionLaTeX.toLaTeX === "\\frac{2}{3}")
	}

	@Test def terminatingDecimals() {
		val one = MathValue("1.0").get
		assert(one.description === "MathDecimal(\"1.0\")")
		assert(one.toLaTeX === "1.0")
		val small = MathValue("1E-24").get
		assert(small.description === "MathDecimal(\"1E-24\")")
		assert(small.toLaTeX === "1*10^{-24}")
		val big = MathValue("2.04E+3").get
		assert(big.description === "MathDecimal(\"2.04E+3\")")
		assert(big.toLaTeX === "2.04*10^{3}")
	}

	@Test def approximations() {
		val one = MathValue("\\approx1").get
		assert(one.description === "MathApproximateNumber(1)")
		assert(one.toLaTeX === "\\approx1")
		val oneHalf = MathValue("\\approx0.5").get
		assert(oneHalf.description === "MathApproximateNumber(0.5)")
		assert(oneHalf.toLaTeX === "\\approx0.5")
	}

	@Test def complex() {
		val oneI = MathExpression("0+1i").get
		assert(oneI.description === "MathComplexNumber(MathInteger(0), MathInteger(1))")
		assert(oneI.toLaTeX === "0+i")
		val approx = MathExpression("\\approx(\\frac{3}{5}-\\frac{2}{3}i)").get
		assert(approx.description === "MathComplexNumber(MathApproximateNumber(0.6), MathApproximateNumber(-0.6666666666666666))")
		assert(approx.toLaTeX === "\\approx(0.6-0.6666666666666666i)")
		val expts = MathExpression("2E+3-4E-3i").get
		assert(expts.description === "MathComplexNumber(MathDecimal(\"2E+3\"), MathDecimal(\"-0.004\"))")
		assert(expts.toLaTeX === "2*10^{3}-0.004i")
		val withParens = MathExpression("(34+6i)").get
		assert(withParens.description === "MathComplexNumber(MathInteger(34), MathInteger(6))")
		assert(withParens.toLaTeX === "34+6i")
		val neg = MathExpression("34-6i").get
		assert(neg.description === "MathComplexNumber(MathInteger(34), MathInteger(-6))")
		assert(neg.toLaTeX === "34-6i")
		val variable = MathComplexNumber("radius")
		assertTrue(variable == None)
		val mess = MathComplexNumber("xyz+4i")
		assertTrue(mess == None)
		val xVar = MathComplexNumber("x")
		assertTrue(xVar == None)
	}

	@Test def constantE() {
		val etest1 = MathConstantE()
		assert(etest1.description === "MathConstantE")
		assert(etest1.toLaTeX === "e")
		val etest2 = MathConstant("e").get
		assert(etest2.description === "MathConstantE")
		assert(etest2.toLaTeX === "e")
		val etest3 = MathValue("e").get
		assert(etest3.description === "MathConstantE")
		assert(etest3.toLaTeX === "e")
	}

	@Test def constantPi() {
		val piTest1 = MathConstantPi()
		assert(piTest1.description === "MathConstantPi")
		assert(piTest1.toLaTeX === "\\pi")
		val piTest2 = MathConstant("\\pi").get
		assert(piTest2.description === "MathConstantPi")
		assert(piTest2.toLaTeX === "\\pi")
		val piTest3 = MathValue("\\pi").get
		assert(piTest3.description === "MathConstantPi")
		assert(piTest3.toLaTeX === "\\pi")
	}

	@Test def variables() {
		val xVar = MathVariable("x").get
		assert(xVar.description === "MathVariable(x)")
		assert(xVar.toLaTeX === "x")
		val xChar = MathVariable('x').get
		assert(xChar.description === "MathVariable(x)")
		assert(xChar.toLaTeX === "x")
		val xVar2 = MathValue("x").get
		assert(xVar2.description === "MathVariable(x)")
		assert(xVar2.toLaTeX === "x")
		val xChar2 = MathValue('x').get
		assert(xChar2.description === "MathVariable(x)")
		assert(xChar2.toLaTeX === "x")
		val pi = MathVariable("pi")
		assertTrue(pi == None)
		val e = MathVariable("e")
		assertTrue(e == None)
	}
}