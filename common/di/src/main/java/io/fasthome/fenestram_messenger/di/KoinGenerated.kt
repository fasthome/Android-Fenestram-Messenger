package io.fasthome.fenestram_messenger.di

/**
 * TODO:
 * заменить на методы из Koin: https://github.com/InsertKoinIO/koin/pull/1225
 */
import androidx.lifecycle.ViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier

inline fun <reified R> Module.single(
    crossinline provider: () -> R,
    qualifier: Qualifier? = null,
) = single(qualifier = qualifier) { params ->
    provider()
}

inline fun <reified P0, reified R> Module.single(
    crossinline provider: (P0) -> R,
    qualifier: Qualifier? = null,
) = single(qualifier = qualifier) { params ->
    provider(get { params })
}

inline fun <reified P0, reified P1, reified R> Module.single(
    crossinline provider: (P0, P1) -> R,
    qualifier: Qualifier? = null,
) = single(qualifier = qualifier) { params ->
    provider(get { params }, get { params })
}

inline fun <reified P0, reified P1, reified P2, reified R> Module.single(
    crossinline provider: (P0, P1, P2) -> R,
    qualifier: Qualifier? = null,
) = single(qualifier = qualifier) { params ->
    provider(get { params }, get { params }, get { params })
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified R> Module.single(
    crossinline provider: (P0, P1, P2, P3) -> R,
    qualifier: Qualifier? = null,
) = single(qualifier = qualifier) { params ->
    provider(get { params }, get { params }, get { params }, get { params })
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified P4, reified R> Module.single(
    crossinline provider: (P0, P1, P2, P3, P4) -> R,
    qualifier: Qualifier? = null,
) = single(qualifier = qualifier) { params ->
    provider(get { params }, get { params }, get { params }, get { params }, get { params })
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified P4, reified P5, reified R> Module.single(
    crossinline provider: (P0, P1, P2, P3, P4, P5) -> R,
    qualifier: Qualifier? = null,
) = single(qualifier = qualifier) { params ->
    provider(get { params }, get { params }, get { params }, get { params }, get { params }, get { params })
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified R> Module.single(
    crossinline provider: (P0, P1, P2, P3, P4, P5, P6) -> R,
    qualifier: Qualifier? = null,
) = single(qualifier = qualifier) { params ->
    provider(
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
    )
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified R> Module.single(
    crossinline provider: (P0, P1, P2, P3, P4, P5, P6, P7) -> R,
    qualifier: Qualifier? = null,
) = single(qualifier = qualifier) { params ->
    provider(
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
    )
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified R> Module.single(
    crossinline provider: (P0, P1, P2, P3, P4, P5, P6, P7, P8) -> R,
    qualifier: Qualifier? = null,
) = single(qualifier = qualifier) { params ->
    provider(
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
    )
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified R> Module.single(
    crossinline provider: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R,
    qualifier: Qualifier? = null,
) = single(qualifier = qualifier) { params ->
    provider(
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
    )
}

inline fun <reified R> Module.factory(
    crossinline provider: () -> R,
    qualifier: Qualifier? = null,
) = factory(qualifier = qualifier) { params ->
    provider()
}

inline fun <reified P0, reified R> Module.factory(
    crossinline provider: (P0) -> R,
    qualifier: Qualifier? = null,
) = factory(qualifier = qualifier) { params ->
    provider(get { params })
}

inline fun <reified P0, reified P1, reified R> Module.factory(
    crossinline provider: (P0, P1) -> R,
    qualifier: Qualifier? = null,
) = factory(qualifier = qualifier) { params ->
    provider(get { params }, get { params })
}

inline fun <reified P0, reified P1, reified P2, reified R> Module.factory(
    crossinline provider: (P0, P1, P2) -> R,
    qualifier: Qualifier? = null,
) = factory(qualifier = qualifier) { params ->
    provider(get { params }, get { params }, get { params })
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified R> Module.factory(
    crossinline provider: (P0, P1, P2, P3) -> R,
    qualifier: Qualifier? = null,
) = factory(qualifier = qualifier) { params ->
    provider(get { params }, get { params }, get { params }, get { params })
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified P4, reified R> Module.factory(
    crossinline provider: (P0, P1, P2, P3, P4) -> R,
    qualifier: Qualifier? = null,
) = factory(qualifier = qualifier) { params ->
    provider(get { params }, get { params }, get { params }, get { params }, get { params })
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified P4, reified P5, reified R> Module.factory(
    crossinline provider: (P0, P1, P2, P3, P4, P5) -> R,
    qualifier: Qualifier? = null,
) = factory(qualifier = qualifier) { params ->
    provider(get { params }, get { params }, get { params }, get { params }, get { params }, get { params })
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified R> Module.factory(
    crossinline provider: (P0, P1, P2, P3, P4, P5, P6) -> R,
    qualifier: Qualifier? = null,
) = factory(qualifier = qualifier) { params ->
    provider(
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
    )
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified R> Module.factory(
    crossinline provider: (P0, P1, P2, P3, P4, P5, P6, P7) -> R,
    qualifier: Qualifier? = null,
) = factory(qualifier = qualifier) { params ->
    provider(
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
    )
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified R> Module.factory(
    crossinline provider: (P0, P1, P2, P3, P4, P5, P6, P7, P8) -> R,
    qualifier: Qualifier? = null,
) = factory(qualifier = qualifier) { params ->
    provider(
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
    )
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified R> Module.factory(
    crossinline provider: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R,
    qualifier: Qualifier? = null,
) = factory(qualifier = qualifier) { params ->
    provider(
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
    )
}

inline fun <reified R : ViewModel> Module.viewModel(
    crossinline provider: () -> R,
    qualifier: Qualifier? = null,
) = viewModel(qualifier = qualifier) { params ->
    provider()
}

inline fun <reified P0, reified R : ViewModel> Module.viewModel(
    crossinline provider: (P0) -> R,
    qualifier: Qualifier? = null,
) = viewModel(qualifier = qualifier) { params ->
    provider(get { params })
}

inline fun <reified P0, reified P1, reified R : ViewModel> Module.viewModel(
    crossinline provider: (P0, P1) -> R,
    qualifier: Qualifier? = null,
) = viewModel(qualifier = qualifier) { params ->
    provider(get { params }, get { params })
}

inline fun <reified P0, reified P1, reified P2, reified R : ViewModel> Module.viewModel(
    crossinline provider: (P0, P1, P2) -> R,
    qualifier: Qualifier? = null,
) = viewModel(qualifier = qualifier) { params ->
    provider(get { params }, get { params }, get { params })
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified R : ViewModel> Module.viewModel(
    crossinline provider: (P0, P1, P2, P3) -> R,
    qualifier: Qualifier? = null,
) = viewModel(qualifier = qualifier) { params ->
    provider(get { params }, get { params }, get { params }, get { params })
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified P4, reified R : ViewModel> Module.viewModel(
    crossinline provider: (P0, P1, P2, P3, P4) -> R,
    qualifier: Qualifier? = null,
) = viewModel(qualifier = qualifier) { params ->
    provider(get { params }, get { params }, get { params }, get { params }, get { params })
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified P4, reified P5, reified R : ViewModel> Module.viewModel(
    crossinline provider: (P0, P1, P2, P3, P4, P5) -> R,
    qualifier: Qualifier? = null,
) = viewModel(qualifier = qualifier) { params ->
    provider(get { params }, get { params }, get { params }, get { params }, get { params }, get { params })
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified R : ViewModel> Module.viewModel(
    crossinline provider: (P0, P1, P2, P3, P4, P5, P6) -> R,
    qualifier: Qualifier? = null,
) = viewModel(qualifier = qualifier) { params ->
    provider(
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
    )
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified R : ViewModel> Module.viewModel(
    crossinline provider: (P0, P1, P2, P3, P4, P5, P6, P7) -> R,
    qualifier: Qualifier? = null,
) = viewModel(qualifier = qualifier) { params ->
    provider(
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
    )
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified R : ViewModel> Module.viewModel(
    crossinline provider: (P0, P1, P2, P3, P4, P5, P6, P7, P8) -> R,
    qualifier: Qualifier? = null,
) = viewModel(qualifier = qualifier) { params ->
    provider(
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
    )
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified R : ViewModel> Module.viewModel(
    crossinline provider: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R,
    qualifier: Qualifier? = null,
) = viewModel(qualifier = qualifier) { params ->
    provider(
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
    )
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified R : ViewModel> Module.viewModel(
    crossinline provider: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R,
    qualifier: Qualifier? = null,
) = viewModel(qualifier = qualifier) { params ->
    provider(
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params }
    )
}
inline fun <reified P0, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified R : ViewModel> Module.viewModel(
    crossinline provider: (P0, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R,
    qualifier: Qualifier? = null,
) = viewModel(qualifier = qualifier) { params ->
    provider(
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params },
        get { params }
    )
}