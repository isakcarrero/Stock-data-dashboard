package Visuals

import Data.{Portfolio, PortfolioManager, StockData}
import scalafx.scene.chart.{CategoryAxis, NumberAxis, ScatterChart, XYChart}
import scalafx.scene.control.{Button, Tooltip, ColorPicker}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.text.Font
import scalafx.geometry.{Insets, Pos}
import scalafx.util.Duration
import scalafx.application.Platform
import scalafx.scene.Node
import scalafx.scene.paint.Color
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ButtonType, ChoiceBox, Dialog}
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** The scatterplot shows purchase dates and prices of stocks in a portfolio.
 * The user can add multiple portfolios in colors of their choosing.*/
class Scatterplot(portfolioName: String, color: String):

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
    style = s"-fx-font-size: 12px; -fx-background-radius: 5;"
    onAction = _ => addPortfolioDialog()
  
  private val buttonBox = new HBox:
    alignment = Pos.TopLeft
    children = addPortfolioButton

  /** Scatter chart styling and axis labels */
  scatterChart.setTitle(s"Portfolio Purchase History")
  scatterChart.setAnimated(false)

  xAxis.setLabel("Purchase Date")
  yAxis.setLabel("Price per Share (USD)")
  xAxis.setTickLabelFont(new Font(10))
  yAxis.setTickLabelFont(new Font(10))
  scatterChart.setStyle("-fx-font-size: 11px;")
  scatterChart.setPrefWidth(900)
  scatterChart.setPrefHeight(900)

  /** Stores all currently displayed (portfolio name, color) pairs */
  private val displayedPortfolios = ObservableBuffer[(String, String)]()
  
  displayedPortfolios += ((portfolioName, color))
  updateChart()
  
  root.children = Seq(buttonBox, scatterChart)

  /** Dialog for adding a new portfolio with color */
  private def addPortfolioDialog(): Unit =
    val dialog = new Dialog[String]()
    dialog.setTitle("Add Portfolio to Chart")
    dialog.setWidth(200)

    /** Dropdown for selecting a portfolio that’s not already in plot */
    val portfolioChoice = new ChoiceBox[String]()
    portfolioChoice.items = ObservableBuffer.from(
      PortfolioManager.getAllPortfolios.keys.filterNot(name =>
        displayedPortfolios.map(_._1).contains(name)))

   
    val scatterColor = new ColorPicker()

    val vbox = new VBox(10, portfolioChoice, scatterColor)
    dialog.getDialogPane.setContent(vbox)
    dialog.getDialogPane.getButtonTypes.add(ButtonType.OK)
    dialog.showAndWait() match
      case Some(ButtonType.OK) =>
        val selectedPortfolio = portfolioChoice.getValue
        if selectedPortfolio != null then
          val chosenColor = scatterColor.value.value
          val hexColor = String.format("#%02X%02X%02X",
            (chosenColor.getRed * 255).toInt,
            (chosenColor.getGreen * 255).toInt,
            (chosenColor.getBlue * 255).toInt)

          /** Add portfolio and refresh the chart */
          displayedPortfolios += ((selectedPortfolio, hexColor))
          updateChart()
      case _ => ()

  /** Updates the scatterplot with all portfolios in the list */
  private def updateChart(): Unit =
    Platform.runLater {
      scatterChart.getData.clear()

      /** Collects all unique purchase dates across the portfolios in the scatterplot*/
      val allDates = displayedPortfolios.flatMap((name, _) =>
        PortfolioManager.getPortfolio(name)
          .map(_.stocks.map(_.date))
          .getOrElse(Seq.empty).distinct)

      /** Sort and format the date labels for x-axis */
      val sortedDates = allDates.flatMap(dateStr =>
        parseDate(dateStr).map(parsedDate => (parsedDate, dateStr))
      ).sortBy(_._1).map((_, dateStr) => formatDate(dateStr))

      xAxis.setCategories(ObservableBuffer.from(sortedDates))

      /** For each portfolio a series with data points and tooltips gets added */
      displayedPortfolios.foreach((currentPortfolioName, currentColor) =>
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

              /** Attach tooltip and color to each scatter node */
              data.nodeProperty().addListener((_, _, node) =>
                if node != null then
                  Tooltip.install(node, tooltip.delegate)
                  node.setStyle(s"-fx-background-color: $currentColor;")
              ))
            scatterChart.getData.add(series)
          case None => )
    }

  /** parseDate and formatDate are used to give the date the right form */
  private def parseDate(dateString: String): Option[LocalDate] =
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    try Some(LocalDate.parse(dateString, formatter))
    catch case _: Exception => None
  
  private def formatDate(dateString: String): String =
    parseDate(dateString)
      .map(_.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
      .getOrElse(dateString)

  /** Creates a tooltip for each stock in the scatterplot */
  private def createTooltip(portfolioName: String, stock: StockData): Tooltip =
    new Tooltip(
      s"""Portfolio: $portfolioName
         |Ticker: ${stock.ticker}
         |Date: ${stock.date}
         |Price: $$${stock.price}
         |Shares: ${stock.amount}""".stripMargin)

  /** Returns root layout as a Node */
  def getNode: Node = root
