package org.meinsweeper

class Cell(var x: Int, var y: Int) {
    var hasMine : Boolean = false
    var isFlagged : Boolean = false
    var isExposed : Boolean = false
    var neighbouringMines : Int = -1
}