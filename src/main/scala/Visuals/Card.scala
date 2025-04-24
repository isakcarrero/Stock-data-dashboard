
package Visuals

import Data.PortfolioManager
import scalafx.geometry.{Insets, Orientation, Pos}
import scalafx.scene.control.{Alert, Button, ButtonType, ChoiceBox, ColorPicker, Dialog, SplitPane, TextField, TextInputDialog}
import scalafx.scene.layout.{ColumnConstraints, GridPane, RowConstraints, StackPane, VBox}
import scalafx.collections.ObservableBuffer
import scalafx.scene.Node
import scalafx.scene.chart.PieChart
import scalafx.scene.control.Alert.AlertType

/** The card grid with the cards make up the center of the rootpane. They are there in case the user
 * wants to visualize data of specific stocks or portfolios. There are four cads where charts or info
 * cards can be inserted. When the user is done with a chart, the user can close the chart and go back to
 * the original state. */

class Card:
  
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
  private def closeWrapper(content: Node, targetCard: StackPane): StackPane =
    val wrapper = new StackPane()
    wrapper.setStyle("-fx-background-color: white; -fx-border-color: #d3d3d3; -fx-border-width: 1px;")

    val closeButton = new Button("\u2716")
    closeButton.setStyle("-fx-background-color: transparent;")
    closeButton.setOnAction(_ =>
      val insertButton = new Button("Insert")
      insertButton.setOnAction(_ => showSelectionDialog(targetCard))
      targetCard.getChildren.setAll(insertButton))

    StackPane.setMargin(closeButton, Insets(5))
    StackPane.setAlignment(closeButton, Pos.TopRight)

    wrapper.getChildren.addAll(content, closeButton)
    wrapper
    
  /** Create the four cards */
  private val card1 = createCard("Insert")
  private val card2 = createCard("Insert")
  private val card3 = createCard("Insert")
  private val card4 = createCard("Insert")

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

    val columnChart = new ButtonType("Column Chart")
    val infoCard = new ButtonType("Information Card")
    val pieChart = new ButtonType("Pie Chart")
    val scatterPlot = new ButtonType("Scatter Plot")

    dialog.getDialogPane.getButtonTypes.addAll(columnChart, infoCard, pieChart, scatterPlot, ButtonType.Cancel)

    /** Different methods get called based on the users choice */
    dialog.showAndWait() match
      case Some(`columnChart`) => columnChartDialog("Enter stock ticker:", targetCard)
      case Some(`infoCard`) => infoSelectionDialog("Select Portfolio", targetCard)
      case Some(`pieChart`) => pieSelectionDialog("Select Portfolio", targetCard)
      case Some(`scatterPlot`) => scatterDialog("Select Portfolio", targetCard)
      case _ =>

  /** When the user selects 'Column Chart', a new dialog pops upo. The user inserts a stock ticker 
   * and chooses a color from the ColorPicker. The method uses the inserted stock ticker and color 
   * as parameters when creating the new Columnchart */
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
      case _ =>

  /** When the user selects 'Information Card', a new dialog pops upo. The user chooses from a dropdown
   * a portfolio. The method uses the selected portfolio's name as parameter when calling on the
   * PortfolioInfo class*/
  def infoSelectionDialog(title: String, targetCard: StackPane) =
    val dialog = new Dialog[String]()
    dialog.setTitle(title)
    dialog.setWidth(200)
    val portfolioChoice = new ChoiceBox[String]()
    portfolioChoice.items = getPortfolioNames

    dialog.getDialogPane.setContent(portfolioChoice)
    dialog.getDialogPane.getButtonTypes.add(ButtonType.OK)

    dialog.showAndWait() match
      case Some(ButtonType.OK) =>
        val selectedPortfolio = portfolioChoice.getValue
        PortfolioManager.getPortfolio(selectedPortfolio) match
          case Some(portfolio) if portfolio.stocks.nonEmpty =>
            val portfolioInfoCard = new Portfolioinfo(selectedPortfolio)
            targetCard.getChildren.setAll(closeWrapper(portfolioInfoCard.infoCard, targetCard))
          case Some(_) =>
            new Alert(AlertType.Error, s"Portfolio '$selectedPortfolio' is empty!").showAndWait()

  /** When the user selects 'Pie Chart', a new dialog pops up. The user chooses from a dropdown
   * a portfolio. The method uses the selected portfolio's name as parameter when calling on the
   * Piechart class */
  def pieSelectionDialog(title: String, targetCard: StackPane) =
    val dialog = new Dialog[String]()
    dialog.setTitle(title)
    dialog.setWidth(200)
    val portfolioChoice = new ChoiceBox[String]()

    portfolioChoice.items = getPortfolioNames

    dialog.getDialogPane.setContent(portfolioChoice)
    dialog.getDialogPane.getButtonTypes.add(ButtonType.OK)

    dialog.showAndWait() match
      case Some(ButtonType.OK) =>
        val selectedPortfolio = portfolioChoice.getValue
        PortfolioManager.getPortfolio(selectedPortfolio) match
          case Some(portfolio) if portfolio.stocks.nonEmpty =>
            val pieChartVisual = new Piechart(selectedPortfolio)
            targetCard.getChildren.setAll(closeWrapper(pieChartVisual.chart, targetCard))
          case Some(_) =>
            new Alert(AlertType.Error, s"Portfolio '$selectedPortfolio' is empty!").showAndWait()

  def scatterDialog(title: String, targetCard: StackPane) =
    val dialog = new Dialog[String]()
    dialog.setTitle("Select Portfolio and Color")
    dialog.setWidth(200)
    val portfolioChoice = new ChoiceBox[String]()

    portfolioChoice.items = getPortfolioNames
    
    dialog.getDialogPane.setContent(portfolioChoice)
    dialog.getDialogPane.getButtonTypes.add(ButtonType.OK)
    dialog.showAndWait() match
      case Some(ButtonType.OK) =>
        val selectedPortfolio = portfolioChoice.getValue
        PortfolioManager.getPortfolio(selectedPortfolio) match
          case Some(portfolio) if portfolio.stocks.nonEmpty =>
            val scatterVisual = new Scatterplot(selectedPortfolio)
            targetCard.getChildren.setAll(closeWrapper(scatterVisual.getNode, targetCard))
          case Some(_) =>
            new Alert(AlertType.Error, s"Portfolio '$selectedPortfolio' is empty!").showAndWait()


