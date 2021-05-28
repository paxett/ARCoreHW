package com.paxet.arcorehw

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.sceneform.Sceneform
import com.google.ar.sceneform.ux.ArFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            if(Sceneform.isSupported(this)) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.ar_fragment, ArFragment::class.java, null)
                    .commit()
            }
        }
    }
}