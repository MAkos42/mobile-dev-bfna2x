package org.meinsweeper

import kotlin.random.Random

class GameManager(private val rows: Int, private val cols: Int, private val mineCount: Int) {
    private var mineDisplayCount: Int = mineCount
    private var mineRemaining: Int = mineCount
    private var isFirstRound: Boolean = true
    private var gameGrid: Array<Array<Cell?>> = Array(rows) {arrayOfNulls(cols) }

    init{
        for (y in 0 until rows) {
            for(x in 0 until cols){
                gameGrid[y][x] = Cell(x,y)
            }
        }

        val rand = Random
        for (i in 0 until mineCount){
            placeMine(rand)
        }
        for (y in 0 until rows) {
            for(x in 0 until cols){
                calcAdjacentMines(gameGrid[y][x]!!)
            }
        }
    }

    private fun placeMine(rand: Random) {
        while (true) {
            val y = rand.nextInt(rows)
            val x = rand.nextInt(cols)

            if (!gameGrid[y][x]!!.hasMine){
                gameGrid[y][x]!!.hasMine = true
                break
            }
        }
    }

    private fun calcAdjacentMines(cell: Cell){
        if (cell.hasMine){
            return
        }
        val x = cell.x
        val y = cell.y
        var value = 0

        for( i in y-1..y+1){
            for (j in x-1 .. x+1)
                if (i in 0 until rows && j in 0 until cols){
                    if(gameGrid[i][j]!!.hasMine){
                        value++
                    }
                }
        }

        cell.neighbouringMines = value
    }
}