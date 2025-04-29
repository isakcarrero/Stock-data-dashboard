import Data.PortfolioManager.{getAllPortfolios, getPortfolio}
import Visuals.{Card, Columnchart, Piechart, Portfolioinfo, Scatterplot}
import Data.{PortfolioManager, StockData}
import scalafx.scene.layout.*
import scalafx.Includes.*
import scalafx.scene.*
import scalafx.scene.control.*
import scalafx.application.JFXApp3
import scalafx.event.ActionEvent
import scalafx.geometry.*
import scalafx.collections.ObservableBuffer
import scalafx.event.EventIncludes
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.shape.Polygon
import scalafx.stage.{FileChooser, Popup, Window}

import java.time.format.DateTimeFormatter
import java.io.{File, PrintWriter}
import scala.collection.mutable
import scala.io.Source
import scala.util.*
import scala.collection.mutable.Map

object Main extends JFXApp3:

  def start(): Unit =

    stage = new JFXApp3.PrimaryStage:
      title = "MyStocks.com"
      maxWidth = 950
      maxHeight = 700

    val rootPane = BorderPane()
    val scene = Scene(parent = rootPane)

    stage.scene = scene

    /** ********************************************************************************************
     * /**The menubar and the different options (File, Portfolio, Help), as well as their suboptions**/
     * ******************************************************************************************** */

    val menu = new MenuBar
    val menuFiles = Menu("File")
    val importFile = new MenuItem("Import")
    val exportFile = new MenuItem("Export")
    menuFiles.items = List(importFile, exportFile)

    val portfolio = Menu("Portfolio")
    val createPortfolio = new MenuItem("Create portfolio")
    portfolio.items = List(createPortfolio)

    val help = Menu("Help")
    val addPortfolio = new MenuItem("Adding a portfolio")
    val addStock = new MenuItem("Adding a stock")
    val removePortfolio = new MenuItem("Removing a portfolio")
    val addChart = new MenuItem("Adding a chart")
    help.items = List(addPortfolio, addStock, removePortfolio, addChart)

    menu.menus = List(menuFiles, portfolio, help)
    rootPane.top = menu

     /**The help text in the menu bar**/
     /********************************************************************************************/
    val addPortfolioText =
      "Adding a portfolio: "
        + "\n1: Press the button named 'Portfolio'."
        + "\n2: Press 'Create portfolio'."
        + "\n3: Enter a unique name for your new portfolio."

    val addStockText =
      "Adding a stock to your portfolio: "
        + "\n1: Press the '+' sign next to the portfolio to which you wan to adda a stock "
        + "\n2: Insert the stock symbol (if you don't know it, google it!)"
        + "\n3: Insert the amount you have bought adn the price per share."
        + "\n4: Finally choose the date when it was bought form the date picker"


    val removePortfolioText =
      "Removing a portfolio "
        + "\n1: Press the button with red 'Del' text on it "
        + "\n2: Press OK on the alert message"

    val addChartText =
      "Inserting chart or data: "
        + "\n1: Press the 'Insert' button on the card where you wan the data."
        + "\n2: Choose what you want to display."
        + "\n3: Finally insert/choose the asked data"

    /** ********************************************************************************************
     * /**Sidebar and card grid**/
     * ******************************************************************************************** */

    val sidebarContent = new VBox()
    sidebarContent.setStyle("-fx-background-color: #ececec")
    sidebarContent.spacing = 5

    val scrollPane = new ScrollPane:
      content = sidebarContent
      fitToWidth = true
      hbarPolicy = ScrollPane.ScrollBarPolicy.Never
      style = "-fx-background: #ececec; -fx-border-color: #ececec"

    scrollPane.setPrefWidth(200)
    scrollPane.setMaxWidth(200)
    rootPane.left = scrollPane

    rootPane.center = Card.cardGrid

    /** ********************************************************************************************
     * /**Menubar methods**/
     * ******************************************************************************************** */

    /** For creating a new portfolio */
    def newPortfolio(): Unit =
      val dialog = new TextInputDialog():
        title = "Name Your Portfolio"
        headerText = "Enter the name of your new portfolio:"
        contentText = "Portfolio Name:"
      val result = dialog.showAndWait()

      result match
        case Some(name) =>
          if name.isEmpty then
            new Alert(Alert.AlertType.Error, "Portfolio name has to be non-empty.").showAndWait()
          else if PortfolioManager.createPortfolio(name) then
            createPortfolioSB(name)
          else
            new Alert(Alert.AlertType.Error, s"Portfolio '$name' already exists!").showAndWait()
        case None => ()

    def displayHelpText(text: String): Unit =
      val label = new Label(text):
        style = "-fx-background-color: #ADD8E6; -fx-padding: 15px;-fx-border-color: #4682B4;-fx-border-width: 2px; " +
          "-fx-font-size: 14px;"

      val popup = new Popup()
      popup.content.add(label)
      popup.autoHide = true
      popup.show(stage)
      popup.x = (stage.width.value - label.width.value) / 2
      popup.y = (stage.height.value - label.height.value) / 2
    /** ********************************************************************************************
     * /* Method for creating portfolio and adding stock to portfolios**/
     * ******************************************************************************************** */

    def createPortfolioSB(name: String, stocks: Seq[StockData] = Seq()) =
      val portfolioLabel = new Label(name)
      portfolioLabel.style = "-fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 5px;"

      val addStockButton = new Button("+"):
        style = "-fx-font-size: 9px; -fx-font-weight: bold;"
        tooltip = Tooltip("Add Stocks")

      var isExpanded = false

      val arrowButton = new Button():
        style = "-fx-font-size: 9px; -fx-font-weight: bold;"
        tooltip = Tooltip("View/Hide Stocks")
        graphic = new Polygon:
          points.addAll(0.0, 0.0, 8.0, 0.0, 4.0, 6.0)
          style = "-fx-fill: black;"

      val deleteButton = new Button("Del"):
        style = "-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: red;"
        tooltip = Tooltip("Delete Portfolio")

      val portfolioHeader = new HBox:
        spacing = 2
        children = Seq(portfolioLabel, new Region {hgrow = Priority.Always}, arrowButton, addStockButton, deleteButton)
        style = "-fx-alignment: center-left; -fx-padding: 3px;"

      val stockList = new VBox:
        spacing = 3
        style = "-fx-padding: 5px; -fx-background-color: white; -fx-border-color: #d3d3d3; -fx-border-width: 1px;"
        visible = false
        managed = false

      val portfolioContainer = new VBox:
        maxWidth = 200
        prefHeight = 40
        style = "-fx-border-color: #d3d3d3; -fx-border-width: 2px; -fx-background-color: white; -fx-border-radius: 3px;"
        children = Seq(portfolioHeader, stockList)

      /** for viewing stocks in portfolio */
      arrowButton.onAction = _ =>
        isExpanded = !isExpanded
        stockList.visible = isExpanded
        stockList.managed = isExpanded

      /** for deleting a portfolio */
      deleteButton.onAction = _ =>
        val alert = new Alert(Alert.AlertType.Confirmation):
          title = "Delete Portfolio"
          headerText = s"Delete portfolio '$name'?"
          contentText = "This will permanently delete the portfolio and all its stocks."

        alert.showAndWait() match
          case Some(ButtonType.OK) =>
            if PortfolioManager.removePortfolio(name) then
              sidebarContent.children.remove(portfolioContainer)
          case _ => ()

      /** for adding stocks to portfolio */
      def addStock(stock: StockData) =
        val stockLabel = new Label(stock.ticker):
          style = "-fx-padding: 3px; -fx-font-size: 10px;"
        val shareInfo = new Label(f"${stock.amount} shares @ $$${stock.price}%.2f"):
          style = "-fx-padding: 3px; -fx-font-size: 8px;"
        val stockEntry = new HBox:
          spacing = 10
          children = Seq(stockLabel, new Region {
            hgrow = Priority.Always
          }, shareInfo)
        stockList.children.add(stockEntry)

      def openAddStockDialog() =
        val dialog = new Dialog():
          title = s"Add Stock to $name"
          headerText = "Enter stock details"

        /** contents in the dialog */
        val tickerField = new TextField():
          promptText = "Stock Ticker (e.g., AAPL)"
        val sharesField = new TextField():
          promptText = "Number of Shares"
        val priceField = new TextField():
          promptText = "Price per Share"
        val datePicker = new DatePicker()

        val grid = new GridPane():
          hgap = 10
          vgap = 10
          padding = Insets(20)
          add(new Label("Ticker:"), 0, 0)
          add(tickerField, 1, 0)
          add(new Label("Shares:"), 0, 1)
          add(sharesField, 1, 1)
          add(new Label("Price:"), 0, 2)
          add(priceField, 1, 2)
          add(new Label("Date:"), 0, 3)
          add(datePicker, 1, 3)

        dialog.dialogPane().content = grid
        dialog.dialogPane().buttonTypes = Seq(ButtonType.Cancel, ButtonType.OK)

        dialog.showAndWait() match
          case Some(ButtonType.OK) =>
            val tickerVal = tickerField.text.value
            val sharesVal = sharesField.text.value
            val priceVal = priceField.text.value

            /** checks that all conditions are fulliflled */
            if !(tickerVal.nonEmpty && sharesVal.nonEmpty && priceVal.nonEmpty &&
              datePicker.getValue != null && tickerVal.matches("^[A-Z]{1,5}$") &&
              Try(sharesVal.toInt > 0).getOrElse(false) && Try(priceVal.toDouble > 0).getOrElse(false)) then

              new Alert(Alert.AlertType.Error,
                "Invalid input. Please ensure:\n\n" +
                  "• Valid ticker that is 1-5 uppercase letters\n" +
                  "• Shares is a positive whole number\n" +
                  "• Price is a positive number\n" +
                  "• All fields are filled\n" +
                  "• Date is selected"
              ).showAndWait()
            else
              val stock = StockData(
                ticker = tickerVal,
                amount = sharesVal.toInt,
                price = priceVal.toDouble,
                date = datePicker.getValue.format(DateTimeFormatter.ISO_DATE))

              if PortfolioManager.addStockToPortfolio(name, stock) then
                addStock(stock)
                if !isExpanded then arrowButton.fire()
              else
                new Alert(Alert.AlertType.Error, s"Failed to add stock to portfolio '$name'").showAndWait()

          case _ => ()


      addStockButton.onAction = _ => openAddStockDialog()

      /** When loading data onto the dashboard this gets used */
      for stock <- stocks do addStock(stock)

      sidebarContent.children.add(portfolioContainer)

    /** ********************************************************************************************
     * /**Methods for saving and loading data form and to the dashboard**/
     * ******************************************************************************************** */

    /** this function saved the dashboard data and writes it into a CSV format file */
    def saveData(window: Window): Unit =
      val chooser = new FileChooser:
        title = "Export Data"
        extensionFilters.add(FileChooser.ExtensionFilter("CSV Files", "*.csv"))
      val file = chooser.showSaveDialog(window)
      if file != null then
        val writer = PrintWriter(file)
        
        /** First it prints all the stock and portfolio information form PortfolioManager*/
        writer.println("portfolio,ticker,date,price,amount")
        val allData = PortfolioManager.getAllPortfolios
        for (portfolioName, portfolio) <- allData do
          for stock <- portfolio.stocks do
            writer.println(s"$portfolioName,${stock.ticker},${stock.date},${stock.price},${stock.amount}")

        /** Then it prints the information of the cards current states from the Card object */
        writer.println("chartType,portOrStock,color")
        for (i <- Card.cardStates.indices) do
          val cardState = Card.cardStates(i)
          val portOrStockStr = cardState.portOrStock match
            case list: List[String] =>
              if cardState.chartType == "scatterPlot" then list.mkString("|")
              else list.mkString(",")
            case other => other.toString
          writer.println(s"${cardState.chartType},$portOrStockStr,${cardState.color}")
        writer.close()

    /** Method for loading data. The method uses a csv file form the users files to display a
     * saved dasboard. */
    def loadData(window: Window): Unit =
      val chooser = new FileChooser:
        title = "CSV File"
        extensionFilters.add(FileChooser.ExtensionFilter("CSV Files", "*.csv"))

      val file = chooser.showOpenDialog(window)
      if file != null then
        /** An alert that informs the user that loading the file will remove current state of dashboard */
        val alert = new Alert(AlertType.Warning)
        alert.setTitle("Warning")
        alert.setHeaderText("Loading this file will remove the current state of the dashboard.")
        alert.setContentText("Do you want to proceed?")

        val result = alert.showAndWait()
        if result.get == ButtonType.OK then
          val lines = Source.fromFile(file).getLines().toList
  
          /** We start by splitting upp the portfolio and chart data */
          val separatorIndex = lines.indexWhere(_.startsWith("chartType,portOrStock,color"))
          val (portfolioLines, chartLines) = lines.splitAt(separatorIndex)
  
          /** Clearing sidebar and portfolio content */
          val dataLines = portfolioLines.tail
          PortfolioManager.clearAllPortfolios()
          sidebarContent.children.clear()
  
          /** for storing the stocks */
          val portfolioStocks = mutable.Map[String, List[StockData]]().withDefaultValue(Nil)
  
          /** getting the data for each portfolio and storing it in our map */
          for line <- dataLines do
            val cols = line.split(",").map(_.trim)
            if cols.length == 5 then
              val portfolioName = cols(0)
              val ticker = cols(1)
              val date = cols(2)
              val price = cols(3).toDouble
              val amount = cols(4).toInt
              val stock = StockData(ticker, amount, price, date)
              portfolioStocks(portfolioName) = stock :: portfolioStocks(portfolioName)
  
          /** Creating each portfolio and adding the stocks to it */
          for (name, stocks) <- portfolioStocks do
            PortfolioManager.createPortfolio(name)
            stocks.foreach(stock => PortfolioManager.addStockToPortfolio(name, stock))
            createPortfolioSB(name, stocks.reverse)
  
          /** Now we load the chart data */
          val chartConfigLines = chartLines.tail
          val cards = List(Card.card1, Card.card2, Card.card3, Card.card4)
  
          /** get the information for each row in our csv file and check what chart
           * it is and display it based on that */
          for (i <- 0 until 4) do
            if i < chartConfigLines.length then
              val cols = chartConfigLines(i).split(",", -1).map(_.trim)
  
              if cols.length >= 3 && cols(0).nonEmpty then
                val chartType = cols(0)
                val portOrStock =
                  if chartType == "scatterPlot" && cols(1).contains("|") then
                    cols(1).split("\\|").toList
                  else if cols(1).contains(",") then
                    cols(1).split(",").toList
                  else if cols(1).nonEmpty then
                    cols(1)
                  else
                    ""
                val color = if cols.length >= 3 then cols(2) else ""
  
                /** We update the card states */
                Card.cardStates(i) = Card.CardState(chartType, portOrStock, color)
  
                /** And then we can restore the data and create the charts */
                chartType match
                  case "ColumnChart" =>
                    if portOrStock.toString.nonEmpty then
                      val columnChartVisual = new Columnchart(portOrStock.toString, color)
                      cards(i).getChildren.setAll(Card.closeWrapper(columnChartVisual.getNode, cards(i)))
  
                  case "pieChart" =>
                    if portOrStock.toString.nonEmpty then
                      val pieChartVisual = new Piechart(portOrStock.toString)
                      cards(i).getChildren.setAll(Card.closeWrapper(pieChartVisual.chart, cards(i)))
  
                  case "scatterPlot" =>
                    println(portOrStock)
                    if portOrStock.toString.nonEmpty then
                      val scatterPlotVisual = portOrStock match
                        case list: List[String] => new Scatterplot(list.head)
                        case single: String => new Scatterplot(single)
  
                      portOrStock match
                        case list: List[String] => list.tail.foreach(scatterPlotVisual.displayedPortfolios += _)
                        case _ =>
  
                      scatterPlotVisual.onPortfoliosAdd = portfolios =>
                        Card.cardStates(i) = Card.CardState("scatterPlot", portfolios)
                      scatterPlotVisual.updateChart()
                      cards(i).getChildren.setAll(Card.closeWrapper(scatterPlotVisual.getNode, cards(i)))
  
                  case "infoCard" =>
                    if portOrStock.toString.nonEmpty then
                      val infoCardVisual = new Portfolioinfo(portOrStock.toString)
                      cards(i).getChildren.setAll(Card.closeWrapper(infoCardVisual.infoCard, cards(i)))
  
                  case _ =>
  
              else
                /** resetting of cards that don't have content according to the csv file */
                val insertButton = new Button("Insert")
                insertButton.setOnAction(_ => Card.showSelectionDialog(cards(i)))
                cards(i).getChildren.setAll(insertButton)
                Card.cardStates(i) = Card.CardState()

    /** ********************************************************************************************
     * /**Event handling**/
     * ******************************************************************************************** */

    /** in the menu bar */
    createPortfolio.onAction = (e: ActionEvent) =>
      newPortfolio()
    addPortfolio.onAction = (e: ActionEvent) => displayHelpText(addPortfolioText)
    addStock.onAction = (e: ActionEvent) => displayHelpText(addStockText)
    removePortfolio.onAction = (e: ActionEvent) => displayHelpText(removePortfolioText)
    addChart.onAction = (e: ActionEvent) => displayHelpText(addChartText)

    /** for importing and exporting */
    importFile.onAction = _ => loadData(stage)
    exportFile.onAction = _ => saveData(stage)




  end start
end Main