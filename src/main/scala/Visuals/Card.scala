
package Visuals

import Data.PortfolioManager
import scalafx.geometry.{Insets, Orientation, Pos}
import scalafx.scene.control.{Alert, Button, ButtonType, ChoiceBox, ColorPicker, Dialog, Label, SplitPane, TextField, TextInputDialog}
import scalafx.scene.layout.{ColumnConstraints, GridPane, RowConstraints, StackPane, VBox}
import scalafx.collections.ObservableBuffer
import scalafx.scene.Node
import scalafx.scene.chart.PieChart
import scalafx.scene.control.Alert.AlertType

/** The card grid with the cards make up the center of the rootpane. They are there in case the user
 * wants to visualize data of specific stocks or portfolios. There are four cads where charts or info
 * cards can be inserted. When the user is done with a chart, the user can close the chart and go back to
 * the original state. */

object Card:

  /** A case class for saving the current state of each card */
  case class CardState(chartType: String ="" ,portOrStock: Any = "", color: String = "" )
  
  /** the card states of each card gets stored here. This val is important as it helps with getting
   * the location of the charts when saving them, and ensuring that they are inserted into the right 
   * card when loading data from a file*/
  val cardStates: Array[CardState] = Array.fill(4)(CardState())

  /** this method is used to update the state of a card when information is added or removed*/
  private def updateCardState(targetCard: StackPane, newState: CardState): Unit =
    val index = targetCard match
      case `card1` => 0
      case `card2` => 1
      case `card3` => 2
      case `card4` => 3

    cardStates(index) = newState

  /** method for creating each of the cards. */
  private def createCard(text: String) =
    val card = new StackPane()
    card.setStyle("-fx-background-color: white; -fx-border-color: #d3d3d3; -fx-border-width: 1px;")
    card.setPrefSize(400, 400)
    val button = new Button(text)
    button.setOnAction(_ => showSelectionDialog(card))
    card.getChildren.add(button)
    card

  /** wraps chart/info with close (X) button. When the "x" is pressed, the chart gets deleted and
   * is replaced with a new insert button so that new charts or info can be inserted */
  def closeWrapper(content: Node, targetCard: StackPane): StackPane =
    val wrapper = new StackPane()
    wrapper.setStyle("-fx-background-color: white; -fx-border-color: #d3d3d3; -fx-border-width: 1px;")

    val closeButton = new Button("\u2716")
    closeButton.setStyle("-fx-background-color: transparent;")
    
    /** Alert to make sure user understand what thy are doing */
    closeButton.setOnAction(_ =>
      val alert = new Alert(Alert.AlertType.Warning)
      alert.setTitle("Confirm Removal")
      alert.setHeaderText("Are you sure you want to remove this content?")
      alert.setContentText("The current information will be removed.")

      alert.showAndWait() match
        case Some(ButtonType.OK) =>
          val insertButton = new Button("Insert")
          insertButton.setOnAction(_ => showSelectionDialog(targetCard))
          targetCard.getChildren.setAll(insertButton)
          updateCardState(targetCard, CardState()))

    StackPane.setMargin(closeButton, Insets(5))
    StackPane.setAlignment(closeButton, Pos.TopRight)

    wrapper.getChildren.addAll(content, closeButton)
    wrapper

  /** Create the four cards */
  val card1 = createCard("Insert")
  val card2 = createCard("Insert")
  val card3 = createCard("Insert")
  val card4 = createCard("Insert")

  /** SplitPane is used as it is easier to implement card resizing than with GridPane */
  /** Horizontal split (top and bottom halves) */
  private val topSplit = new SplitPane()
  topSplit.setOrientation(Orientation.Horizontal)
  topSplit.getItems.addAll(card1, card2)
  topSplit.setDividerPositions(0.5)

  private val bottomSplit = new SplitPane()
  bottomSplit.setOrientation(Orientation.Horizontal)
  bottomSplit.getItems.addAll(card3, card4)
  bottomSplit.setDividerPositions(0.5)

  /** Vertical split (splits top and bottom) */
  val cardGrid = new SplitPane()
  cardGrid.setOrientation(Orientation.Vertical)
  cardGrid.getItems.addAll(topSplit, bottomSplit)
  cardGrid.setDividerPositions(0.5)

  /** Method for getting the portfolio names form the PortfolioManager class, so that they can be
 * listed in the dropdown lists. */
  def getPortfolioNames: ObservableBuffer[String] =
    ObservableBuffer.from(PortfolioManager.getAllPortfolios.keys.toSeq)

/** Selection dialog where the user chooses what kind of chart or information they want to insert. */
  def showSelectionDialog(targetCard: StackPane) =
    val dialog = new Dialog[String]()
    dialog.setTitle("Select Display Type")

    val columnChartButton = new ButtonType("Column Chart")
    val infoCardButton = new ButtonType("Information Card")
    val pieChartButton = new ButtonType("Pie Chart")
    val scatterPlotButton = new ButtonType("Scatter Plot")

    dialog.getDialogPane.getButtonTypes.addAll(columnChartButton,
      infoCardButton, pieChartButton, scatterPlotButton, ButtonType.Cancel)

    /** Different methods get called based on the users choice */
    dialog.showAndWait() match
      case Some(`columnChartButton`) => columnChartDialog("Enter stock ticker:", targetCard)
      case Some(`infoCardButton`) => infoCard("Info Card", targetCard)
      case Some(`pieChartButton`) => pieChart("Pie Chart", targetCard)
      case Some(`scatterPlotButton`) => scatterPlot("Scatter Plot", targetCard)
      case _ =>

  /** This functions as a dialog for choosing which portfolio to display in the Piechart,
   * Infocard and Scatterplot. It saves the chosen portfolio as an option */
  def portfolioSelectionDialog(title: String): Option[String] =
    val dialog = new Dialog[String]()
    dialog.setTitle(title)
    dialog.setWidth(200)

    val guideText = new Label("Choose a portfolio:")
    val portfolioChoice = new ChoiceBox[String]()
    portfolioChoice.items = getPortfolioNames

    val content = new VBox():
      children = Seq(guideText, portfolioChoice)
      prefWidth = 200
    dialog.getDialogPane.setContent(content)
    dialog.getDialogPane.getButtonTypes.add(ButtonType.OK)

    dialog.showAndWait() match
      case Some(ButtonType.OK) => Option(portfolioChoice.getValue)
      case _ => None

  /** This method uses the chosen portfolio from the portfolioSelectionDialog to visualize the data as a
   * Pie Chart, Info Card or Scatter Plot. */
  def showPortfolioChart[T](title: String, targetCard: StackPane, chartType: String,
                            chartBuilder: String => T, nodeExtractor: T => Node) =
    portfolioSelectionDialog(title) match
      case Some(name) =>
        PortfolioManager.getPortfolio(name) match
          case Some(p) if p.stocks.nonEmpty =>
            val chart = chartBuilder(name)
            val node = nodeExtractor(chart)
            targetCard.getChildren.setAll(closeWrapper(node, targetCard))
            updateCardState(targetCard, CardState(chartType, name))
            println("cardStates(0) =" + cardStates(0))
            println("cardStates(1) =" + cardStates(1))
            println("cardStates(2) =" + cardStates(2))
            println("cardStates(3) =" + cardStates(3))
          case Some(_) =>
            new Alert(AlertType.Error, s"Portfolio '$name' is empty!").showAndWait()
          case None =>
      case None =>

  /** When the user selects 'Column Chart', a new dialog pops up. The user inserts a stock ticker
   * and chooses a color from the ColorPicker. The method uses the inserted stock ticker and color
   * as parameters when creating the new Columnchart. It has a lot of the same functionality
   * as portfolioSelectionDialog and showPortfolioChart, but dou to it having a ColorPicker it
   * had to be made separate*/
  def columnChartDialog(title: String, targetCard: StackPane) =
    val dialog = new Dialog[String]
    dialog.setTitle("Select Portfolio and Color")
    dialog.setWidth(200)
    val stockInput = new TextField():
      promptText = "Stock Ticker (e.g., AAPL)"

    val columnColor = new ColorPicker()

    val vbox = new VBox(10, stockInput, columnColor)
    dialog.getDialogPane.setContent(vbox)
    dialog.getDialogPane.getButtonTypes.add(ButtonType.OK)

    dialog.showAndWait() match
      case Some(ticker) =>
        val stockTicker = stockInput.text.value
        val chosenColor = columnColor.value.value
        val hexColor = String.format("#%02X%02X%02X",
          (chosenColor.getRed * 255).toInt,
          (chosenColor.getGreen * 255).toInt,
          (chosenColor.getBlue * 255).toInt)
        val columnChartVisual = new Columnchart(stockTicker, hexColor)
        targetCard.getChildren.setAll(closeWrapper(columnChartVisual.getNode, targetCard))

        updateCardState(targetCard, CardState("ColumnChart", stockTicker, hexColor))
      case _ =>

  /** This is the method for visualizing the Info Card. It uses the showPortfolioChart
   * to do so*/
  def infoCard(title: String, targetCard: StackPane) =
    showPortfolioChart(title, targetCard, "infoCard", name => new Portfolioinfo(name), _.infoCard)

  /** This is the method for visualizing the Pie Chart. It uses the showPortfolioChart
   * to do so*/
  def pieChart(title: String, targetCard: StackPane) =
    showPortfolioChart(title, targetCard, "pieChart", name => new Piechart(name), _.chart)

  /** This is the method for visualizing the Scatter Plot. It uses the showPortfolioChart
   * to do so*/
  def scatterPlot(title: String, targetCard: StackPane) =
    showPortfolioChart(title, targetCard, "scatterPlot",
      name =>
        val plot = new Scatterplot(name)
        plot.onPortfoliosAdd = portfolios => updateCardState(targetCard, CardState("scatterPlot", portfolios))
        plot,
      _.getNode)



