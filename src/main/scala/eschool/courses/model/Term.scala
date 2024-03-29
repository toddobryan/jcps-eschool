package eschool.courses.model

import javax.jdo.annotations._
import org.joda.time.{DateTime, LocalDate}
import bootstrap.liftweb.DataStore
import org.datanucleus.api.jdo.query._
import org.datanucleus.query.typesafe._
import javax.jdo.JDOHelper

@PersistenceCapable(detachable="true")
class Term {
  @PrimaryKey
  @Persistent(valueStrategy=IdGeneratorStrategy.INCREMENT)
  private[this] var _id: Long = _
  private[this] var _name: String = _
  private[this] var _year: AcademicYear = _
  @Persistent
  private[this] var _start: java.sql.Date = _
  @Persistent
  private[this] var _end: java.sql.Date = _
  
  def this(name: String, year: AcademicYear, start: LocalDate, end: LocalDate) = {
    this()
    _name = name
    _year = year
    start_=(start)
    end_=(end)
  }

  def id: Long = _id

  def name: String = _name
  def name_=(theName: String) { _name = theName }

  def year: AcademicYear = _year
  def year_=(theYear: AcademicYear) { _year = theYear }

  def start: LocalDate = new DateTime(_start).toLocalDate
  def start_=(theStart: LocalDate) { _start = new java.sql.Date(theStart.toDateTimeAtStartOfDay.toDate.getTime) }
  
  def end: LocalDate = new DateTime(_end).toLocalDate
  def end_=(theEnd: LocalDate) { _end = new java.sql.Date(theEnd.toDateTimeAtStartOfDay.toDate.getTime) }
}

object Term {
  lazy val current: Term = {
    val cand = QTerm.candidate
    val term = DataStore.pm.query[Term].filter(cand.name.eq("Fall 2011")).executeOption().get
    DataStore.pm.detachCopy(term)
  }
  
  /*def current: Term = {
    println(JDOHelper.getObjectState(_current))
    val cand = QTerm.candidate
    DataStore.pm.query[Term].filter(cand.name.eq("Fall 2011")).executeOption().get           
  }*/
}

trait QTerm extends PersistableExpression[Term] {
  private[this] lazy val _name: StringExpression = new StringExpressionImpl(this, "_name")
  def name: StringExpression = _name
  
  private[this] lazy val _year: ObjectExpression[AcademicYear] = new ObjectExpressionImpl[AcademicYear](this, "_year")
  def term: ObjectExpression[AcademicYear] = _year
  
  private[this] lazy val _start: DateExpression[java.util.Date] = new DateExpressionImpl[java.sql.Date](this, "_start")
  def start: DateExpression[java.util.Date] = _start

  private[this] lazy val _end: DateExpression[java.util.Date] = new DateExpressionImpl[java.sql.Date](this, "_end")
  def end: DateExpression[java.util.Date] = _end
}

object QTerm {
  def apply(parent: PersistableExpression[_], name: String, depth: Int): QTerm = {
    new PersistableExpressionImpl[Term](parent, name) with QTerm
  }
  
  def apply(cls: Class[Term], name: String, exprType: ExpressionType): QTerm = {
    new PersistableExpressionImpl[Term](cls, name, exprType) with QTerm
  }
  
  private[this] lazy val jdoCandidate: QTerm = candidate("this")
  
  def candidate(name: String): QTerm = QTerm(null, name, 5)
  
  def candidate(): QTerm = jdoCandidate
  
  def parameter(name: String): QTerm = QTerm(classOf[Term], name, ExpressionType.PARAMETER)
  
  def variable(name: String): QTerm = QTerm(classOf[Term], name, ExpressionType.VARIABLE)
}

