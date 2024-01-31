package org.meinsweeper

class Cell(var row: Int, var col: Int) {
    var hasMine : Boolean = false
    var isFlagged : Boolean = false
    var isExposed : Boolean = false
    var adjacentMines : Int = -1
}
