package kornell.server.util

trait Err extends Throwable
case class StringErr(e: String) extends Err
case object RequirementNotMet extends Err

abstract class Conditional[+T] {
  def isPassed: Boolean
  def get: T
  def err: Err
  def requiring(newcond: => Boolean, e: => Err = RequirementNotMet): Conditional[T]
  def or(newcond: => Boolean, e: => Err = RequirementNotMet): Conditional[T]
}

class Passed[T](expr: => T) extends Conditional[T] {
  def isPassed: Boolean = true
  def get: T = expr
  def err: Err = throw new IllegalStateException("cannot err passed")
  def requiring(newcond: => Boolean, err: => Err): Conditional[T] =
    if (newcond) this else new Failed(expr, err)

  def or(newcond: => Boolean, e: => Err): Conditional[T] =
    this
}

object Passed {
  def unapply[T](p: Passed[T]): Option[T] = Option(p.get)
}

class Failed[T](expr: => T, _err: => Err) extends Conditional[T] {
  def isPassed: Boolean = false
  def get: T = throw _err
  def err: Err = _err
  def requiring(newcond: => Boolean, e: => Err): Failed[T] = this
  def or(newcond: => Boolean, e: => Err): Conditional[T] =
    if (newcond) new Passed(expr) else this
}

object Failed {
  def unapply[T](f: Failed[T]): Option[Err] = Option(f.err)
}

object Conditional {
  implicit def toConditional[T](expr: => T): Passed[T] = Conditional(expr)
  implicit def toErr(s: String): StringErr = StringErr(s)
  def apply[T](expr: => T) = new Passed(expr)
}