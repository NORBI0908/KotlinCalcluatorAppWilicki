package com.example.kotlincalcluatorappwilicki

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.exp
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    private var canAddOperation = false
    private var canAddDecimal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadData()
    }

    private fun saveData(){
        val sharedWorkings:String = workingsTV.text.toString()
        val sharedResults:String = resultsTV.text.toString()

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.apply{
            putString("STRING_KEY_WOR", sharedWorkings)
            putString("STRING_KEY_RES", sharedResults)
        }.apply()
    }

    private fun loadData(){
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedDataWor = sharedPreferences.getString("STRING_KEY_WOR", null)
        val savedDataRes = sharedPreferences.getString("STRING_KEY_RES", null)

        workingsTV.text = savedDataWor
        resultsTV.text = savedDataRes
    }

    fun numberAction(view: View) {
        if(view is Button)
        {
            if(view.text == ".")
            {
                if(canAddDecimal)
                    workingsTV.append(view.text)

                canAddDecimal = false
            }
            else
                workingsTV.append(view.text)

            canAddOperation = true
        }
    }

    fun operationAction(view: View) {
        if(view is Button && canAddOperation)
        {
            workingsTV.append(view.text)
            saveData()
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun allClearAction(view: View) {
        workingsTV.text = ""
        resultsTV.text = ""
        saveData()
    }

    fun backSpaceAction(view: View) {
        val length = workingsTV.length()
        if(length > 0) {
            workingsTV.text = workingsTV.text.subSequence(0, length - 1)
            saveData()
        }
    }

    fun equalsAction(view: View) {
        resultsTV.text = calculateResults()
        saveData()
    }

    fun sqrtOperation(view: View) {
        var sqrtDown: Float = calculateResults().toFloat()
        resultsTV.text = sqrt(sqrtDown).toString()
        saveData()
    }

    fun expOperation(view: View) {
        var expDown: Float = calculateResults().toFloat()
        resultsTV.text = (expDown*expDown).toString()
        saveData()
    }


    private fun calculateResults() : String{
        val digitsOperators = digitsOperators()
        if(digitsOperators.isEmpty()) return ""
        
        val timesDivision = timesDivisionCalculate(digitsOperators)
        if(timesDivision.isEmpty()) return ""

        val result = addSubstractCalculate(timesDivision)
        saveData()
        return result.toString()
    }

    private fun addSubstractCalculate(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for(i in passedList.indices)
        {
            if(passedList[i] is Char && i != passedList.lastIndex)
            {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                if(operator == '+')
                    result += nextDigit
                if(operator == '-')
                    result -= nextDigit
            }
        }

        return result
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('x') || list.contains('/'))
        {
            list = calcTimeDiv(list)
        }
        return list
    }

    private fun calcTimeDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for(i in passedList.indices){
            if(passedList[i] is Char && i != passedList.lastIndex && i < restartIndex)
            {
                val operator  = passedList[i]
                val prevDigit  = passedList[i - 1] as Float
                val nextDigit  = passedList[i + 1] as Float

                when(operator)
                {
                    'x' ->
                    {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/' ->
                    {
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }
                    else ->
                    {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }

            if(i > restartIndex)
                newList.add(passedList[i])
        }

        return newList
    }

    private fun digitsOperators(): MutableList<Any>
    {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for(character in workingsTV.text)
        {
            if(character.isDigit() || character == '.')
                currentDigit += character
            else
            {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }

        if(currentDigit != "")
            list.add(currentDigit.toFloat())
        
        return list
    }
}