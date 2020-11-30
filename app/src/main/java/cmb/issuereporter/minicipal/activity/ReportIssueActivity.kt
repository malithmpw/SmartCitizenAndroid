package cmb.issuereporter.minicipal.activity

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import cmb.issuereporter.minicipal.R

class ReportIssueActivity : BaseActivity() {

    private val PERMISSION_CODE = 1000;
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri? = null
    var issueImage: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_new_issue_layout)
        issueImage = findViewById(R.id.imageView_issue_image)
        val retakeImageView = findViewById<ImageButton>(R.id.imageButton_retake)
        retakeImageView.setOnClickListener {
            openCameraToTakeAPicture()
        }
        openCameraToTakeAPicture()
    }


    private fun openCameraToTakeAPicture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED
            ) {
                //permission was not enabled
                val permission =
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                //show popup to request permission
                requestPermissions(permission, PERMISSION_CODE)
            } else {
                //permission already granted
                openCamera()
            }
        } else {
            //system os is < marshmallow
            openCamera()
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //called when user presses ALLOW or DENY from Permission Request Popup
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup was granted
                    openCamera()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //called when image was captured from camera intent
        if (resultCode == Activity.RESULT_OK) {
            //set image captured to image view
            issueImage?.setImageURI(image_uri)
        }
    }
}