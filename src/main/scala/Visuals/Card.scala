
package Visuals

import Data.PortfolioManager
import scalafx.geometry.Insets
import scalafx.scene.control.{Alert, Button, ButtonType, ChoiceBox, ColorPicker, Dialog, TextField, TextInputDialog}
import scalafx.scene.layout.{ColumnConstraints, GridPane, RowConstraints, StackPane, VBox}
import scalafx.collections.ObservableBuffer
import scalafx.scene.Node
import scalafx.scene.chart.PieChart
import scalafx.scene.control.Alert.AlertType

class Card:
  val cardGrid = new GridPane()
  cardGrid.setHgap(10)
  cardGrid.setVgap(10)
  cardGrid.setPadding(Insets(10))

  private val col1 = new ColumnConstraints()
  col1.setPercentWidth(50)
  private val col2 = new ColumnConstraints()
  col2.setPercentWidth(50)

  private val row1 = new RowConstraints()
  row1.setPercentHeight(50)
  private val row2 = new RowConstraints()
  row2.setPercentHeight(50)

  cardGrid.getColumnConstraints.addAll(col1, col2)
  cardGrid.getRowConstraints.addAll(row1, row2)

  private def createCard(text: String) =
    val card = new StackPane()
    card.setStyle("-fx-background-color: white; -fx-border-color: #d3d3d3; -fx-border-width: 1px;")
    card.setPrefSize(400, 400)
    val button = new Button(text)
    button.setOnAction(_ => showSelectionDialog(card))
    card.getChildren.add(button)
    card

  /** creating the four cards */
  private val card1 = createCard("Insert 1")
  private val card2 = createCard("Insert 2")
  private val card3 = createCard("Insert 3")
  private val card4 = createCard("Insert 4")

  cardGrid.add(card1, 0, 0)
  cardGrid.add(card2, 1, 0)
  cardGrid.add(card3, 0, 1)
  cardGrid.add(card4, 1, 1)


/** gets teh portfolio names so that they can ba displayed */
  def getPortfolioNames: ObservableBuffer[String] =
    ObservableBuffer.from(PortfolioManager.getAllPortfolios.keys.toSeq)

  def showSelectionDialog(targetCard: StackPane) =
    val dialog = new Dialog[String]()
    dialog.setTitle("Select Display Type")

    val columnChart = new ButtonType("Column Chart")
    val infoCard = new ButtonType("Information Card")
    val pieChart = new ButtonType("Pie Chart")
    val scatterPlot = new ButtonType("Scatter Plot")

    dialog.getDialogPane.getButtonTypes.addAll(columnChart, infoCard, pieChart, scatterPlot, ButtonType.Cancel)

    dialog.showAndWait() match
      case Some(`columnChart`) => columnChartDialog("Enter stock ticker:", targetCard)
      case Some(`infoCard`) => infoSelectionDialog("Select Portfolio", targetCard)
      case Some(`pieChart`) => pieSelectionDialog("Select Portfolio", targetCard)
      case Some(`scatterPlot`) => scatterDialog()
      case _ =>

  def columnChartDialog(title: String, targetCard: StackPane) =
    /**val textInputDialog = new TextInputDialog()
    textInputDialog.setTitle("Column Chart")
    textInputDialog.setHeaderText(title)**/

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
        targetCard.getChildren.setAll(columnChartVisual.getNode)
      case _ =>

  def infoSelectionDialog(title: String, targetCard: StackPane) =
    val dialog = new Dialog[String]()
    dialog.setTitle(title)
    dialog.setWidth(200)
    val portfolioChoice = new ChoiceBox[String]()
    portfolioChoice.items = getPortfolioNames

    dialog.getDialogPane.setContent(portfolioChoice)
    dialog.getDialogPane.getButtonTypes.add(ButtonType.OK)

    dialog.showAndWait() match
      case Some(_) =>
        val selected = portfolioChoice.getValue
        if selected != null && selected.nonEmpty then
          val portfolioInfoCard = new Portfolioinfo(selected)
          targetCard.getChildren.setAll(portfolioInfoCard.infoCard)
      case _ =>


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
            targetCard.getChildren.setAll(pieChartVisual.chart)
          case Some(_) =>
            new Alert(AlertType.Error, s"Portfolio '$selectedPortfolio' is empty!").showAndWait()

  def scatterDialog() =
    val dialog = new Dialog[String]()
    dialog.setTitle("Select Portfolio and Color")
    dialog.setWidth(200)
    val portfolioChoice = new ChoiceBox[String]()

    portfolioChoice.items = getPortfolioNames

    val scatterColor = new ColorPicker()

    val vbox = new VBox(10, portfolioChoice, scatterColor)
    dialog.getDialogPane.setContent(vbox)
    dialog.getDialogPane.getButtonTypes.add(ButtonType.OK)
    dialog.showAndWait() match
      case Some(portfolio) =>
        val selectedPortfolio = portfolioChoice.value
        val selectedColor = scatterColor.value.toString