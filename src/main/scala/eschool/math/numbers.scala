package eschool.math

import java.math.MathContext

abstract class MathNumber extends MathConstant {
  //def toMathMlPresentation(): NodeSeq
  //def toMathMlContent(): NodeSeq
}

object MathNumber {
  def apply(s: String): Option[MathNumber] = {
    MathRealNumber(s) orElse MathComplexNumber(s)
  }

  def stringToDecimal(s: String): Option[BigDecimal] = {
    try {
      Some(BigDecimal(s, MathContext.UNLIMITED))
    } catch {
      case e: NumberFormatException => None
    }
  }

  def stringToDouble(s: String): Option[Double] = {
    try {
      Some(s.toDouble)
    } catch {
      case e: NumberFormatException => None
    }
  }

  def stringToInt(num: String): Option[BigInt] = {
    if (numBeginsWithPlusSign(num)) {
      stringToInt(num.substring(1))
    }
    try {
      Some(BigInt(num))
    } catch {
      case e: NumberFormatException => None
    }
  }

  def numBeginsWithPlusSign(num: String): Boolean = (num.length != 0 && num.charAt(0) == '+')


  def intDescription(i: BigInt): String = {
    if (i < Integer.MIN_VALUE || i > Integer.MAX_VALUE) {
      "BigInt(%s)".format(i.toString)
    } else {
      "%d".format(i)
    }
  }
}

abstract class MathRealNumber extends MathNumber {
  override def getValue: BigDecimal = this.toApproximation.getValue

  def toApproximation: MathApproximateNumber
}

object MathRealNumber {
  def apply(s: String): Option[MathRealNumber] = {
    MathInteger(s) orElse MathFraction(s) orElse MathDecimal(s) orElse MathApproximateNumber(s)
  }
}

abstract class MathExactNumber extends MathRealNumber

class MathFraction(val numerator: BigInt, val denominator: BigInt) extends MathExactNumber {
  def getNumerator = numerator

  def getDenominator = denominator

  override def toApproximation: MathApproximateNumber = MathApproximateNumber(this.getNumerator.toDouble./(this.getDenominator.toDouble))

  private def formatString(s: String): String = {
    if (this.getDenominator == 1) {
      s.format(this.getNumerator.toString())
    } else {
      s.format(this.getNumerator.toString(), this.getDenominator.toString())
    }
  }

  override def toLaTeX: String = {
    if (this.getDenominator == 1) formatString("%s") else formatString("\\frac{%s}{%s}")
  }

  override def description: String = {
    "MathFraction(%s, %s)".format(MathNumber.intDescription(this.getNumerator), MathNumber.intDescription(this.getDenominator))
  }
}

object MathFraction {
  def apply(numerator: BigInt, denominator: BigInt) = new MathFraction(numerator, denominator)

  def apply(s: String): Option[MathFraction] = MathFraction.stringToRational(s)

  def stringToRational(s: String): Option[MathFraction] = {
    (MathFraction.extractNumeratorFromString(s), MathFraction.extractDenominatorFromString(s)) match {
      case (Some(numerator), Some(denominator)) => Some(MathFraction(numerator, denominator))
      case _ => None
    }
  }

  def extractNumeratorFromString(s: String): Option[BigInt] = {
    if (s.contains("/")) {
      MathNumber.stringToInt(s.substring(0, s.indexOf("/")))
    } else {
      None
    }
  }

  def extractDenominatorFromString(s: String): Option[BigInt] = {
    if (s.contains("/")) {
      MathNumber.stringToInt(s.substring(s.indexOf("/") + 1))
    } else {
      None
    }
  }
}

class MathInteger(anInt: BigInt) extends MathFraction(anInt, BigInt(1)) {
  def getInt = anInt

  override def getValue: BigDecimal = BigDecimal(anInt)

  override def description: String = "MathInteger(%s)".format(MathNumber.intDescription(anInt))
}

object MathInteger {
  def apply(anInt: BigInt): MathInteger = new MathInteger(anInt)

  def apply(s: String): Option[MathInteger] = {
    MathNumber.stringToInt(s) match {
      case Some(bigInt) => Some(MathInteger(bigInt))
      case _ => None
    }
  }
}


class MathDecimal(val value: BigDecimal) extends MathExactNumber {
  override def getValue: BigDecimal = value

  def toApproximation: MathApproximateNumber = MathApproximateNumber(this.getValue.toDouble)

  def toLaTeX: String = this.getValue.toString

  def description: String = "MathDecimal(\"%s\")".format(this.getValue.toString)
}

object MathDecimal {
  def apply(bigDecimal: BigDecimal) = new MathDecimal(bigDecimal)

  def apply(s: String): Option[MathDecimal] = {
    MathNumber.stringToDecimal(s).map(MathDecimal(_))
  }
}


class MathApproximateNumber(val value: BigDecimal) extends MathRealNumber {
  override def getValue: BigDecimal = value

  def toApproximation: MathApproximateNumber = this

  override def toLaTeX: String = "\\approx " + this.getValue.toString

  def description: String = "MathApproximateNumber(%s)".format(this.getValue.toString)
}

object MathApproximateNumber {
  val symbol: String = "\u2248"

  def apply(d: BigDecimal) = new MathApproximateNumber(d)

  def apply(s: String): Option[MathApproximateNumber] = {
    if (s.startsWith(MathApproximateNumber.symbol)) MathRealNumber(s.substring(1)).map(_.toApproximation)
    else None
  }
}

class MathComplexNumber(val real: MathRealNumber, val imag: MathRealNumber) extends MathNumber {
  // if either real or imag are approximate, both are coerced to approximate
  def getReal: MathRealNumber = real

  def getImaginary: MathRealNumber = imag

  override def getValue: BigDecimal = getReal.getValue

  def isApproximation: Boolean = real.isInstanceOf[MathApproximateNumber] || imag.isInstanceOf[MathApproximateNumber]

  private def realToLaTeX: String = {
    if (isApproximation) {
      this.getReal.toApproximation.getValue.toString
    } else {
      this.getReal.toString
    }
  }

  private def imaginaryToLaTeX: String = {
    if (isApproximation) {
      this.getImaginary.toApproximation.getValue.toString
    } else {
      this.getImaginary.toString
    }
  }

  private def complexString: String = {
    realToLaTeX + getOperand + imaginaryToLaTeX + "i"
  }

  private def getOperand: String = if (imaginaryToLaTeX.startsWith("-")) "" else "+"

  def toLaTeX: String = {
    if (isApproximation) "\\approx(%s)".format(complexString) else complexString
  }

  def description: String = "MathComplexNumber(%s, %s)".format(getReal.description, getImaginary.description)
}

object MathComplexNumber {
  def apply(real: MathRealNumber, imaginary: MathRealNumber): MathComplexNumber = {
    if (real.isInstanceOf[MathApproximateNumber] || imaginary.isInstanceOf[MathApproximateNumber]) {
      new MathComplexNumber(real.toApproximation, imaginary.toApproximation)
    } else {
      new MathComplexNumber(real, imaginary)
    }
  }

  def apply(s: String): Option[MathComplexNumber] = {
    val complexString = new ComplexNumberString(s)
    complexString.toMathComplexNumber
  }
}

class ComplexNumberString(val s: String) {
  override def toString: String = s

  def toMathComplexNumber: Option[MathComplexNumber] = {
    if (this.isAComplexNumber) {
      this.extractComplexNumber
    } else {
      None
    }
  }

  def extractComplexNumber: Option[MathComplexNumber] = {
    (MathRealNumber(this.getRealPart), MathRealNumber(this.getImaginaryPart)) match {
      case (Some(real), Some(imaginary)) => Some(MathComplexNumber(real, imaginary))
      case _ => None
    }
  }

  def hasNoRealPart: Boolean = {
    getIndexOfOperand(this.toString) <= 0
  }

  def isAComplexNumber: Boolean = {
    this.toString.contains("i")
  }

  def isApproximation: Boolean = this.toString.startsWith(MathApproximateNumber.symbol)

  def getRealPart: String = {
    if (this.hasNoRealPart) "0"
    else {
      val basicString: String = this.withoutTrivialParts
      basicString.substring(0, getIndexOfOperand(basicString))
    }
  }

  def getImaginaryPart: String = {
    val basicString: String = this.withoutTrivialParts
    if (this.hasNoRealPart) {
      basicString
    } else {
      basicString.substring(this.imaginaryNumberIndex)
    }
  }

  def withoutTrivialParts: String = {
    val stringWithoutParenthesis: ComplexNumberString = this.withoutParenthesis
    stringWithoutParenthesis.removeI
  }

  def withoutParenthesis: ComplexNumberString = {
    val parenRegex = """[\(\)]""".r
    new ComplexNumberString(parenRegex.replaceAllIn(this.toString, ""))
  }

  def removeI: String = {
    val iRegex = """i""".r
    iRegex.replaceAllIn(this.toString, "")
  }

  def hasParenthesis: Boolean = {
    val str = this.toString
    if (this.isApproximation) {
      str.substring(1, 2).equals("(") && str.substring(s.length - 1).equals(")")
    } else {
      str.substring(0, 1).equals("(") && str.substring(s.length - 1).equals(")")
    }
  }

  def imaginaryNumberIndex: Int = {
    val basicString = this.withoutTrivialParts
    val indexOfOperand = getIndexOfOperand(basicString)
    if (basicString.charAt(indexOfOperand) == '+') {
      indexOfOperand + 1
    } else {
      indexOfOperand
    }
  }

  def getIndexOfOperand(str: String): Int = {
    val range = (str.length - 1).to(0, -1)
    range.find((i: Int) =>
      (str.charAt(i) == '+' || str.charAt(i) == '-' &&
        (i == 0 || str.charAt(i - 1) != 'E'))).getOrElse(-1)
  }
}