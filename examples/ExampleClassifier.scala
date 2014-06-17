import com.twitter.scalding._
import pmml.example._
import TDsl._

/*
To run:

cd pmml-example
scald \
  --local \
  --cp target/scala-2.10/pmml-example-assembly-0.0.1.jar \
  examples/ExampleClassifier.scala \
  --input src/main/resources/data/Glass.tsv \
  --output-type examples/glass_classified.tsv \
  --output-ri examples/glass_ri_predicted.tsv
*/
class ExampleClassifier(args: Args) extends Job(args) {

  // parse input arguments
  val inputFile = args("input")
  val outputTypeFile = args("output-type")
  val outputRiFile = args("output-ri")
  val modelFile = args.getOrElse("model", "/models/glass_model.xml")

  // load the data and convert it to a Glass object. We'll use this a few times.
  val data: TypedPipe[Glass] = TextLine(args("input"))
    .map{line => Glass.fromTextLine(line)}

  // open the SVM model used to predict the type of glass.
  val svmModel = getClass.getResource(modelFile).openStream()
  val svmModelEvaluator = Predictable.buildEvaluator(svmModel)

  val predictedType: TypedPipe[(Int, GlassType.PredictType)] = data
    // turn the Glass object into a GlassType
    .map(GlassType.fromGlass)
    // predict type
    .map{ meas => (meas.id, meas.predict(svmModelEvaluator)) }
    // write output
    .write(TypedTsv[(Int, GlassType.PredictType)](outputTypeFile))

  // Open the linear regression model used to estimate the amount of Ri
  val lmModel = getClass.getResource("/models/glass_model_lm.xml").openStream()
  val lmModelEvaluator = Predictable.buildEvaluator(lmModel)

  val predictedRi = data
    // turn the Glass object into GlassRi
    .map(GlassRi.fromGlass)
    // apply the model
    .map{ meas => (meas.id, meas.predict(lmModelEvaluator)) }
    // write the output
    .write(TypedTsv[(Int, GlassRi.PredictType)](outputRiFile))
}
