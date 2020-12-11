package cmb.reporter.smartcitizen.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.widget.*
import androidx.core.app.ActivityCompat
import cmb.reporter.smartcitizen.AppData
import cmb.reporter.smartcitizen.BuildConfig
import cmb.reporter.smartcitizen.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.ByteArrayOutputStream
import java.io.InputStream


open class ReportIssueActivity : BaseActivity() {
    private val PERMISSION_CODE = 1000;
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri? = null
    var issueImage: ImageView? = null
    var encodedImage: String? = null
    var areaSpinner: Spinner? = null
    var categorySpinner: Spinner? = null

    /**
     * Provides the entry point to the Fused Location Provider API.
     */
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    /**
     * Represents a geographical location.
     */
    private var mLastLocation: Location? = null

    private var description: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_new_issue_layout)
        issueImage = findViewById(R.id.imageView_issue_image)
        areaSpinner = findViewById(R.id.spinner_area)
        categorySpinner = findViewById(R.id.spinner_department)
        val retakeImageView = findViewById<ImageButton>(R.id.imageButton_retake)
        retakeImageView.setOnClickListener {
            openCameraToTakeAPicture()
        }
        description = findViewById(R.id.textView_report_issue_description)
        openCameraToTakeAPicture()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun initSpinners() {
        val areaAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, AppData.getAreas().map { it.name })
        areaSpinner?.let {
            it.adapter = areaAdapter
        }

        val categoryAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, AppData.getCategory().map { it.name })
        categorySpinner?.let {
            it.adapter = categoryAdapter
        }
    }

    public override fun onStart() {
        super.onStart()

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getLastLocation()
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //called when image was captured from camera intent
        if (resultCode == Activity.RESULT_OK) {
            //set image captured to image view
            issueImage?.setImageURI(image_uri)
            image_uri?.let {
                val imageStream: InputStream? = contentResolver.openInputStream(it)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                encodedImage = encodeImage(selectedImage)
                initSpinners()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient!!.lastLocation
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    mLastLocation = task.result
                    mLastLocation?.let {
                        val lat = it.latitude
                        val lon = it.longitude

                        description?.text = "Lat : $lat, Lon :$lon"
                    }

                } else {
                    Toast.makeText(
                        this@ReportIssueActivity,
                        getString(R.string.no_location_detected),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this@ReportIssueActivity,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun requestPermissions() {

        startLocationPermissionRequest()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size <= 0) {
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {

                // Build intent that displays the App settings screen.
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts(
                    "package",
                    BuildConfig.APPLICATION_ID, null
                )
                intent.data = uri
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }

    companion object {
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }

    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }
}