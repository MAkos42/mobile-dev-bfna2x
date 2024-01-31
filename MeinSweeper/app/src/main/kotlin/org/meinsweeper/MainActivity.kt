package org.meinsweeper

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private var gameReset: Boolean = false

    private lateinit var grid : GridLayout
    private lateinit var gm : GameManager
    private lateinit var imageArray : Array<Array<ImageView>>

    private lateinit var mineCounter : TextView
    private lateinit var updateTimer : Runnable
    private lateinit var timer : TextView

    private val executor = Executors.newSingleThreadScheduledExecutor()
    private var gameTime : Int = -1

    private val cellImageArray : Array<Int> = arrayOf(
        R.drawable.none,
        R.drawable.one,
        R.drawable.two,
        R.drawable.three,
        R.drawable.four,
        R.drawable.five,
        R.drawable.six,
        R.drawable.seven,
        R.drawable.eight)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val settings = intent.getIntArrayExtra("gameSettings")!!

        gm = GameManager(settings[0],settings[1],settings[2])
        grid = findViewById(R.id.grid)
        mineCounter = findViewById(R.id.mineCounter)
        timer = findViewById(R.id.timer)


        updateTimer = Runnable {
            // Update the timer TextView with the elapsed seconds
            runOnUiThread {
                gameTime++
                timer.text = String.format("%03d", gameTime)
            }
        }

        val button = findViewById<ImageButton>(R.id.startButton)
        button.setOnClickListener{
            newGame()
        }
        button.setOnLongClickListener {
            finish()
            true
        }

        newGame()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun newGame(){

        //regenerate game board
        gm.startGame()

        //cleanup previous game
        if(gameReset) {
            grid.removeAllViews()

            imageArray.forEach { row -> //clear image array from previous game
                row.forEach { imageView ->
                    imageView.setOnClickListener(null)
                    imageView.setOnLongClickListener(null)
                    imageView.setImageDrawable(null)
                    (imageView.parent as? ViewGroup)?.removeView(imageView)
                }
            }

            System.gc()
        }

        //reset grid size and counters
        grid.rowCount = gm.rows
        grid.columnCount = gm.cols
        mineCounter.text = "${gm.mineDisplayCount}"
        timer.text = getString(R.string.zeroCount)
        gameTime = -1

        //create image grid
        imageArray = Array(gm.rows) { Array(gm.cols){ImageView(this)} }

        //populate image grid
        for(row in 0 until gm.rows) {
            for (col in 0 until gm.cols) {
                    imageArray[row][col].setImageResource(R.drawable.button) //set image
                imageArray[row][col].layoutParams = ViewGroup.LayoutParams(112, 112)

                imageArray[row][col].setOnClickListener{
                    revealCell(row,col)
                }

                imageArray[row][col].setOnLongClickListener{
                    flagCell(row,col)
                    true
                }
                //add each ImageView to the display grid
                grid.addView(imageArray[row][col])
            }
        }


        //set resetGame flag after first init
        gameReset = true
    }

    private fun flagCell(row: Int, col: Int) {
        val thisCell : Cell = gm.gameGrid[row][col]!!
        if(thisCell.isExposed){
            if(gm.allMinesFlagged(thisCell))
                revealAdjacent(row,col)
            return
        }

        thisCell.isFlagged = !thisCell.isFlagged //flip flag value

        when(thisCell.isFlagged){ //redraw cell based on new flag state
            true -> {
                gm.mineDisplayCount--
                imageArray[row][col].setImageResource(R.drawable.flag)
            }
            false -> {
                gm.mineDisplayCount++
                imageArray[row][col].setImageResource(R.drawable.button)
            }
        }
        mineCounter.text = "${gm.mineDisplayCount}"
    }


    private fun revealCell(row: Int, col: Int){

        val thisCell = gm.gameGrid[row][col]!!

        if(thisCell.isExposed)
            return

        if(thisCell.isFlagged)
            return

        if(gm.isFirstRound){
            if(thisCell.hasMine)
                gm.firstClickMine(thisCell)

            executor.scheduleAtFixedRate(updateTimer, 0, 1, TimeUnit.SECONDS)
            gm.isFirstRound = false
        }

        if(thisCell.hasMine){
            imageArray[row][col].setImageResource(R.drawable.mine)
            endGame(false)
            return
        }

        imageArray[row][col].setImageResource(cellImageArray[thisCell.adjacentMines])
        thisCell.isExposed = true
        gm.remainingCells.remove(thisCell)

        if(gm.remainingCells.isEmpty()){
            println("Game end?")
            endGame(true)
        }

        if(thisCell.adjacentMines == 0)
            revealAdjacent(row,col)

        return
    }

    private fun revealAdjacent(row: Int, col: Int) {

        for(i in row-1..row+1)
            if(i in 0 until gm.rows){
                for(j in col-1 .. col+1)
                    if(j in 0 until gm.cols){
                        val thisCell = gm.gameGrid[i][j]!!
                        if(gm.remainingCells.contains(thisCell) || (!thisCell.isFlagged && thisCell.hasMine))
                            revealCell(i,j)
                    }
            }
    }

    @SuppressLint("ResourceAsColor")
    private fun endGame(victory: Boolean) {
        executor.shutdown()

        if(victory) {
            val endDialog = AlertDialog.Builder(this,R.style.AlertDialogTitle)
            endDialog.setTitle(R.string.victory)
            val messageContent = TextView(ContextThemeWrapper(this,R.style.AlertDialogContent))
            messageContent.text = String.format(getString(R.string.endText),gm.rows,gm.cols,gm.mineCount/60,gm.mineCount%60,gameTime)
            endDialog.setView(messageContent)
            endDialog.setPositiveButton(R.string.new_game){
                dialog, _ ->
                dialog.dismiss()
                newGame()
            }

            endDialog.setNeutralButton(R.string.menu){
                    dialog, _ ->
                dialog.dismiss()
                finish()
            }

            endDialog.show()

        }
        else{
            gm.gameGrid.flatten().forEach { cell ->

                if (cell!!.hasMine) {
                    cell.isExposed = true
                    imageArray[cell.row][cell.col].setImageResource(R.drawable.mine)
                }
            }
            imageArray.flatten().forEach {
                imageView ->
                imageView.setOnClickListener(null)
                imageView.setOnLongClickListener(null)
            }
            return
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Shutdown the executor to stop the timer when the activity is destroyed
        executor.shutdown()
    }

}

