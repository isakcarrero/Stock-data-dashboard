package Data

import Data.StockDataParsing.getClosingPrices
import org.json4s.*
import org.json4s.jackson.JsonMethods.*
import scala.io.Source
import java.io.File

object StockDataParsing:
  implicit val formats: Formats = DefaultFormats
  
/** gets the closing price for the last n days. Only works for files currently */  
  def getClosingPrices(ticker: String, days: Int): List[(String, Double)] = 
    val filePath = s"StockAPIDataDaily/$ticker.json"
    val file = new File(filePath)
    if (!file.exists()) return List.empty

    val source = Source.fromFile(file)
    val jsonString = try source.mkString finally source.close()
    val json = parse(jsonString)

    val timeSeries = json \ "Time Series (Daily)"

    val closingData = timeSeries match 
      case JObject(dailyEntries) =>
        dailyEntries.map { case (date, JObject(fields)) =>
          val close = fields.find(_._1 == "4. close").map(_._2.extract[String].toDouble).getOrElse(0.0)
          (date, close)
        }.sortBy(_._1).reverse.take(days)

      case _ => List.empty
    
    closingData
  
/** tester */
@main def testClosingPrices()=
  val prices = getClosingPrices("AAPL", 40)
  println(prices)
  

