
import scalafx.scene.layout.*
import scalafx.Includes.*
import scalafx.scene.*
import scalafx.scene.control.{Alert, Button, ChoiceDialog, Label, Menu, MenuBar, MenuItem, ScrollPane, Slider, SplitPane, Tab, TabPane, TableView, TextField, TextInputDialog, Tooltip}
import scalafx.stage.{Modality, Popup, Stage}
import scalafx.scene.{Node, Scene, control}
import scalafx.application.JFXApp3
import scalafx.event.ActionEvent
import scalafx.geometry.{HPos, Insets, Pos, VPos}
import scalafx.scene.layout.{BorderPane, HBox}
import scalafx.Includes.eventClosureWrapperWithParam
import scalafx.event.EventIncludes.eventClosureWrapperWithParam
import scalafx.scene.shape.Rectangle



object Main extends JFXApp3:

  def start() =

    stage = new JFXApp3.PrimaryStage:
      title = "MyStocks.com"
      width = 950
      height = 700


    val rootPane = BorderPane()
    val scene = Scene(parent = rootPane)

    stage.scene = scene




    /**********************************************************************************************
    /**The menubar and the different options (File, Portfolio, Help), as well as their suboptions**/
    **********************************************************************************************/


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

    /**********************************************************************************************
    /**Sidebar**/
    **********************************************************************************************/
    val sidebar = new VBox()
    sidebar.setPrefWidth(200)
    sidebar.setMaxWidth(200)
    sidebar.setStyle("-fx-background-color: #ececec")
    rootPane.left = sidebar

    /**********************************************************************************************
    /**Menubar methods**/
    **********************************************************************************************/

    /** For creating a new fortfolio **/
    def newPortfolio(): Unit =
      val dialog = new TextInputDialog():
        title = "Name Your Portfolio"
        headerText = "Enter the name of your new portfolio:"
        contentText = "Portfolio Name:"
      val result = dialog.showAndWait()

      result match
        case Some(name) =>
          val portfolioLabel = new Label(name)
          portfolioLabel.style = "-fx-padding: 5px;"

          val addButton = new Button("+"):
            style = "-fx-font-size: 10px; -fx-padding: 5px 7px;"

      /** Need to fix the spacing between the objects **/
          val hbox = new HBox:
            maxWidth = sidebar.width.value
            children = Seq(portfolioLabel, addButton)
            alignment = Pos.CenterRight

          val rectangle = new Rectangle:
            width = sidebar.width.value
            height = 30
            style = "-fx-fill: white; -fx-stroke: darkgray; -fx-stroke-width: 1px;"

          val stackPane = new StackPane:
            children = Seq(rectangle, hbox)
            alignment = Pos.CenterLeft
            style = "-fx-padding: 5px 0px 0px 0px;"

          sidebar.children.add(stackPane)
        case None =>
          println("Portfolio creation cancelled.")
    /**********************************************************************************************
    /**Event handling**/
    **********************************************************************************************/

    createPortfolio.onAction = (e: ActionEvent) => newPortfolio()




  end start

end Main

