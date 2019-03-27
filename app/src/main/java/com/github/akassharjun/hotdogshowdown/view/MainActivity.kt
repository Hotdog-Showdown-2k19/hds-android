package com.github.akassharjun.hotdogshowdown.view

import android.annotation.SuppressLint
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
    val tableNumber = ""

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

        /* ON CLICK LISTENERS */
        mAddHotdog.setOnClickListener {
            var hotdogsEaten = Integer.parseInt(mNumberOfHotdogs.text.toString())
            hotdogsEaten++
            if (isAmountValid(hotdogsEaten)) {
                mNumberOfHotdogs.text = hotdogsEaten.toString()
                presenter.updateCollection(userID, round, hotdogsEaten.toString())
            } else {
                Toast.makeText(this, "Limit reached!", Toast.LENGTH_SHORT).show()
            }
        }

        mRemoveHotdog.setOnClickListener {
            var hotdogsEaten = Integer.parseInt(mNumberOfHotdogs.text.toString())
            hotdogsEaten--
            if (isAmountValid(hotdogsEaten)) {
                mNumberOfHotdogs.text = hotdogsEaten.toString()
                presenter.updateCollection(userID, round, hotdogsEaten.toString())
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

    override fun onStart() {
        super.onStart()
        showTablePickerDialog()
    }

    /* FUNCTIONS */
    private fun isAmountValid(hotdogsEaten: Int): Boolean {
        return hotdogsEaten > -1 && hotdogsEaten < 21
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
            message.text = getString(R.string.reset_data)
        }

        positive.setOnClickListener {
            if (type == "submit") {
//                val intent = Intent(this@MainActivity, ResultActivity::class.java)
//                intent.putExtra("newAmount", mNumberOfHotdogs.text.toString())
//                intent.putExtra("previousAmount", previousHotdogs)
//                intent.putExtra("userID", userID)
//                intent.putExtra("documentID", documentID)
                startActivity(intent)
            } else {
//                mNumberOfHotdogs.text = "0"
//                previousHotdogs = "0"
//                presenter.updateCollection(userID, mRoundName.text.toString(), "0")
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
            round = "round_1"
            roundSelectionDialog.dismiss()
        }

        finalRound.setOnClickListener {
            mRoundName.text = getString(R.string.final_round)
            round = "round_2"
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

    override fun showAPIErrorDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_invalid_user, null)
        val apiErrorDialog = android.app.AlertDialog.Builder(this@MainActivity).create()
        apiErrorDialog.window!!.setBackgroundDrawable(ColorDrawable(0x00000000))

        val message = view.findViewById<TextView>(R.id.message)
        val tryAgain = view.findViewById<Button>(R.id.tryAgain)

        message.text = "There was an error with the API, please try again!"

        tryAgain.setOnClickListener {
            presenter.updateCollection(userID, round, mNumberOfHotdogs.text.toString())
        }

        apiErrorDialog.setCancelable(false)
        apiErrorDialog.setView(view)
        apiErrorDialog.show()
    }

    fun showTablePickerDialog() {

        val view = layoutInflater.inflate(R.layout.dialog_table_selection, null)
        val tablePickerDialog = android.app.AlertDialog.Builder(this@MainActivity).create()
        tablePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(0x00000000))

        val listView = view.findViewById<ListView>(R.id.tableList)

        val listItems = mutableListOf<String>()

        for (i in 1..20) {
            listItems.add("Table $i")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)

        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // value of item that is clicked
            val tableNumber = listView.getItemAtPosition(position) as String

            // Toast the values
            Toast.makeText(applicationContext,
                    "Position :$position\nItem Value : $tableNumber", Toast.LENGTH_LONG)
                    .show()

            tablePickerDialog.dismiss()

            showRoundSelectionDialog()
        }

        tablePickerDialog.setCancelable(false)
        tablePickerDialog.setView(view)
        tablePickerDialog.show()


    }
}



