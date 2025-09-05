package br.com.mobicare.cielo.chargeback.presentation.home.helper

import android.content.res.Resources
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.domain.model.Chargeback
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackStatus
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.SEVEN
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extensions.toLowerCasePTBR

enum class ChargebackStatusStyleSelectorScreenType {
    HOME, DETAILS
}

class ChargebackStatusStyleSelector(
    private val res: Resources,
    private val screenType: ChargebackStatusStyleSelectorScreenType,
    chargeback: Chargeback
) {

    private var _tagIcon: Int = ZERO
    val tagIcon get() = _tagIcon

    private var _backgroundShape: Int = ZERO
    val backgroundShape get() = _backgroundShape

    private var _textStyle: Int = ZERO
    val textStyle get() = _textStyle

    private var _text: String = EMPTY
    val text get() = _text

    init {
        if (chargeback.isDone)
            chargeback.actionTakenCode?.let { selectDone(it) }
        else
            selectPending(chargeback.daysToDeadLine ?: ZERO)
    }

    private val selectTextStringRes
        get() =
            when (screenType) {
                ChargebackStatusStyleSelectorScreenType.HOME -> R.string.chargeback_days_to_deadline
                else -> R.string.chargeback_in_n_days
            }

    private fun selectPending(daysToDeadLine: Int) {
        _text = res.getString(selectTextStringRes, daysToDeadLine)

        if (daysToDeadLine <= THREE) {
            _tagIcon = R.drawable.ic_warning
            _backgroundShape = R.drawable.background_status_chargeback_warning_danger_100
            _textStyle = R.style.Label_100_regular_14_danger_500
            if (daysToDeadLine <= ONE)
                _text = res.getString(R.string.chargeback_deadline_today)
        } else if (daysToDeadLine <= SEVEN) {
            _tagIcon = R.drawable.ic_clock_orange
            _backgroundShape = R.drawable.background_status_chargeback_alert_100
            _textStyle = R.style.Label_100_regular_14_alert_600
        } else {
            _tagIcon = R.drawable.ic_calendar_clock
            _backgroundShape = R.drawable.background_status_chargeback_gray
            _textStyle = R.style.Label_100_regular_14_storm_100
        }
    }

    private fun selectDone(status: String) {
        _text = status.toLowerCasePTBR().capitalizePTBR()

        when (status) {
            ChargebackStatus.DECLINED.statusName -> {
                _tagIcon = R.drawable.ic_check_rounded
                _backgroundShape = R.drawable.background_status_chargeback_success_100
                _textStyle = R.style.Label_100_regular_14_success_500
            }
            else -> {
                _tagIcon = R.drawable.ic_money_note_arrow_up
                _backgroundShape = R.drawable.background_status_chargeback_secondary_twilight_100
                _textStyle = R.style.Label_100_regular_14_twilight_500
            }
        }
    }

}