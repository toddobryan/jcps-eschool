package eschool.users.model

import javax.jdo.annotations._
import org.datanucleus.query.typesafe._
import org.datanucleus.api.jdo.query._

@PersistenceCapable(detachable="true")
@Inheritance(strategy=InheritanceStrategy.SUBCLASS_TABLE)
class Perspective {
  @PrimaryKey
  @Persistent(valueStrategy=IdGeneratorStrategy.INCREMENT)
  private[this] var _id: Long = _

  private[this] var _user: User = _
  
  protected def this(user: User) = {
    this()
    user_=(user)
  }
  
  def id: Long = _id

  def user: User = _user
  def user_=(theUser: User) { _user = theUser }
}

trait QPerspective[PC <: Perspective] extends PersistableExpression[PC] {
  private[this] lazy val _id: NumericExpression[Long] = new NumericExpressionImpl[Long](this, "_id")
  def id: NumericExpression[Long] = _id

  private[this] lazy val _user: ObjectExpression[User] = new ObjectExpressionImpl[User](this, "_user")
  def user: ObjectExpression[User] = _user 
}
