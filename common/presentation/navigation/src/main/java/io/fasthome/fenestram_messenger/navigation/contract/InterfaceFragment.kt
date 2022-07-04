package io.fasthome.fenestram_messenger.navigation.contract

interface InterfaceFragment<out I : Any> {
    fun getInterface(): I
}