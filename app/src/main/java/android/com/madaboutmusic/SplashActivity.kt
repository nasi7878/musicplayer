package android.com.madaboutmusic

import android.com.madaboutmusic.Home.MainActivity
import android.com.madaboutmusic.utils.setStatusBarColor
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_splash.*

/**
 *
 * Created by nasima on 19/04/18.
 *
 */
class SplashActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(R.color.md_black_1000)
        setContentView(R.layout.activity_splash)
        loadGetStarted()
        getStartedBtn.setOnClickListener { loadMainActivity() }

    }

    private fun loadMainActivity() {
        val homeIntent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(homeIntent)
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out)
    }

    private fun loadGetStarted() {
        var handler = Handler()
        var runnable = Runnable {
            loadScreen()
        }
        handler.postDelayed( runnable , 3000)
    }

    private fun loadScreen() {

        getStartedRL.visibility = View.VISIBLE;
        initialScreen.visibility = View.GONE;

    }

    override fun onClick(v: View?) {

    }
}