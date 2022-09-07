package com.aldajo92.opencvapplication

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldajo92.opencvapplication.ui.RenderPhoto
import com.aldajo92.scancv.DocScanCV2Activity
import com.aldajo92.scancv.DocScanCV2Activity.Companion.PHOTO_IMAGE_BUNDLE_KEY

class MainActivity : ComponentActivity() {

    private val imagePathState = mutableStateOf("")

    private val requestPhotoResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            it.data
                ?.getStringExtra(PHOTO_IMAGE_BUNDLE_KEY)
                ?.let { imagePathString ->
                    imagePathState.value = imagePathString
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val pathState by imagePathState
            ContentMain(pathState) {
                requestPhotoResult.launch(DocScanCV2Activity.openCameraIntent(this))
            }
        }
    }

}

@Preview
@Composable
fun ContentMain(
    imageUriPathState: String? = "",
    buttonClicked: () -> Unit = {},
) {
    Box(Modifier.fillMaxSize()) {
        RenderPhoto(
            Modifier.padding(bottom = 25.dp),
            imageSrc = imageUriPathState.orEmpty(),
        ){
            buttonClicked()
        }
    }
}
