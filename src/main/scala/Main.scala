import scalafx.scene.layout._
import scalafx.Includes._
import scalafx.scene._
import scalafx.scene.control.{Alert, Button, ChoiceDialog, ComboBox, Label, Menu, MenuBar, MenuItem, ScrollPane, Slider, SplitPane, Tab, TabPane, TableView, TextField, TextInputDialog, Tooltip, Dialog, DatePicker}
import scalafx.stage.{Modality, Popup, Stage}
import scalafx.scene.{Node, Scene, control}
import scalafx.application.JFXApp3
import scalafx.event.ActionEvent
import scalafx.geometry.{HPos, Insets, Pos, VPos}
import scalafx.scene.layout.{BorderPane, HBox, GridPane}
import scalafx.Includes.eventClosureWrapperWithParam
import scalafx.collections.ObservableBuffer
import scalafx.event.EventIncludes.eventClosureWrapperWithParam
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import Visuals.Card

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

    val sidebar = new VBox()
    sidebar.setPrefWidth(200)
    sidebar.setMaxWidth(200)
    sidebar.setStyle("-fx-background-color: #ececec")
    rootPane.left = sidebar

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
          portfolioLabel.style = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 5px;"

          val items = ObservableBuffer[String]()
          val portfolioDropdown = new ComboBox[String](items):
            promptText = "Stocks"
            prefWidth = sidebar.width.value - 30

          val addStockButton = new Button("+"):
            style = "-fx-font-size: 10px; -fx-padding: 7px 9px;"

          val stocksContainer = new HBox:
            maxWidth = sidebar.width.value
            children = Seq(portfolioDropdown, addStockButton)

          val portfolioContainer = new VBox:
            maxWidth = sidebar.width.value
            style = "-fx-border-color: #d3d3d3; -fx-border-width: 2px; -fx-margin-top: 50px"
            children = Seq(portfolioLabel, stocksContainer)

          addStockButton.setOnAction(_ =>
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
            dialog.dialogPane().buttonTypes = Seq(javafx.scene.control.ButtonType.OK, javafx.scene.control.ButtonType.CANCEL)

            dialog.showAndWait()

            // For now just add the ticker to maintain original behavior
            if (tickerField.text.value.nonEmpty && !items.contains(tickerField.text.value)) then
              items += tickerField.text.value
          )

          sidebar.children.add(portfolioContainer)

        case None =>
          println("Portfolio creation cancelled.")

/** ********************************************************************************************
 * /**Event handling**/
 * ******************************************************************************************** */

    createPortfolio.onAction = (e: ActionEvent) => newPortfolio()

  end start
end Main