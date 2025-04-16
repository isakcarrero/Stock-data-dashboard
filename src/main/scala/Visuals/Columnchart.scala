package Visuals

import Data.StockDataParser
import scalafx.scene.chart.{BarChart, CategoryAxis, NumberAxis, XYChart}
import scalafx.scene.layout.{Priority, VBox}
import scalafx.scene.control.{ChoiceBox, Tooltip}
import scalafx.collections.ObservableBuffer
import scalafx.Includes.*
import scalafx.application.Platform
import scalafx.geometry.Insets
import scalafx.scene.text.Font
import scalafx.geometry.Side
import scalafx.util.Duration
import scalafx.scene.Node
import scalafx.scene.paint.Color

/** The column chart visualizes a stocks historical prices (one week, two weeks, one month three months).
 * The user writes a stock ticker in the TextField and gives the columns a color of their choosing */
class Columnchart(ticker: String, color: String):

  private val root = new VBox:
    padding = Insets(5)
    spacing = 5

  /** Time series dropdown selection functionality, with an initial value of "1 week" */
  private val timeChoice = new ChoiceBox[String](ObservableBuffer("1 Week", "2 Weeks", "1 Month", "3 Months"))
  timeChoice.setValue("1 Week")

  /** Column chart layout and design.*/
  private val xAxis = new CategoryAxis()
  private val yAxis = new NumberAxis()
  private val barChart = new BarChart[String, Number](xAxis, yAxis)

  barChart.setTitle(s"$ticker price over time")
  barChart.setCategoryGap(1)
  barChart.setBarGap(0.5)
  barChart.setLegendVisible(false)
  barChart.setAnimated(false)
  
  xAxis.setLabel("Date")
  yAxis.setLabel("Closing Price (USD)")

  barChart.setPrefWidth(900)
  barChart.setPrefHeight(900)

  xAxis.setTickLabelFont(new Font(10))
  yAxis.setTickLabelFont(new Font(10))
  barChart.setStyle("-fx-font-size: 11px;")

  /** Functionality for updating the column chart after a new time series is chosen */
  private def updateChart(period: String): Unit =
    val days = period match
      case "1 Week" => 6
      case "2 Weeks" => 11
      case "1 Month" => 25
      case "3 Months" => 78
      case _ => 6

    /** Functionality for fetching the n latest closing prices, either form a file of form the API*/
    val closingPrices = StockDataParser.getClosingPrices(ticker, days)

    Platform.runLater:
      barChart.getData.clear()
      val series = new XYChart.Series[String, Number]()

      /** Adds each pair (date, price) as a column to the column chart */
      closingPrices.reverse.foreach { case (date, price) =>
        val data = XYChart.Data[String, Number](date, price)
        val tooltip = new Tooltip(s"Date: $date\nPrice: $price")
        tooltip.setShowDelay(Duration(100))
        series.getData.add(data)

        /** Adds a tooptip for each column (node)*/
        data.nodeProperty().addListener { (_, _, node) =>
          if node != null then
            Tooltip.install(node, tooltip.delegate)
            node.setStyle(s"-fx-bar-fill: $color ;")
        }
      }

      barChart.getData.add(series)

  updateChart("1 Week")
  timeChoice.onAction = _ => updateChart(timeChoice.getValue)

  root.children = Seq(timeChoice, barChart)

  /** For accessing root container as node */
  def getNode: Node = root
