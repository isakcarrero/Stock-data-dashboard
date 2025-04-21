
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

  /**def saveData(stage: Stage): Unit =
    val fileChooser = new FileChooser
    fileChooser.title = "Save Portfolio Data"
    fileChooser.extensionFilters.add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"))

    val selectedFile = fileChooser.showSaveDialog(stage)
    if selectedFile != null then
      val file = new File(selectedFile.getPath)
      val writer = new BufferedWriter(new FileWriter(file))

      try
        // Write header
        writer.write("portfolioName,ticker,amount,price,date")
        writer.newLine()

        // Write all portfolio data
        PortfolioManager.getAllPortfolios.foreach { (_, portfolio) =>
          portfolio.stocks.foreach { stock =>
            writer.write(s"${portfolio.portfolioName},${stock.ticker},${stock.amount},${stock.price},${stock.date}")
            writer.newLine()
          }
        }
      finally
        writer.close()

  def loadData(stage: Stage): Unit =
    val fileChooser = new FileChooser
    fileChooser.title = "Load Portfolio Data"
    fileChooser.extensionFilters.add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"))

    val selectedFile = fileChooser.showOpenDialog(stage)
    if selectedFile != null then
      val source = Source.fromFile(selectedFile.getPath)
      try
        val lines = source.getLines().toVector
        if lines.nonEmpty && lines.head == "portfolioName,ticker,amount,price,date" then
          // Clear existing portfolios
          PortfolioManager.portfolios.clear()

          // Process each data line
          lines.tail.foreach { line =>
            val parts = line.split(",")
            if parts.length == 5 then
              val name = parts(0)
              val ticker = parts(1)
              val amount = parts(2).toInt
              val price = parts(3).toDouble
              val date = parts(4)

              if !PortfolioManager.portfolios.contains(name) then
                PortfolioManager.createPortfolio(name)

              PortfolioManager.addStockToPortfolio(name, StockData(ticker, amount, price, date))
          }
      finally
        source.close()**/

object StockDataFetcher:
  def main(args: Array[String]): Unit =
    val dataFetcher = new Datafetching()

    /** If you want to download files then add the ticker symbols of the stocks whos info you want to download*/
    val stocks = List("AAPL", "ADBE", "ADI", "ADP", "AEP")

    stocks.foreach: stock =>
      dataFetcher.getStockData(stock) match
        case Success(_) => println(s"$stock data download successful.")
        case Failure(exception) => println(s"${exception.getMessage}")
