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
  val APIKey = "LMJ7WY6F7CEEQUAD"

  /** Fetches stock data and saves it to a file in the StockAPIData folder */
  def getStockData(ticker: String): Try[Unit] =
    Try:
      val url = new URL(s"https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY_ADJUSTED&symbol=$ticker&apikey=$APIKey&datatype=json")
      val jsonString = Source.fromInputStream(url.openStream()).mkString

      val folder = new File("StockAPIData")
      if !folder.exists() then folder.mkdir()

      val file = new File(s"StockAPIData/$ticker.json")
      val writer = new BufferedWriter(new FileWriter(file))
      writer.write(jsonString)
      writer.close()
      println(s"Downloaded $ticker data to StockAPIData/$ticker.json")

object StockDataFetcher:
  def main(args: Array[String]): Unit =
    val dataFetcher = new Datafetching()

    /** change after every download*/
    val stocks = List("WDC", "WLTW", "XEL", "XLNX", "ZM", "ZS")

    stocks.foreach: stock =>
      dataFetcher.getStockData(stock) match
        case Success(_) => println(s"$stock data download successful.")
        case Failure(exception) => println(s"${exception.getMessage}")
