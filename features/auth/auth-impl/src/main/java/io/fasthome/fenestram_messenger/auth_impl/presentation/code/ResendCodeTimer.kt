package io.fasthome.fenestram_messenger.auth_impl.presentation.code

import android.os.CountDownTimer

class ResendCodeTimer(
    millisInFuture: Long,
    countDownInterval: Long,
    private val callback: String?.() -> Unit,
) : CountDownTimer(millisInFuture, countDownInterval) {
    override fun onTick(millisUntilFinished: Long) {
        callback(" ${millisUntilFinished.div(1000).inc()} c")
    }

    override fun onFinish() {
        callback(null)
    }
}