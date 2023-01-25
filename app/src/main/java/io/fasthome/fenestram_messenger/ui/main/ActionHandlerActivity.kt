/**
 * Created by Dmitry Popov on 25.01.2023.
 */
package io.fasthome.fenestram_messenger.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ActionHandlerActivity : AppCompatActivity() {

    /***
     * Активность сделана в виде шлюза для принятия данных из других приложений, т.к. MainActivity не может быть одновременно
     * и лаунчером и ресивером
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.setClass(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}