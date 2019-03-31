package com.github.akassharjun.hotdogshowdown.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import com.github.akassharjun.hotdogshowdown.R
import com.github.akassharjun.hotdogshowdown.presenter.MainActivityPresenter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainActivityPresenter.View {

    /* VARIABLES */
    private var previousHotdogs = "0"
    private val presenter = MainActivityPresenter(this)
    private var round = ""
    private var userID = ""
    private var tableNumber = ""

    /* LIFECYCLE */
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        //setting user info
        mName.text = "${intent.getStringExtra("firstName")}\n${intent.getStringExtra("lastName").capitalize()}"
        userID = intent.getStringExtra("id")
        mId.text = "HDS#00$userID"

        presenter.initializeRecord(userID)
        showTablePickerDialog()
        mNumberOfHotdogs.text = "0"

        /* ON CLICK LISTENERS */
        mAddHotdog.setOnClickListener {
            var hotdogsEaten = Integer.parseInt(mNumberOfHotdogs.text.toString())
            hotdogsEaten++
            if (isAmountValid(hotdogsEaten)) {
                mNumberOfHotdogs.text = hotdogsEaten.toString()
                presenter.incrementHotdog(userID, round)
                presenter.updateCollection(tableNumber, hotdogsEaten.toString())
            } else {
                Toast.makeText(this, "Limit reached!", Toast.LENGTH_SHORT).show()
            }
        }

        mRemoveHotdog.setOnClickListener {
            var hotdogsEaten = Integer.parseInt(mNumberOfHotdogs.text.toString())
            hotdogsEaten--
            if (isAmountValid(hotdogsEaten)) {
                mNumberOfHotdogs.text = hotdogsEaten.toString()
                presenter.decrementHotdog(userID, round)
                presenter.updateCollection(tableNumber, hotdogsEaten.toString())
            } else {
                Toast.makeText(this, "There are no hotdogs to be removed!", Toast.LENGTH_SHORT).show()
            }
        }

        mReset.setOnClickListener {
            showConfirmationDialog("reset")
        }

        mSubmit.setOnClickListener {
            showConfirmationDialog("submit")
        }

        mRoundName.setOnClickListener {
            showRoundSelectionDialog()
        }
    }

    /* FUNCTIONS */
    private fun isAmountValid(hotdogsEaten: Int): Boolean {
        return hotdogsEaten > -1 && hotdogsEaten < 10
    }

    private fun showConfirmationDialog(type: String) {
        val view = layoutInflater.inflate(R.layout.dialog_confirmation, null)
        val confirmationDialog = android.app.AlertDialog.Builder(this@MainActivity).create()
        confirmationDialog.window!!.setBackgroundDrawable(ColorDrawable(0x00000000))

        val positive = view.findViewById<Button>(R.id.positive)
        val negative = view.findViewById<Button>(R.id.negative)
        val message = view.findViewById<TextView>(R.id.message)

        if (type == "submit") {
            message.text = getString(R.string.submit_data)
        } else {
            message.text = getString(R.string.exit_main)
        }

        positive.setOnClickListener {
            if (type == "submit") {
                val intent = Intent(this@MainActivity, ResultActivity::class.java)
                intent.putExtra("amount", mNumberOfHotdogs.text.toString())
                startActivity(intent)
            } else {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            confirmationDialog.dismiss()
        }

        negative.setOnClickListener {
            confirmationDialog.dismiss()
        }

        confirmationDialog.setCancelable(false)
        confirmationDialog.setView(view)
        confirmationDialog.show()
    }

    private fun showRoundSelectionDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_round_selection, null)
        val roundSelectionDialog = android.app.AlertDialog.Builder(this@MainActivity).create()
        roundSelectionDialog.window!!.setBackgroundDrawable(ColorDrawable(0x00000000))

        val preliminaryRound = view.findViewById<Button>(R.id.preliminaryRound)
        val finalRound = view.findViewById<Button>(R.id.finalRound)

        preliminaryRound.setOnClickListener {
            mRoundName.text = getString(R.string.preliminary_round)
            round = "FR"
            presenter.getPreviousData(userID, round, tableNumber)
            roundSelectionDialog.dismiss()
        }

        finalRound.setOnClickListener {
            mRoundName.text = getString(R.string.final_round)
            round = "LR"
            presenter.getPreviousData(userID, round, tableNumber)
            roundSelectionDialog.dismiss()
        }


        roundSelectionDialog.setCancelable(false)
        roundSelectionDialog.setView(view)
        roundSelectionDialog.show()
    }

    override fun setPreviousData(previousAmount: String) {
        mNumberOfHotdogs.text = previousAmount
        previousHotdogs = previousAmount
    }

    override fun showAPIErrorDialog(type: String) {
        val view = layoutInflater.inflate(R.layout.dialog_error, null)
        val apiErrorDialog = android.app.AlertDialog.Builder(this@MainActivity).create()
        apiErrorDialog.window!!.setBackgroundDrawable(ColorDrawable(0x00000000))

        val message = view.findViewById<TextView>(R.id.message)
        val tryAgain = view.findViewById<Button>(R.id.tryAgain)

        message.text = getString(R.string.api_error)

        tryAgain.setOnClickListener {
            when (type) {
                "increment" -> presenter.incrementHotdog(userID, round)
                "initialize" -> presenter.initializeRecord(userID)
                "decrement" -> presenter.decrementHotdog(userID, round)
                else -> presenter.getPreviousData(userID, round, tableNumber)
            }
            apiErrorDialog.dismiss()
        }

        apiErrorDialog.setCancelable(false)
        apiErrorDialog.setView(view)
        apiErrorDialog.show()
    }

    private fun showTablePickerDialog() {

        val view = layoutInflater.inflate(R.layout.dialog_table_selection, null)
        val tablePickerDialog = android.app.AlertDialog.Builder(this@MainActivity).create()
        tablePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(0x00000000))

        val listView = view.findViewById<ListView>(R.id.tableList)
        val back = view.findViewById<ImageView>(R.id.back)

        back.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        val listItems = mutableListOf<String>()

        for (i in 1..20) {
            listItems.add("Table $i")
        }

        val adapter = ArrayAdapter(this, R.layout.list_item, listItems)

        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // value of item that is clicked
            tableNumber = listView.getItemAtPosition(position) as String

            tablePickerDialog.dismiss()

            showRoundSelectionDialog()
        }

        tablePickerDialog.setCancelable(false)
        tablePickerDialog.setView(view)
        tablePickerDialog.show()


    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}



