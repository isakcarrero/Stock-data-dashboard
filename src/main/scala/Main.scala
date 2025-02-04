

object Main extends JFXApp3:

  def start() =

    stage = new JFXApp3.PrimaryStage:
      title = "UniqueProjectName"
      width = 600
      height = 450

    val root = Pane()

    val scene = Scene(parent = root)
    stage.scene = scene

    val rectangle = new Rectangle:
      x = 275
      y = 175
      width = 50
      height = 50
      fill = Blue

    root.children += rectangle

  end start

end Main

