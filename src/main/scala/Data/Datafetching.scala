
package Data

import java.net.URL
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import scala.io.Source
import scala.util.Try
import scala.util.Success
import scala.util.Failure

class Datafetching:
  private val APIKey = "LMJ7WY6F7CEEQUAD"

  /** Fetches stock data and saves it to a file in the StockAPIData folder */
  def getStockData(ticker: String) =
    Try:
      /** for monthly time series use TIME_SERIES_MONTHLY_ADJUSTED */
      val url = new URL(s"https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=$ticker&apikey=$APIKey&datatype=json")
      val jsonString = Source.fromInputStream(url.openStream()).mkString

      /** if you dont have  afolder already then this creates one */
      val folder = new File("StockAPIDataDaily")
      if !folder.exists() then folder.mkdir()

      val file = new File(s"StockAPIDataDaily/$ticker.json")
      val writer = new BufferedWriter(new FileWriter(file))
      writer.write(jsonString)
      writer.close()

object StockDataFetcher:
  def main(args: Array[String]): Unit =
    val dataFetcher = new Datafetching()

    /** If you want to download files then add the ticker symbols of the stocks whos info you want to download*/
    val stocks = List("AAPL", "ADBE", "ADI", "ADP", "AEP")

    stocks.foreach: stock =>
      dataFetcher.getStockData(stock) match
        case Success(_) => println(s"$stock data download successful.")
        case Failure(exception) => println(s"${exception.getMessage}")
