package org.meinsweeper

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StartPage : AppCompatActivity(){

    private val easyGame : IntArray = intArrayOf(9,9,10)
    private val medGame : IntArray = intArrayOf(16,16,40)
    private val expertGame : IntArray = intArrayOf(16,30,99)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_settings)
    }
    fun newGameEasy(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("gameSettings",easyGame)    //{9,9,10}}

        startActivity(intent)
    }

    fun newGameMed(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("gameSettings",medGame)     //{16,16,40}

        startActivity(intent)
    }
    fun newGameExpert(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("gameSettings", expertGame) //{16,30,99}

        startActivity(intent)
    }

    fun helpAlert(view: View) {
        val helpDialog = AlertDialog.Builder(this,R.style.AlertDialogTitle)
        helpDialog.setTitle(R.string.help)
        val messageContent = TextView(ContextThemeWrapper(this,R.style.AlertDialogContent))
        messageContent.text = getString(R.string.helpText)
        helpDialog.setView(messageContent)
        helpDialog.setPositiveButton(R.string.ok){
                dialog, _ ->
            dialog.dismiss()
        }
        helpDialog.show()
    }

}