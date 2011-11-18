package eschool.courses.model

import javax.jdo.annotations._
import jdo.Id
import jdo.QId
import org.datanucleus.api.jdo.query._
import org.datanucleus.query.typesafe._

class Period extends Id[Long] {
  private[this] var _name: String = _
  private[this] var _order: Int = _
  
  def this(name: String, order: Int) = {
    this()
    _name = name
    _order = order
  }

  def name: String = _name
  def name_=(theName: String) { _name = theName }
  
  def order: Int = _order
  def order_=(theOrder: Int) { _order = theOrder }
}

trait QPeriod extends QId[Long, Period] {
  private[this] lazy val _name: StringExpression = new StringExpressionImpl(this, "_name")
  def name: StringExpression = _name
  
  private[this] lazy val _order: NumericExpression[Int] = new NumericExpressionImpl[Int](this, "_name")
  def order: NumericExpression[Int] = _order
}

object QPeriod {
  def apply(parent: PersistableExpression[_], name: String, depth: Int): QPeriod = {
    new PersistableExpressionImpl[Period](parent, name) with QId[Long, Period] with QPeriod
  }
  
  def apply(cls: Class[Period], name: String, exprType: ExpressionType): QPeriod = {
    new PersistableExpressionImpl[Period](cls, name, exprType) with QId[Long, Period] with QPeriod
  }
  
  private[this] lazy val jdoCandidate: QPeriod = candidate("this")
  
  def candidate(name: String): QPeriod = QPeriod(null, name, 5)
  
  def candidate(): QPeriod = jdoCandidate
  
  def parameter(name: String): QPeriod = QPeriod(classOf[Period], name, ExpressionType.PARAMETER)
  
  def variable(name: String): QPeriod = QPeriod(classOf[Period], name, ExpressionType.VARIABLE)
}