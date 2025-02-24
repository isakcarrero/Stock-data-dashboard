import scalafx.application.JFXApp3
import scalafx.scene.layout._
import scalafx.scene._
import scalafx.scene.control.{Menu, MenuBar, MenuItem}


object Main extends JFXApp3:

  def start() =

    stage = new JFXApp3.PrimaryStage:
      title = "MyStocks.com"
      width = 950
      height = 700

    val rootPane = BorderPane()

    val scene = Scene(parent = rootPane)
    stage.scene = scene

    /**The menu bar**/
    val menu = new MenuBar

    val menuFiles = Menu("File")
    val newFile = new MenuItem("New")
    val importFile = new MenuItem("Import")
    val exportFile = new MenuItem("Export")
    menuFiles.items = List(newFile, importFile, exportFile)

    val createPortfolio = Menu("Create Portfolio")

    val help = Menu("Help")
    val addStock = new MenuItem("Adding a stock")
    val removeStock = new MenuItem("Removing a stock")
    val addChart = new MenuItem("Adding a chart")
    help.items = List(addStock, removeStock, addChart)

    menu.menus = List(menuFiles, createPortfolio, help)

    rootPane.top = menu


  end start

end Main

