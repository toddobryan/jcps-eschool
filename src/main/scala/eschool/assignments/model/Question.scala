package eschool.assignments.model

import javax.jdo.annotations._
import scala.xml.NodeSeq
import eschool.utils.Helpers.string2nodeSeq
import org.datanucleus.query.typesafe._
import org.datanucleus.api.jdo.query._

@PersistenceCapable(detachable="true")
class Question {
  @PrimaryKey
  @Persistent(valueStrategy=IdGeneratorStrategy.INCREMENT)
  private[this] var _id: Long = _
  
  @Column(allowsNull="false")
  private[this] var _subject : Subject = _
  @Column(allowsNull="false")
  private[this] var _source: Source = _
  private[this] var _number: String = _
  private[this] var _content: String = _
  
  def id: Long = _id
  
  def subject: Subject = _subject
  def subject_=(theSubject: Subject) { _subject = theSubject }
  
  def source: Source = _source
  def source_=(theSource: Source) { _source = theSource }
  
  def number: String = _number
  def number_=(theNumber: String) { _number = theNumber }
  
  def content: NodeSeq = string2nodeSeq(_content)
  def content_=(html: NodeSeq) { _content = html.toString }
  def content_=(htmlString: String) { content_=(string2nodeSeq(htmlString)) }
}

trait QQuestion extends PersistableExpression[Question] {
  private[this] lazy val _id: NumericExpression[Long] = new NumericExpressionImpl[Long](this, "_id")
  def id: NumericExpression[Long] = _id

  private[this] lazy val _subject: ObjectExpression[Subject] = new ObjectExpressionImpl[Subject](this, "_subject")
  def subject: ObjectExpression[Subject] = _subject
  
  private[this] lazy val _source: ObjectExpression[Source] = new ObjectExpressionImpl[Source](this, "_source")
  def source: ObjectExpression[Source] = _source
  
  private[this] lazy val _number: StringExpression = new StringExpressionImpl(this, "_number")
  def number: StringExpression = _number
  
  private[this] lazy val _content: StringExpression = new StringExpressionImpl(this, "_content")
  def content: StringExpression = _content
}

object QQuestion {
  def apply(parent: PersistableExpression[_], name: String, depth: Int): QQuestion = {
    new PersistableExpressionImpl[Question](parent, name) with QQuestion
  }
  
  def apply(cls: Class[Question], name: String, exprType: ExpressionType): QQuestion = {
    new PersistableExpressionImpl[Question](cls, name, exprType) with QQuestion
  }
  
  private[this] lazy val jdoCandidate: QQuestion = candidate("this")
  
  def candidate(name: String): QQuestion = QQuestion(null, name, 5)
  
  def candidate(): QQuestion = jdoCandidate
  
  def parameter(name: String): QQuestion = QQuestion(classOf[Question], name, ExpressionType.PARAMETER)
  
  def variable(name: String): QQuestion = QQuestion(classOf[Question], name, ExpressionType.VARIABLE)
}