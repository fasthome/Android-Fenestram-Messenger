package io.fasthome.fenestram_messenger.onboarding_impl.presentation.onboarding

import io.fasthome.fenestram_messenger.onboarding_impl.R
import io.fasthome.fenestram_messenger.util.PrintableText

object PageLists {

    fun getIntroSlides(curTextColor: Int): List<Page> {
        return listOf(
            Page(
                R.drawable.image_onboarding1,
                PrintableText.Raw("Обменивайся сообщениями безопасно и быстро."),
                curTextColor
            ),
            Page(
                R.drawable.image_onboarding2,
                PrintableText.Raw("Кроссплатформенный сервис обмена сообщениями позволяет общаться с разных устройств"),
                curTextColor
            ),
            Page(
                R.drawable.image_onboarding3,
                PrintableText.Raw("Создавай и общайся в групповых чатах. Делись своими впечатлениями с помощью фотографий."),
                curTextColor
            )
        )
    }
}