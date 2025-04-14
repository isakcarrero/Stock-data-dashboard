package Data

import org.json4s.*
import org.json4s.jackson.JsonMethods.*
import scala.io.Source
import java.io.File
import scala.util.Try
import java.net.URL

object StockDataParser:

  implicit val formats: Formats = DefaultFormats

  /** Change to false if you want to instead use files */
  private val useAPI = true

  /** Gets the closing price for the last n days */
  def getClosingPrices(ticker: String, days: Int): List[(String, Double)] =
    val jsonString =
      if useAPI then fetchLiveData(ticker)
      else readFromFile(s"StockAPIDataDaily/$ticker.json")

    if jsonString.isEmpty then return List.empty

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
/** for reading the files */
  private def readFromFile(path: String): String =
    val file = new File(path)
    if !file.exists() then return ""
    val source = Source.fromFile(file)
    try source.mkString finally source.close()
    
/** for geting the data from the API */
  private def fetchLiveData(ticker: String): String =
    val apiKey = "LMJ7WY6F7CEEQUAD"
    val url = new URL(s"https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=$ticker&apikey=$apiKey&datatype=json")
    Try(Source.fromInputStream(url.openStream()).mkString).getOrElse("")

  /** tester */
  @main def testClosingPrices() =
    val prices = getClosingPrices("BABA", 10)
    println(prices)
