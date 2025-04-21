package Visuals

import Data.PortfolioManager.getPortfolio
import scalafx.scene.chart.PieChart
import scalafx.collections.ObservableBuffer

/** The piechart visualizes a portfolios stock allocation. It calculates the weighted percentages of the 
 * stocks and converts them into percentages that are then displayed in te chart */
class Piechart(portfolioName: String):

  private val portfolioData = getPortfolio(portfolioName) match
    case Some(portfolio) =>
      /** portfolios can have Shares of same company bought at
       * different time so we have to first group all of the same ones together  */
      val groupedValues = portfolio.stocks.groupBy(_.ticker).map((ticker, stocks) =>
          ticker -> stocks.map(s => s.amount * s.price).sum)
      val totalValue = groupedValues.values.sum
      groupedValues.map((ticker, value) => ticker -> (value / totalValue * 100))
    case None => Map[String, Double]()

  val chart = new PieChart:
    style = s"-fx-font-size: 8px;"
    title = s"$portfolioName Allocation"
    data = ObservableBuffer(portfolioData.map((stock, percentage) =>
      /** example: [Data[AAPL (68,8%),68.81330823122744] */
      PieChart.Data(s"$stock (${"%.1f".format(percentage)}%)", percentage)).toSeq: _*)
    println(data)