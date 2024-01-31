package ci.miage.master2.calculatricekt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast



class MainActivity : AppCompatActivity() {
    private lateinit var workingTV: TextView
    private var canAddOperation = false
    private var canAddDecimal = true
    private var currentInput = StringBuilder()
    private var lastOperation: Char? = null
    private var equals = false
    private var operation: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        workingTV = findViewById(R.id.workingTV)

        if (savedInstanceState != null) {
            canAddOperation  = savedInstanceState.getBoolean("canAddOperation")
            currentInput = StringBuilder(savedInstanceState.getString("currentInput", ""))
            lastOperation = savedInstanceState.getChar("lastOperation", 0.toChar())
            equals = savedInstanceState.getBoolean("equals")
            operation = savedInstanceState.getString("operation", "")
            updateUI()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("canAddOperation", canAddOperation)
        outState.putString("currentInput", currentInput.toString())
        outState.putChar("lastOperation", lastOperation ?: 0.toChar())
        outState.putBoolean("equals", equals)
        outState.putString("operation", operation)
        super.onSaveInstanceState(outState)
    }

    private fun updateUI() {
        workingTV.text = currentInput.toString()
    }

    private fun clearInput() {
        currentInput.clear()
        lastOperation = null
        workingTV.text = ""
        operation = ""
    }

    fun numberAction(view: View) {
        if (view is Button) {
            val number = view.text.toString()
            if (equals && !isOperation(number)) {
                clearInput()
                equals = false
            }

            if (number == ".") {
                if (canAddDecimal) {
                    currentInput.append("")
                }
                canAddDecimal = false
            } else {
                currentInput.append(number)
            }
        }
        canAddOperation = true
        updateUI()
    }

    fun operationAction(view: View) {
        if (view is Button && canAddOperation) {
            operation = view.text.toString()
            if (lastOperation != null) {
                calculateResult()
            }

            currentInput.append(operation)
            updateUI()
            lastOperation = operation[0]
            canAddOperation = false
            canAddDecimal = true
            equals = false
        }
    }

    private fun isOperation(text: String): Boolean {
        return "+-x÷%".contains(text)
    }

    fun allClearAction(view: View) {
        clearInput()
        updateUI()
    }

    fun backSpaceAction(view: View) {
        if (currentInput.isNotEmpty()) {
            currentInput.deleteCharAt(currentInput.length - 1)
            if (currentInput.isNotEmpty() && currentInput[0] == '-') {
                if (currentInput.length == 2) {
                    currentInput.deleteCharAt(0)
                }
            }
            updateUI()
        }
    }

    fun negativeBouton(view: View) {
        if (currentInput.isNotEmpty()) {
            currentInput = if (currentInput.startsWith("-")) {
                StringBuilder(currentInput.substring(1))
            } else {
                StringBuilder("-").append(currentInput)
            }
            updateUI()
        }
    }

    fun splitBySecondMinus(input: String): Pair<Int, Int>? {
        val firstMinusIndex = input.indexOf('-')
        if (firstMinusIndex != -1) {
            val secondMinusIndex = input.indexOf('-', firstMinusIndex + 1)
            if (secondMinusIndex != -1) {
                val number1 = input.substring(0, secondMinusIndex).toInt()
                val number2 = input.substring(secondMinusIndex + 1).toInt()
                return Pair(number1, number2)
            }
        }
        return null
    }




    fun equalAction(view: View) {
        calculateResult()
        equals = true
        operation = ""
        lastOperation = null


    }

    /*private fun calculateResult() {
        if (currentInput.isEmpty() || lastOperation == null) {
            return
        }

        val parts = currentInput.toString().split(lastOperation.toString())
        if (parts.size != 2) {
            return
        }

        val (number1, number2) = parts.map { it.toIntOrNull() }

        if (number1 == null || number2 == null) {
            showError()
            return
        }

        val result = when (lastOperation) {
            '+' -> number1 + number2
            '-' -> if (currentInput.startsWith("-")) number1 + number2 else number1 - number2
            'x' -> number1 * number2
            '÷' -> if (number2 != 0) number1 / number2 else showError()
            '%' -> if (number2 != 0) number1 % number2 else showError()
            else -> showError()
        }

        currentInput.clear()
        currentInput.append(result)
        updateUI()
    }
*/
    private fun calculateResult() {
        if (currentInput.isEmpty() || lastOperation == null) {
            return
        }

        val isNegative = currentInput.startsWith("-")
        val parts = if (isNegative) {
            val splitResult = splitBySecondMinus(currentInput.toString())
            if (splitResult != null) {
                val (number1, number2) = splitResult
                listOf(number1.toString(), number2.toString())
            } else {
                currentInput.toString().split(lastOperation.toString())
            }
        } else {
            currentInput.toString().split(lastOperation.toString())
        }

        if (parts.size != 2) {
            return
        }

        val (number1, number2) = parts.map { it.toIntOrNull() }
        if (number1 == null || number2 == null) {
            showError()
            return
        }

        val result = when (lastOperation) {
            '+' -> number1 + number2
            '-' -> number1 - number2
            'x' -> number1 * number2
            '÷' -> if (number2 != 0) number1 / number2 else showError()
            '%' -> if (number2 != 0) number1 % number2 else showError()
            else -> showError()
        }

        currentInput.clear()
        currentInput.append(result)
        updateUI()
    }


    private fun showError() {
        Toast.makeText(this, "ERREUR", Toast.LENGTH_SHORT).show()
    }
}