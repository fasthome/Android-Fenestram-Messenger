
import io.fasthome.fenestram_messenger.camera_api.CameraParams
import io.fasthome.fenestram_messenger.camera_api.CameraResult
import io.fasthome.fenestram_messenger.camera_impl.presentation.camera.CameraFragment
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract

object CameraNavigationContract : NavigationContract<CameraParams, CameraResult>(CameraFragment::class)