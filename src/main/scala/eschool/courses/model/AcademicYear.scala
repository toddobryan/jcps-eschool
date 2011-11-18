package eschool.courses.model

import javax.jdo.annotations._
import jdo.Id
import jdo.QId
import org.datanucleus.query.typesafe.StringExpression
import org.datanucleus.api.jdo.query.StringExpressionImpl
import org.datanucleus.query.typesafe.PersistableExpression
import org.datanucleus.api.jdo.query.PersistableExpressionImpl
import org.datanucleus.api.jdo.query.ExpressionType

class AcademicYear extends Id[Long] {
  @Unique
  @Column(allowsNull="false")
  private[this] var _name: String = _
  
  def this(name: String) = {
    this()
    _name = name
  }
  
  def name: String = _name
  def name_=(theName: String) { _name = theName }
}

trait QAcademicYear extends QId[Long, AcademicYear]  {
  private[this] lazy val _name: StringExpression = new StringExpressionImpl(this, "_name")
  def name: StringExpression = _name
}

object QAcademicYear {
  def apply(parent: PersistableExpression[_], name: String, depth: Int): QAcademicYear = {
    new PersistableExpressionImpl[AcademicYear](parent, name) with QId[Long, AcademicYear] with QAcademicYear
  }
  
  def apply(cls: Class[AcademicYear], name: String, exprType: ExpressionType): QAcademicYear = {
    new PersistableExpressionImpl[AcademicYear](cls, name, exprType) with QId[Long, AcademicYear] with QAcademicYear
  }
  
  private[this] lazy val jdoCandidate: QAcademicYear = candidate("this")
  
  def candidate(name: String): QAcademicYear = QAcademicYear(null, name, 5)
  
  def candidate(): QAcademicYear = jdoCandidate
  
  def parameter(name: String): QAcademicYear = QAcademicYear(classOf[AcademicYear], name, ExpressionType.PARAMETER)
  
  def variable(name: String): QAcademicYear = QAcademicYear(classOf[AcademicYear], name, ExpressionType.VARIABLE)
}