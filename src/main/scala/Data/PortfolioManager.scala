package Data

import scala.collection.mutable

/** the stocks that get added */
case class StockData(ticker: String, amount: Double, price: Double, date: String)

/** the portfolios that are created */
case class Portfolio(portfolioName: String, stocks: Buffer[stockData])

/** for managing all portfolios, checks if the portfolio already exists*/
object PortfolioManager:
  private val portfolios = LinkedHashMap[String, Portfolio]()
  def createPortfolio(name: String): Boolean =
    if (portfolios.contain(name)) then
      false
    else
      portfolios(name) = Portfolio(name)
      true
  
/** adding new stocks to the portfolio */  
  def addStockToPortfolio(portfolioName: String, stocks: StockData): Boolean=
    portfolios.get(portfolioName) match
      case Some(portfolio) =>
        portfolio.stocks += transaction
        true
      case None => false

  def getPortfolio(name: String): Option[Portfolio] = portfolios.get(name)

  def getAllPortfolios: Map[String, Portfolio] = portfolios.toMap
