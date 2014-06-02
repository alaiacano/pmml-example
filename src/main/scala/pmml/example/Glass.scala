package pmml.example
import org.dmg.pmml.FieldName

object Glass {

  val delim = "\t"

  def fromTextLine(line: String): Glass = {
    line.split(delim).toList match {
      case ri :: na :: mg :: al :: si :: k :: ca :: ba :: fe :: typ :: id :: nil =>
        Glass(id.toInt, ri.toDouble, na.toDouble, mg.toDouble, al.toDouble, si.toDouble, k.toDouble, ca.toDouble, ba.toDouble, fe.toDouble)

      case ri :: na :: mg :: al :: si :: k :: ca :: ba :: fe :: typ :: nil =>
        Glass(-1, ri.toDouble, na.toDouble, mg.toDouble, al.toDouble, si.toDouble, k.toDouble, ca.toDouble, ba.toDouble, fe.toDouble)
    }
  }
}

case class Glass(id: Int, ri: Double, na: Double, mg: Double, al: Double, si: Double,
            k: Double, ca: Double, ba: Double, fe: Double)

object GlassType {
  type PredictType = String
  // These are case-sensitive and must be the same as the column names in your R model.
  val ClassifyFields = Seq("RI", "Na", "Mg", "Al", "Si", "K", "Ca", "Ba", "Fe").map(i=>new FieldName(i))
  val PredictField = new FieldName("Type")

  def fromGlass(g: Glass) = GlassType(g.id, g.ri, g.na, g.mg, g.al, g.si, g.k, g.ca, g.ba, g.fe)
}

/**
 * svm(Type ~ ., data = Glass, cost = 100, gamma = 1)
 */
case class GlassType(id: Int, ri: Double, na: Double, mg: Double, al: Double, si: Double,
                     k: Double, ca: Double, ba: Double, fe: Double)
           extends Predictable[GlassType.PredictType]{
  override val ClassifyFields = GlassType.ClassifyFields
  override val PredictField = GlassType.PredictField
  override val rowValues = Seq[Double](ri, na, mg, al, si, k, ca, ba, fe)
}

object GlassRi {
  type PredictType = Double
  val PredictField = new FieldName("RI")
  val ClassifyFields = Seq("Na", "Mg", "Al").map(i=>new FieldName(i))

  def fromGlass(g: Glass) = GlassRi(g.na, g.mg, g.al, g.id)
}

/**
 * lm(RI ~ Na + Si + Al, data=Glass)
 */
case class GlassRi(na: Double, mg: Double, al: Double, id: Int)
      extends Predictable[GlassRi.PredictType] {

  override val ClassifyFields = GlassRi.ClassifyFields
  override val PredictField = GlassRi.PredictField
  override val rowValues = Seq[Double](na, mg, al)
}
