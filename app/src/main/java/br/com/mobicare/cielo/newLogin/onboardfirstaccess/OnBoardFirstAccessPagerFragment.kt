package br.com.mobicare.cielo.newLogin.onboardfirstaccess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.extensions.setSpanBold
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.newLogin.onboardfirstaccess.model.Item
import kotlinx.android.synthetic.main.fragment_onboard_first_access_pager.*

class OnBoardFirstAccessPagerFragment : Fragment() {

    companion object {
        private const val ITEM = "item"
        private const val POSITION = "POSITION"
        private const val PRODUCT_AND_SERVICE = 2
        private const val SHOW_OK_BUTTON = 4
        private var listener: OnBoardFirstAccessActivity.CallProcedeUserInformation? = null

        fun newInstance(item: Item, position: Int,
                        listener: OnBoardFirstAccessActivity.CallProcedeUserInformation?)
                : OnBoardFirstAccessPagerFragment {

            this.listener = listener
            val fragment = OnBoardFirstAccessPagerFragment()
            val bundle = Bundle()
            bundle.putInt(POSITION, position)
            bundle.putSerializable(ITEM, item)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_onboard_first_access_pager,
                container,
                false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        arguments?.let {
            val item = it.getSerializable(ITEM) as Item
            textViewTitle.text = item.title
            textViewContent.text = item.content
            val position = it.getInt(POSITION)

            if (position == PRODUCT_AND_SERVICE) {
                textViewContent.text = item.content
                        .setSpanBold(arrayOf(60, 85), arrayOf(72, 101))
            }
            if (position == SHOW_OK_BUTTON) {
                buttonOK.visible()
                buttonOK.setOnClickListener {
                    listener?.callProcedeUserInformation()
                    activity?.finish()
                }
            }
        }
    }
}