/**
 * Created by Viktor Solodskiy on 17.08.2022.
 */
package io.fasthome.fenestram_messenger.onboarding_api

import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult

interface OnboardingFeature {
    val onboardingNavigationContract: NavigationContractApi<NoParams,NoResult>
}