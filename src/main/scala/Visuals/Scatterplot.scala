package Visuals

import Data.StockDataParser.getClosingPrices
import Data.{Portfolio, PortfolioManager, StockData}
import scalafx.scene.chart.{CategoryAxis, NumberAxis, ScatterChart, XYChart}
import scalafx.scene.control.{Alert, Button, ButtonType, ChoiceBox, ColorPicker, Dialog, Label, Tooltip}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.text.Font
import scalafx.geometry.{Insets, Pos}
import scalafx.util.Duration
import scalafx.application.Platform
import scalafx.scene.Node
import scalafx.scene.paint.Color
import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.Popup

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** The scatterplot shows purchase dates and prices of stocks in a portfolio.
 * The user can add multiple portfolios.*/
class Scatterplot(portfolioName: String):

  /** Root layout for the scatterplot component */
  private val root = new VBox:
    padding = Insets(5)
    spacing = 5

  /**Chart axes and scatter chart setup */
  private val xAxis = new CategoryAxis()
  private val yAxis = new NumberAxis()
  private val scatterChart = new ScatterChart[String, Number](xAxis, yAxis)

  /** Button for adding additional portfolios to the scatterplot */
  private val addPortfolioButton = new Button("+"):
    style = s"-fx-font-size: 11px; -fx-background-radius: 5;"
    onAction = _ => addPortfolioDialog()

  private val buttonBox = new HBox:
    alignment = Pos.TopLeft
    children = addPortfolioButton

  /** Scatter chart styling and axis labels */
  scatterChart.setTitle(s"Portfolio Purchase History")
  scatterChart.setAnimated(false)

  xAxis.setLabel("Purchase Date")
  yAxis.setLabel("Price per Share (USD)")
  xAxis.setTickLabelFont(new Font(8))
  yAxis.setTickLabelFont(new Font(8))
  scatterChart.setStyle("-fx-font-size: 9px;")
  scatterChart.setPrefWidth(900)
  scatterChart.setPrefHeight(900)

  /** Callback function for adding portfolios to the chart */
  var onPortfoliosAdd: Seq[String] => Unit = _ => ()
  
  /** Stores all currently displayed (portfolio name) */
  val displayedPortfolios = ObservableBuffer[String]()

  displayedPortfolios += portfolioName
  onPortfoliosAdd(displayedPortfolios.toSeq)
  updateChart()

  root.children = Seq(buttonBox, scatterChart)

  /** Dialog for adding a new portfolio */
  private def addPortfolioDialog(): Unit =
    val dialog = new Dialog[String]()
    dialog.setTitle("Add Portfolio to Chart")
    dialog.setWidth(200)

    /** Dropdown for selecting a portfolio that’s not already in plot */
    val portfolioChoice = new ChoiceBox[String]()
    portfolioChoice.items = ObservableBuffer.from(
      PortfolioManager.getAllPortfolios.keys.filterNot(name =>
        displayedPortfolios.contains(name)))


    dialog.getDialogPane.setContent(portfolioChoice)
    dialog.getDialogPane.getButtonTypes.add(ButtonType.OK)
    dialog.showAndWait() match
      case Some(ButtonType.OK) =>
        val selectedPortfolio = portfolioChoice.getValue
        if selectedPortfolio != null then
          /** Add portfolio and refresh the chart */
          displayedPortfolios += selectedPortfolio
          onPortfoliosAdd(displayedPortfolios.toSeq)
          updateChart()
      case _ => ()

  /** Updates the scatterplot with all portfolios in the list */
  private def updateChart(): Unit =
      scatterChart.getData.clear()

      /** Collects all unique purchase dates across the portfolios in the scatterplot.
       * format: [2024-04-19, 2023-07-20, 2025-04-13]*/
      val allDates = displayedPortfolios.flatMap(name =>
        PortfolioManager.getPortfolio(name).map(_.stocks.map(_.date)).get)

      /** Sort and format the date labels for x-axis
       * format: [Jul 20, 2023, Apr 19, 2024, Apr 13, 2025]*/
      val sortedDates = allDates.flatMap(dateStr =>
        parseDate(dateStr).map(parsedDate => (parsedDate, dateStr))
      ).sortBy(_._1).map((_, dateStr) => formatDate(dateStr))

      xAxis.setCategories(ObservableBuffer.from(sortedDates))

      /** For each portfolio a series with data points and tooltips gets added */
      displayedPortfolios.foreach(currentPortfolioName =>
        PortfolioManager.getPortfolio(currentPortfolioName) match
          case Some(portfolio) =>
            val series = new XYChart.Series[String, Number]()
            series.setName(currentPortfolioName)

            portfolio.stocks.foreach(stock =>
              val formattedDate = formatDate(stock.date)
              val data = XYChart.Data[String, Number](formattedDate, stock.price)
              val tooltip = createTooltip(currentPortfolioName, stock)
              tooltip.setShowDelay(Duration(100))
              series.getData.add(data)

              /** Attach tooltip and latest price(when clicked on) to each scatter node */
              data.nodeProperty().addListener((_, _, node) =>
                if node != null then
                  Tooltip.install(node, tooltip.delegate)
                  node.setOnMouseClicked(e => infoPopup(stock.ticker, e.getScreenX, e.getScreenY))))

            scatterChart.getData.add(series)
          case None => )

  /** parseDate and formatDate are used to give the date the right form */
  private def parseDate(dateString: String) =
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    try Some(LocalDate.parse(dateString, formatter))
    catch case _: Exception => None

  private def formatDate(dateString: String): String =
    parseDate(dateString)
      .map(_.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))).getOrElse(dateString)

  /** Creates a tooltip for each stock in the scatterplot */
  private def createTooltip(portfolioName: String, stock: StockData): Tooltip =
    new Tooltip(
      s"""Portfolio: $portfolioName
         |Ticker: ${stock.ticker}
         |Date: ${stock.date}
         |Price: $$${stock.price}
         |Shares: ${stock.amount}""".stripMargin)

  /** a function for displaying the latest closing price of a datapoint */
  private def infoPopup(ticker: String, x: Double, y: Double): Unit=
    val latest = getClosingPrices(ticker, 1).headOption.map(_._2).getOrElse(0.0)
    val label = new Label(s"$ticker\nLatest close: $$${f"$latest%.2f"}"):
      style = "-fx-background-color: rgba(50, 50, 50, 0.75); -fx-text-fill: white; -fx-padding: 10px;" +
        "-fx-font-size: 8px;-fx-background-radius: 8px; -fx-border-radius: 8px;"
    val popup = new Popup()
    popup.getContent.add(label)
    popup.setAutoHide(true)
    popup.show(scatterChart.getScene.getWindow, x + 5, y + 5)

  /** Returns root layout as a Node */
  def getNode: Node = root