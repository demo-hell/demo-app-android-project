package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.engine

import androidx.fragment.app.Fragment
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_NAME_MACHINE


fun Fragment.getNameMachine() = this.arguments?.getString(ARG_PARAM_NAME_MACHINE)