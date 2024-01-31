package org.meinsweeper

import kotlin.random.Random

class GameManager(val rows: Int, val cols: Int, val mineCount: Int) {
    var mineDisplayCount: Int = mineCount
    var isFirstRound: Boolean = true
    var gameGrid: Array<Array<Cell?>> = Array(rows) {arrayOfNulls(cols) }
    var remainingCells: MutableList<Cell> = mutableListOf<Cell>()

    fun startGame(){
        remainingCells.clear()
        mineDisplayCount = mineCount
        isFirstRound = true

        for (row in 0 until rows) {
            for(col in 0 until cols){
                gameGrid[row][col] = Cell(row,col)
                remainingCells.add(gameGrid[row][col]!!)
            }
        }

        val rand = Random
        for (i in 0 until mineCount){
            placeMine(rand)
        }
        for (row in 0 until rows) {
            for(col in 0 until cols){
                calcAdjacentMines(gameGrid[row][col]!!)
            }
        }
    }

    fun firstClickMine(cell:Cell){
        placeMine(Random)
        cell.hasMine = false
        remainingCells.add(cell)

        for (row in 0 until rows) {
            for(col in 0 until cols){
                calcAdjacentMines(gameGrid[row][col]!!)
            }
        }

    }

    private fun placeMine(rand: Random) {
        while (true) {
            val row = rand.nextInt(rows)
            val col = rand.nextInt(cols)

            if (!gameGrid[row][col]!!.hasMine){
                gameGrid[row][col]!!.hasMine = true
                remainingCells.remove(gameGrid[row][col])
                break
            }
        }
    }

    private fun calcAdjacentMines(cell: Cell){
        if (cell.hasMine){
            return
        }
        val row = cell.row
        val col = cell.col
        var value = 0

        for( i in row-1..row+1){
            for (j in col-1 .. col+1)
                if (i in 0 until rows && j in 0 until cols){
                    if(gameGrid[i][j]!!.hasMine){
                        value++
                    }
                }
        }

        cell.adjacentMines = value
    }

    fun allMinesFlagged(cell: Cell): Boolean{

        if(!cell.isExposed || cell.adjacentMines == 0) //cell hasn't been revealed yet
            return false

        var adjacentFlags = 0;

        for( i in cell.row-1..cell.row+1){
            for (j in cell.col-1 .. cell.col+1)
                if (i in 0 until rows && j in 0 until cols){
                    if(gameGrid[i][j]!!.isFlagged){
                        adjacentFlags++
                    }
                }
        }
        //return true if adjacent the number of adjacent flags equals the number of adjacent mines
        return adjacentFlags == cell.adjacentMines
    }

}