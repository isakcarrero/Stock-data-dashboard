package Visuals

import scalafx.geometry.Insets
import scalafx.scene.control.Button
import scalafx.scene.layout.{ColumnConstraints, GridPane, RowConstraints, StackPane, VBox}
import scalafx.scene.shape.Rectangle


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
      val label = new Button(text)
      card.getChildren.add(label)
      card
    
    val card1 = createCard("Insert 1")
    val card2 = createCard("Insert 2")
    val card3 = createCard("Insert 3")
    val card4 = createCard("Insert 4")
    
    cardGrid.add(card1, 0, 0)
    cardGrid.add(card2, 1, 0)
    cardGrid.add(card3, 0, 1)
    cardGrid.add(card4, 1, 1)
  
  
  