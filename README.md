This is a little demo project about how to apply [PMML models](http://en.wikipedia.org/wiki/Predictive_Model_Markup_Language) in [Scalding](http://www.github.com/twitter/scalding). I'll have an accompanying blog post up "soon."

# Usage

The first thing you'll need is a PMML file representing the predictive model that you want to apply. You can build that in R or any other language that will export to PMML. 

Once you have that, there's a `Predictable[T]` trait in here that you can use to extend a class that represents a single observation. To configure your model, you'll need to specify three things:

  * `ClassifyFields` - Fields used for the classification
  * `PredictField` - Field to be predicted
  * `rowValues` - A method to convert from the case class to a Seq of values (I'm working on getting rid of this)

Once that is done, you'll get two methods to take care of the rest:

* `buildEvaluator(model: InputStream)` for preparing a model from a PMML (XML) file.
* `predict(evaluator: org.jpmml.evaluator.Evaluator` for applying the model to an object

## Example

Here's an example using the `mlbench::Glass` data set from R. See `Glass.scala` for implementations of `Glass` and `GlassType` (used for predicting the type of glass).

```scala
  // load the data and convert it to a Glass object.
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
```
