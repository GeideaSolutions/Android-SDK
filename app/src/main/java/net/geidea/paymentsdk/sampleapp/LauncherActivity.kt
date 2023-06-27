package net.geidea.paymentsdk.sampleapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import net.geidea.paymentsdk.sampleapp.databinding.ActivityLauncherBinding

class LauncherActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLauncherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        with(binding){
            setContentView(root)
            setSupportActionBar(binding.includeAppBar.toolbar)
            item1.setOnClickListener {
                startActivity(Intent(this@LauncherActivity, MainActivity::class.java))
            }

            item2.setOnClickListener {
                startActivity(Intent(this@LauncherActivity, CardPaymentActivity::class.java))
            }
        }
    }
}