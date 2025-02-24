
import scalafx.scene.layout.*
import scalafx.scene.*
import scalafx.scene.control.{Alert, Button, ChoiceDialog, Label, Menu, MenuBar, MenuItem, ScrollPane, Slider, SplitPane, Tab, TabPane, TableView, TextField, TextInputDialog, Tooltip}
import scalafx.stage.{Modality, Stage}
import scalafx.scene.{Node, Scene, control}
import scalafx.application.JFXApp3
import scalafx.geometry.{HPos, Insets, VPos}
import scalafx.scene.layout.{BorderPane, HBox}


object Main extends JFXApp3:

  def start() =

    stage = new JFXApp3.PrimaryStage:
      title = "MyStocks.com"
      width = 950
      height = 700


    val rootPane = BorderPane()

    val scene = Scene(
      parent = rootPane,
      /**fill = Color.rgb(63,63,63)**/
    )

    stage.scene = scene
    /**********************************************************************************************
    /**The menubar**/
    **********************************************************************************************/

    val menu = new MenuBar
    /**menu.setStyle("-fx-background-color: #3f3f3f")**/
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
    /**Menubar methods**/
    **********************************************************************************************/

    /**********************************************************************************************
    /**Event handling**/
    **********************************************************************************************/

    /**********************************************************************************************
    /**Sidebar**/
    **********************************************************************************************/
    val sidebar = new VBox()
    sidebar.setMinWidth(200)
    sidebar.setStyle("-fx-background-color: #ececec")
    rootPane.left = sidebar



  end start

end Main

