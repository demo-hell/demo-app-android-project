package br.com.mobicare.cielo.home.presentation.main.domain

import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.ui.BaseFragment

data class MenuActionElement(var itemId: String = "",
                             val fragment: BaseFragment?,
                             val action: ((activity: BaseActivity) -> Unit)? = null)