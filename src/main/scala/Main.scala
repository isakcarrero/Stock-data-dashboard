import scalafx.scene.layout._
import scalafx.Includes._
import scalafx.scene._
import scalafx.scene.control._
import scalafx.scene.{Node, Scene, control}
import scalafx.application.JFXApp3
import scalafx.event.ActionEvent
import scalafx.geometry._
import scalafx.scene.layout._
import scalafx.collections.ObservableBuffer
import scalafx.event.EventIncludes.eventClosureWrapperWithParam
import Visuals.Card
import scalafx.scene.shape.Polygon

object Main extends JFXApp3:

  def start() =

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
    val newFile = new MenuItem("New")
    val importFile = new MenuItem("Import")
    val exportFile = new MenuItem("Export")
    menuFiles.items = List(newFile, importFile, exportFile)

    val portfolio = Menu("Portfolio")
    val createPortfolio = new MenuItem("Create portfolio")
    portfolio.items = List(createPortfolio)

    val help = Menu("Help")
    val addStock = new MenuItem("Adding a stock")
    val removeStock = new MenuItem("Removing a stock")
    val addChart = new MenuItem("Adding a chart")
    help.items = List(addStock, removeStock, addChart)

    menu.menus = List(menuFiles, portfolio, help)
    rootPane.top = menu

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

    rootPane.center = Card().cardGrid

    /** ********************************************************************************************
     * /**Menubar methods**/
     * ******************************************************************************************** */

    /** For creating a new fortfolio * */
    def newPortfolio(): Unit =
      val dialog = new TextInputDialog():
        title = "Name Your Portfolio"
        headerText = "Enter the name of your new portfolio:"
        contentText = "Portfolio Name:"
      val result = dialog.showAndWait()

      result match
        case Some(name) =>
          val portfolioLabel = new Label(name)
          portfolioLabel.style = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 5px;"

          val stocksInPortfolio = ObservableBuffer[String]()

          val addStockButton = new Button("+"):
            style = "-fx-font-size: 12px; -fx-font-weight: bold;"

          var isExpanded = false

          val arrowButton = new Button():
            graphic = new Polygon:
              points.addAll(0.0, 0.0, 10.0, 0.0, 5.0, 8.0)
              style = "-fx-fill: black;"

          val portfolioHeader = new HBox:
            spacing = 5
            children = Seq(portfolioLabel, new Region { hgrow = Priority.Always }, arrowButton, addStockButton)
            style = "-fx-alignment: center-left; -fx-padding: 5px;"

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

          /** for viewing stocks in fortfolio */
          arrowButton.onAction = _ =>
            isExpanded = !isExpanded
            stockList.visible = isExpanded
            stockList.managed = isExpanded

          /** dialog for adding stocks to portfolio */
          addStockButton.onAction = _ =>
            val dialog = new Dialog[Unit]():
              title = s"Add Stock to $name"
              headerText = "Enter stock details"

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
            dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
            dialog.showAndWait()
            
            /** currently checks for duplicas, will be changed and stocks in fortfolio has to be uppdated */
            if tickerField.text.value.nonEmpty && !stocksInPortfolio.contains(tickerField.text.value) then
              stocksInPortfolio += tickerField.text.value
            val stockLabel = new Label(tickerField.text.value):
              style = "-fx-padding: 3px; -fx-font-size: 12px;"
            val shareInfo = new Label(s"${sharesField.text.value} shares @ ${priceField.text.value}"):
              style = "-fx-padding: 3px; -fx-font-size: 10px;"
            val stockEntry = new HBox:
              spacing = 10
              children = Seq(stockLabel, new Region { hgrow = Priority.Always}, shareInfo)

            stockList.children.add(stockEntry)
            
            /**opens stocklist if it is closed**/
            if !isExpanded then arrowButton.fire()

          sidebarContent.children.add(portfolioContainer)

        case None =>
          println("Portfolio creation cancelled.")

    /** ********************************************************************************************
     * /**Event handling**/
     * ******************************************************************************************** */

    createPortfolio.onAction = (e: ActionEvent) => newPortfolio()

  end start
end Main