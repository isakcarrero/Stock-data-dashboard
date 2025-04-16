package Data

import scala.collection.mutable

/** the stocks that get added */
case class StockData(ticker: String, amount: Int, price: Double, date: String)

/** the portfolios that are created */
case class Portfolio(portfolioName: String, stocks: mutable.Buffer[StockData])

/** for managing all portfolios, checks if the portfolio already exists*/
object PortfolioManager:
  val portfolios = mutable.LinkedHashMap[String, Portfolio]()
  def createPortfolio(name: String): Boolean =
    if (portfolios.contains(name)) then
      false
    else
      portfolios(name) = Portfolio(name, mutable.Buffer[StockData]())
      true

/** adding new stocks to the portfolio */
  def addStockToPortfolio(portfolioName: String, stock: StockData): Boolean=
    portfolios.get(portfolioName) match
      case Some(portfolio) =>
        portfolio.stocks += stock
        true
      case None => false

/** gets the information of a portfolio */
  def getPortfolio(name: String): Option[Portfolio] = portfolios.get(name)

/** gets the information of all portfolios */
  def getAllPortfolios: Map[String, Portfolio] = portfolios.toMap
