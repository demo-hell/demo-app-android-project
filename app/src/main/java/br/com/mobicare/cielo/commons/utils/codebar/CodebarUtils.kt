package br.com.mobicare.cielo.commons.utils.codebar

import br.com.mobicare.cielo.commons.utils.DataCustom
import br.com.mobicare.cielo.commons.utils.addDays
import br.com.mobicare.cielo.commons.utils.financial.Agreement
import br.com.mobicare.cielo.commons.utils.financial.Bank
import br.com.mobicare.cielo.commons.utils.isNumeric
import java.util.*


class CodebarUtils {

    private fun isValidCode(it25code: String): Boolean {
        var validCode = checkLengthAndConsistency(it25code)
        if (!validCode) {
            return false
        }

        val charIt25Code = it25code.toCharArray()

        // check if the digit is valid
        var verifierDigit: Int = 0
        var calculatedDigit: Int? = 99
        val dataToCalculate: String

        if (Integer.parseInt(charIt25Code[0].toString()) == 8) {
            // consumption payment slip
            // forth digit is the verificator digit
            // the rest is data to test
            verifierDigit = Integer.parseInt(charIt25Code[3].toString())
            val digit = Integer.parseInt(charIt25Code[2].toString())

            dataToCalculate = it25code.substring(0, 3) + it25code.substring(4, 44)
            when (digit) {
                6, 7 -> calculatedDigit = mod10(dataToCalculate)
                8, 9 -> calculatedDigit = mod11(dataToCalculate, 0)
                else ->
                    // Invalid"
                    calculatedDigit = 99
            }
        } else {
            // charge payment slip
            verifierDigit = Integer.parseInt(charIt25Code[4].toString())
            dataToCalculate = it25code.substring(0, 4) + it25code.substring(5, 44)
            calculatedDigit = mod11(dataToCalculate, 1)

        }
        if (calculatedDigit == 99) {
            validCode = false
        }
        if (verifierDigit !== calculatedDigit) {
            validCode = false
        }
        return validCode
    }


    private fun mod11(data: String, bestCase: Int): Int? {
        var base = 2
        var total = 0

        val reverseData = StringBuilder(data).reverse().toString()
        for (ch in reverseData.toCharArray()) {
            // each term is calculated multiplying the digit by the base

            val intChar = Integer.parseInt(ch.toString())
            var term = intChar
            term *= base
            //Add term
            total += term

            //Next base
            base += 1
            if (base >= 10) {
                base = 2
            }
        }
        // Digit = 11 - (total%11)
        // if total%11 = 0 or 1 we return the best case
        // otherwise we return a number between 1 to 9 as the digit
        val reminder = total % 11
        return if (reminder == 0 || reminder == 1) {
            bestCase
        } else {
            11 - reminder
        }
    }

    private fun mod10(data: String): Int {
        var base = 2 // Multiply base (2 - 9)
        var total = 0 // Ammount of calculated digits

        val reverseData = StringBuilder(data).reverse().toString()
        for (ch in reverseData.toCharArray()) {
            // each term is calculated multiplying the digit by the base
            val intChar = Integer.parseInt(ch.toString())
            var term = intChar
            term *= base
            // if the actual term is greater then 9, we need to addInFrame their digits
            // and NOT the term. The term must be 18 (2*9) maximum. So we can
            // subtract 9 from the term. Ex: is term is 16: 1+6 = 7 and 16-9 = 7
            if (term > 9) {
                term -= 9
            }
            //Add term
            total += term

            // the base is changed at 2
            base = if (base == 2) 1 else 2
        }
        // the digit is calculated by the reminder (rem = 0 digit = 0)
        val reminder = total % 10
        return if (reminder != 0) 10 - reminder else 0
    }

    private fun checkLengthAndConsistency(it25code: String): Boolean {
        var isValidCode = true
        // first test if we have a 44 digit code
        if (it25code.length != 44) {
            //DLog("wrong size, must have 44 digits")
            isValidCode = false
        } else {
            // now check if all chars are numbers

            try {
                if (!it25code.isNumeric()) {
                    isValidCode = false
                }
            } catch (e: Exception) {
                //                DLog("code \(it25code) generated a fault")
                isValidCode = false
            }

        }
        return isValidCode
    }


    fun getDigitableCodeFrom(it25: String): CodeBarData {

        val charIt25 = it25.toCharArray()
        val camps = arrayOfNulls<String>(8)
        var digitableLine = it25
        var valor: String  = ""
        var dataResult : Calendar? = null
        val dateBase = DataCustom(1997, 10, 7).toCalendar()
        var exitDate = false
        var bank: String? = null
        var agreement: String? = null
        var valid: Boolean = true


        if (isValidCode(it25)) {
            if (Integer.parseInt(charIt25[0].toString()) == 8) {
                // consumption payment slip
                val thirdDigit = charIt25[2].toString()
                val firstCamp = it25.substring(0, 11)

                if (thirdDigit == "6" || thirdDigit == "7") {
                    camps[0] = firstCamp
                    camps[1] = mod10(camps[0]!!).toString()
                    camps[2] = it25.substring(11, 22)
                    camps[3] = mod10(camps[2]!!).toString()
                    camps[4] = it25.substring(22, 33)
                    camps[5] = mod10(camps[4]!!).toString()
                    camps[6] = it25.substring(33, 44)
                    camps[7] = mod10(camps[6]!!).toString()
                } else {
                    camps[0] = firstCamp
                    camps[1] = mod11(camps[0]!!, 0).toString()
                    camps[2] = it25.substring(11, 22)
                    camps[3] = mod11(camps[2]!!, 0).toString()
                    camps[4] = it25.substring(22, 33)
                    camps[5] = mod11(camps[4]!!, 0).toString()
                    camps[6] = it25.substring(33, 44)
                    camps[7] = mod11(camps[6]!!, 0).toString()
                }
                agreement = Agreement.nameFrom(it25.substring(1,2))
                valor = it25.substring(4,15)
                digitableLine = camps[0] + "-" + camps[1] + " " + camps[2] + "-" + camps[3] + " " + camps[4] + "-" + camps[5] + " " + camps[6] + "-" + camps[7]

            } else {
                // charge payment slip
                val firstCamp = it25.substring(0, 4) + it25.substring(19, 20)
                camps[0] = String.format("%05d", Integer.parseInt(firstCamp))

                bank = Bank.nameFrom( it25.substring(0, 3))

                val secondCamp = it25.substring(20, 24) + mod10(camps[0] + it25.substring(20, 24)).toString()
                camps[1] = String.format("%05d", Integer.parseInt(secondCamp))

                val thirdCamp = it25.substring(24, 29)
                camps[2] = String.format("%05d", Integer.parseInt(thirdCamp))

                val fourthCamp = it25.substring(29, 34) + mod10(camps[2] + it25.substring(29, 34)).toString()
                camps[3] = String.format("%06d", Integer.parseInt(fourthCamp))

                val fiftyCamp = it25.substring(34, 39)
                camps[4] = String.format("%05d", Integer.parseInt(fiftyCamp))

                val sixtyCamp = it25.substring(39, 44) + mod10(camps[4] + it25.substring(39, 44)).toString()
                camps[5] = String.format("%06d", Integer.parseInt(sixtyCamp))

                camps[6] = it25.substring(4, 5)
                camps[7] = it25.substring(5, 19)

                digitableLine = camps[0] + "." + camps[1] + " " + camps[2] + "." + camps[3] + " " + camps[4] + "." + camps[5] + " " + camps[6] + " " + camps[7]

                valor = camps[7]!!.substring(4, 14)

                val dateAdd: Int = camps[7]!!.substring(0, 4).toInt()
                 if(dateAdd != 0 ) {
                     exitDate = true
                     dateBase.addDays(dateAdd)
                 }
            }
        } else {
            //format user typed value
            if (it25.length == 47 || it25.length == 48) {
                val thirdDigit = charIt25[2].toString()
                if (Integer.parseInt(charIt25[0].toString()) == 8) {
                    agreement = Agreement.nameFrom(it25.substring(1,2))


                    valid = validArrecadacao(it25, valid, thirdDigit)

                    digitableLine = (it25.substring(0, 11) + "-" + it25.substring(11, 12) + " "
                            + it25.substring(12, 23) + "-" + it25.substring(23, 24) + " "
                            + it25.substring(24, 35) + "-" + it25.substring(35, 36) + " "
                            + it25.substring(36, 47) + "-" + it25.substring(47, 48) + " ")
                    valor = it25.substring(4,11) + it25.substring(12,16)
                } else {

                    valid = validaBoleto(it25, valid)

                    bank = Bank.nameFrom(it25.substring(0, 3))
                    digitableLine = (it25.substring(0, 5) + "." + it25.substring(5, 10) + " "
                            + it25.substring(10, 15) + "." + it25.substring(15, 21) + " "
                            + it25.substring(21, 26) + "." + it25.substring(26, 32) + " "
                            + it25.substring(32, 33) + " " + it25.substring(33, 47))
                    valor = it25.substring(37, 47)
                    val dateAdd: Int = it25.substring(33, 37).toInt()
                    if(dateAdd != 0 ) {
                        exitDate = true
                        dateBase.addDays(dateAdd)
                    }
                }
            } else {
                valid = false
            }
        }
        if (exitDate) {
            dataResult = dateBase
        }

        return  CodeBarData(digitableLine, valor, dataResult, bank, agreement, valid)
    }

    private fun validaBoleto(it25: String, valid: Boolean): Boolean {
        var valid1 = valid
        val firstCamp = it25.substring(0, 9)
        val mod1 = mod10(firstCamp).toString()
        if (!it25.substring(9, 10).equals(mod1)) valid1 = false

        val thirdCamp = it25.substring(10, 20)
        val mod2 = mod10(thirdCamp).toString()

        if (!it25.substring(20, 21).equals(mod2)) valid1 = false

        val fiftyCamp = it25.substring(21, 31)
        val mod3 = mod10(fiftyCamp).toString()
        if (!it25.substring(31, 32).equals(mod3)) valid1 = false

        return valid1
    }

    private fun validArrecadacao(it25: String, valid: Boolean, thirdDigit: String): Boolean {

        var valid1 = valid
        var mod1: String
        var mod2: String
        var mod3: String
        var mod4: String

        if (thirdDigit == "6" || thirdDigit == "7") {

            mod1 = mod10(it25.substring(0, 11)).toString()
            mod2 = mod10(it25.substring(12, 23)).toString()
            mod3 = mod10(it25.substring(24, 35)).toString()
            mod4 = mod10(it25.substring(36, 47)).toString()
        }else{
            mod1 = mod11(it25.substring(0, 11), 0).toString()
            mod2 = mod11(it25.substring(12, 23),0).toString()
            mod3 = mod11(it25.substring(24, 35),0).toString()
            mod4 = mod11(it25.substring(36, 47),0).toString()
        }


        if (!it25.substring(11, 12).equals(mod1)) {
            valid1 = false
        }
        if (!it25.substring(23, 24).equals(mod2)) {
            valid1 = false
        }
        if (!it25.substring(35, 36).equals(mod3)) {
            valid1 = false
        }
        if (!it25.substring(47, 48).equals(mod4)) {
            valid1 = false
        }
        return valid1
    }


}