# Camera Scanner with OpenCV #

## Preview with sample app ##

![](.media/evidence1.gif)

## Usage:

The easy way to call the activity wich contains the scanner algorith, is just call the following line:
```
import com.aldajo92.scancv.DocScanCV2Activity

// ...
DocScanCV2Activity.openCameraIntent(context)
```

If you need to get the path of the picture taken, use `registerForActivityResult` with the intent provided in the library, getting the string extra with the key `DocScanCV2Activity.PHOTO_IMAGE_BUNDLE_KEY`:
```
private val requestPhotoResult = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) {
    if (it.resultCode == Activity.RESULT_OK) {
        it.data
            ?.getStringExtra(DocScanCV2Activity.PHOTO_IMAGE_BUNDLE_KEY)
            ?.let { imagePathString ->
                // Hadle your image path here
            }
    }
}

// ...
requestPhotoResult.launch(DocScanCV2Activity.openCameraIntent(context))
```

Author:
- Alejandro Daniel Jose Gomez Florez (aldajo92)
- alejodiscovery20@gmail.com
