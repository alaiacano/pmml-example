package pmml.example

import org.dmg.pmml.{IOUtil, FieldName}
import org.jpmml.evaluator.{ModelEvaluatorFactory, EvaluatorUtil, Evaluator}
import org.jpmml.manager.PMMLManager
import java.io.InputStream

// jpmml needs a Java map, which is converted from a *mutable* map
import scala.collection.mutable.{Map => MMap}
import scala.collection.JavaConverters._

object Predictable {
  // load the model from /src/main/resources/model_name.pmml
  def buildEvaluator(model: InputStream): Evaluator = {

    // build up the pmml evaluator
    val pmml = IOUtil.unmarshal(model)
    val pmmlManager = new PMMLManager(pmml)
    pmmlManager
      .getModelManager(null, ModelEvaluatorFactory.getInstance)
      .asInstanceOf[Evaluator]
  }
}

trait Predictable[T] {

  // These three values need to be overridden
  val ClassifyFields: Seq[FieldName]
  val PredictField: FieldName
  val rowValues: Seq[AnyVal]

  /**
   * [[Evaluator]] objects require a [[ java.util.Map[FieldName, Any] ]], so we do some
   * conversion.
   */
  lazy val formattedDataPoints: java.util.Map[FieldName, _] = {
    val fieldsAndValues = ClassifyFields
      .zip(rowValues)
      .map{fv => fv._1 -> fv._2}
    MMap(fieldsAndValues:_*).asJava
  }

  def predict(evaluator: Evaluator): T = {

    val predictionMap = evaluator
      .evaluate(formattedDataPoints)
      .get(PredictField)

    EvaluatorUtil.decode(predictionMap)
      .asInstanceOf[T]
  }
}