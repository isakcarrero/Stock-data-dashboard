package Visuals

import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafx.scene.control.Label
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.text.Font
import Data.PortfolioManager.getPortfolio
import Data.StockDataParser.getClosingPrices
import scalafx.geometry.Pos.Center



class Portfolioinfo(portfolioName: String):

  private val portfolioStocks = getPortfolio(portfolioName)

  /** combined buy price of stocks */
  private val totalCost: Double = portfolioStocks.map(_.stocks.map(x => x.amount * x.price).sum).get

  /** uses the parser to gain the historical prices */
  private val stockData =
    portfolioStocks.map(_.stocks.groupBy(_.ticker).map(t =>
      val ticker = t._1
      val stocks = t._2
      val latest = getClosingPrices(ticker, 1).headOption.map(_._2)
      val weekAgo = getClosingPrices(ticker, 6).lastOption.map(_._2)
      val amount = stocks.map(_.amount).sum
      (ticker, latest.get, weekAgo.get, amount)
    )).get

  /********************************************************************/
  /** Calculations for the values that are displayed on the info card */
  private val totalValue = stockData.map(x => x._2 * x._4).sum

  private val totalGain = totalValue - totalCost
  private val totalGainPerc = totalGain / totalCost * 100

  private val weeklyGain = stockData.map(x => (x._2 - x._3) * x._4).sum
  private val weeklyGainPerc = weeklyGain / (totalValue - weeklyGain) * 100


  private val numberOfStocks: Int = portfolioStocks.map(_.stocks.size).getOrElse(0)
  private val biggestAllocation: String = portfolioStocks.flatMap(_.stocks.groupBy(_.ticker).maxByOption(_._2.map(s => s.amount * s.price).sum).map(_._1)).get

  /** ***************************************************************** */
  /** Creation of labels that are displayed on the info card */
  private val totalValueLabel = new Label(s"Total Value:"):
    style = "-fx-font-weight: bold; -fx-font-size: 16px;"
  private val totalValueValueLabel = new Label(s"$totalValue"):
    style = "-fx-font-weight: normal; -fx-font-size: 16px; "

  private val weeklyGainLabel = new Label("Week's gain:"):
    style = "-fx-font-weight: bold; -fx-font-size: 16px;"
  private val weeklyGainValueLabel = new Label( 
    if weeklyGain < 0 then
      val newWeekly = weeklyGain*(-1)
      s"-$$$newWeekly ($weeklyGainPerc)"
    else
      s"$$$weeklyGain ($weeklyGainPerc)"):
    style = "-fx-font-weight: normal; -fx-font-size: 16px; "

  private val totalGainLabel = new Label("Total gain:"):
    style = "-fx-font-weight: bold; -fx-font-size: 16px;"
  private val totalGainValueLabel = new Label(s"$totalGain ($totalGainPerc)"):
    style = "-fx-font-weight: normal; -fx-font-size: 16px;"

  private val numberOfStockLabel = new Label("Number of Stock:"):
    style = "-fx-font-weight: bold; -fx-font-size: 16px;"
  private val numberOfStockValueLabel = new Label(numberOfStocks.toString):
    style = "-fx-font-weight: normal; -fx-font-size: 16px; "

  private val biggestAllocationLabel = new Label("Biggest allocation:"):
    style = "-fx-font-weight: bold; -fx-font-size: 16px;"
  private val biggestAllocationValueLabel = new Label(biggestAllocation):
    style = "-fx-font-weight: normal; -fx-font-size: 16px; "

  val card = new StackPane:
    style = "-fx-background-color: white; -fx-border-color: #d3d3d3;"
    prefWidth = 400
    padding = Insets(10)

    val content = new VBox(8):
      alignment = Center
      children = Seq(
        new HBox(10) { children = Seq(totalValueLabel, totalValueValueLabel) },
        new HBox(10) { children = Seq(weeklyGainLabel, weeklyGainValueLabel) },
        new HBox(10) { children = Seq(totalGainLabel, totalGainValueLabel) },
        new HBox(10) { children = Seq(numberOfStockLabel, numberOfStockValueLabel) },
        new HBox(10) { children = Seq(biggestAllocationLabel, biggestAllocationValueLabel) }
      )

    children.add(content)