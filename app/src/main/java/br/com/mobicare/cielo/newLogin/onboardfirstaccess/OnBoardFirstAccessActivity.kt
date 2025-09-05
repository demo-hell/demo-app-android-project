package br.com.mobicare.cielo.newLogin.onboardfirstaccess

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.newLogin.onboardfirstaccess.adapter.OnBoardFirstAccessAdapter
import br.com.mobicare.cielo.newLogin.onboardfirstaccess.model.Item
import kotlinx.android.synthetic.main.activity_on_board_first_access.*

class OnBoardFirstAccessActivity : AppCompatActivity() {

    private lateinit var onBoardFirstAccessAdapter: OnBoardFirstAccessAdapter

    companion object {
        private var listener: CallProcedeUserInformation? = null
        fun create(context: Context, listener: CallProcedeUserInformation) {
            context.startActivity(Intent(context,
                    OnBoardFirstAccessActivity::class.java))
            this.listener = listener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_board_first_access)
        initViews()
        initListener()
    }

    private fun initViews() {
        val itemList = resources.getStringArray(R.array.onboard_first_access_title)
                .mapIndexed { index, title ->
                    Item(title, resources
                            .getStringArray(R.array.onboard_first_access)[index])
                }
        onBoardFirstAccessAdapter = OnBoardFirstAccessAdapter(itemList, listener, this.supportFragmentManager)
        viewPager.adapter = onBoardFirstAccessAdapter
        viewPager.offscreenPageLimit = 3
        tabDots.setupWithViewPager(viewPager)
        UserPreferences.getInstance().saveNewlyAccredited(true)
    }

    private fun initListener() {
        imageViewCross.setOnClickListener {
            listener?.callProcedeUserInformation()
            finish()
        }
    }

    override fun onBackPressed() = Unit

    interface CallProcedeUserInformation {
        fun callProcedeUserInformation()
    }
}
