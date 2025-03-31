package Visuals

import Data.PortfolioManager
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, ChoiceBox, Dialog, TextInputDialog}
import scalafx.scene.layout.{ColumnConstraints, GridPane, RowConstraints, StackPane, VBox}
import scalafx.scene.control.ButtonType
import scalafx.collections.ObservableBuffer

class Card:
  val cardGrid = new GridPane()
  cardGrid.setHgap(10)
  cardGrid.setVgap(10)
  cardGrid.setPadding(Insets(10))

  val col1 = new ColumnConstraints()
  col1.setPercentWidth(50)
  val col2 = new ColumnConstraints()
  col2.setPercentWidth(50)

  val row1 = new RowConstraints()
  row1.setPercentHeight(50)
  val row2 = new RowConstraints()
  row2.setPercentHeight(50)

  cardGrid.getColumnConstraints.addAll(col1, col2)
  cardGrid.getRowConstraints.addAll(row1, row2)

  def createCard(text: String) =
    val card = new StackPane()
    card.setStyle("-fx-background-color: white; -fx-border-color: #d3d3d3; -fx-border-width: 1px;")
    card.setPrefSize(400, 400)
    val button = new Button(text)
    button.setOnAction(_ => showSelectionDialog())
    card.getChildren.add(button)
    card

  val card1 = createCard("Insert 1")
  val card2 = createCard("Insert 2")
  val card3 = createCard("Insert 3")
  val card4 = createCard("Insert 4")

  cardGrid.add(card1, 0, 0)
  cardGrid.add(card2, 1, 0)
  cardGrid.add(card3, 0, 1)
  cardGrid.add(card4, 1, 1)
  
  
/** gets teh portfolio namnse so that they can ba diplayed */
  def getPortfolioNames: ObservableBuffer[String] =
    ObservableBuffer.from(PortfolioManager.getAllPortfolios.keys.toSeq)

  def showSelectionDialog() =
    val dialog = new Dialog[String]()
    dialog.setTitle("Select Display Type")

    val columnChart = new ButtonType("Column Chart")
    val infoCard = new ButtonType("Information Card")
    val pieChart = new ButtonType("Pie Chart")
    val scatterPlot = new ButtonType("Scatter Plot")

    dialog.getDialogPane.getButtonTypes.addAll(columnChart, infoCard, pieChart, scatterPlot, ButtonType.Cancel)

    dialog.showAndWait() match
      case Some(`columnChart`) => columnChartDialog("Enter stock ticker:")
      case Some(`infoCard`) => portfolioSelectionDialog("Select Portfolio for Information Card")
      case Some(`pieChart`) => portfolioSelectionDialog("Select Portfolio for Pie Chart")
      case Some(`scatterPlot`) => scatterDialog()
      case _ =>

  def columnChartDialog(ticker: String) =
    val textInputDialog = new TextInputDialog()
    textInputDialog.setTitle("Input Required")
    textInputDialog.setHeaderText(ticker)
    textInputDialog.showAndWait()

  def portfolioSelectionDialog(title: String) =
    val dialog = new Dialog[String]()
    dialog.setTitle(title)
    val portfolioChoice = new ChoiceBox[String]()
    
    portfolioChoice.items = getPortfolioNames

    dialog.getDialogPane.setContent(portfolioChoice)
    dialog.getDialogPane.getButtonTypes.add(ButtonType.OK)
    dialog.showAndWait()

  def scatterDialog() =
    val dialog = new Dialog[String]()
    dialog.setTitle("Select Portfolio and Color")
    val portfolioChoice = new ChoiceBox[String]()
    
    
    portfolioChoice.items = getPortfolioNames

    val scatterColor = new ChoiceBox[String]()
    scatterColor.getItems.addAll("Green", "Red", "Blue", "Orange")

    val vbox = new VBox(10, portfolioChoice, scatterColor)
    dialog.getDialogPane.setContent(vbox)
    dialog.getDialogPane.getButtonTypes.add(ButtonType.OK)
    dialog.showAndWait()