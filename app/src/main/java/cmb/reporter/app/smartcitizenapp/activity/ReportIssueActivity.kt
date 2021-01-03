package cmb.reporter.app.smartcitizenapp.activity

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
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import cmb.reporter.app.*
import cmb.reporter.app.smartcitizenapp.*
import cmb.reporter.app.smartcitizenapp.adapter.SmartCitizenSpinnerAdapter
import cmb.reporter.app.smartcitizenapp.models.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream


open class ReportIssueActivity : BaseActivity() {
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private var imageUri: Uri? = null
    private var issueImage: ImageView? = null
    private var encodedImage: String? = null
    private var areaSpinner: Spinner? = null
    private var categorySpinner: Spinner? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLastLocation: Location? = null
    private var descriptionTv: TextView? = null
    private lateinit var progressbar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_new_issue_layout)
        issueImage = findViewById(R.id.imageView_issue_image)
        areaSpinner = findViewById(R.id.spinner_area)
        categorySpinner = findViewById(R.id.spinner_department)
        progressbar = findViewById(R.id.progressBar)

        val retakeImageView = findViewById<ImageButton>(R.id.imageButton_retake)
        val reportIssueButton = findViewById<Button>(R.id.button_report_issue)
        retakeImageView.setOnClickListener {
            openCameraToTakeAPicture()
            reportIssueButton.isEnabled = true
            reportIssueButton.background =
                resources.getDrawable(R.drawable.rounded_button)
        }
        descriptionTv = findViewById(R.id.textView_report_issue_description)
        openCameraToTakeAPicture()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        reportIssueButton.setOnClickListener {
            reportIssueButton.isEnabled = false
            reportIssueButton.background =
                resources.getDrawable(R.drawable.rounded_button_disabled)
            val area =
                getArea(areaName = (if (areaSpinner == null) null else areaSpinner!!.selectedItem as String))

            val category =
                getCategory(categoryName = (if (categorySpinner == null) null else categorySpinner!!.selectedItem as String))

            val description = descriptionTv?.text.toString()
            if( description.isEmpty()){
                reportIssueButton.isEnabled = true
                Toast.makeText(
                    this@ReportIssueActivity,
                    "Please a description of the issue",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val status = IssueStatus.OPEN.name

            if (encodedImage.isNullOrEmpty()) {
                reportIssueButton.isEnabled = true
                Toast.makeText(
                    this@ReportIssueActivity,
                    "Attach an Image",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            if (latitude != null && longitude != null) {
                progressbar.visibility = View.VISIBLE
                val issue = Issue(
                    user = sharePrefUtil.getUser(),
                    category = category,
                    area = area,
                    imageToSave = listOf(encodedImage!!),
                    description = description,
                    lat = latitude!!,
                    lon = longitude!!,
                    status = status
                )
                val call = apiService.addIssue(
                    issue = issue
                )
                call.enqueue(object : Callback<IssueResponse> {
                    override fun onResponse(
                        call: Call<IssueResponse>,
                        response: Response<IssueResponse>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@ReportIssueActivity,
                                "Reporting is Successful",
                                Toast.LENGTH_LONG
                            ).show()
                            onBackPressed()
                        } else {
                            progressbar.visibility = View.GONE
                            reportIssueButton.isEnabled = true
                            reportIssueButton.background =
                                resources.getDrawable(R.drawable.rounded_button)
                            Toast.makeText(
                                this@ReportIssueActivity,
                                "Error Occurred, Try again",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<IssueResponse>, t: Throwable) {
                        progressbar.visibility = View.GONE
                        reportIssueButton.isEnabled = true
                        reportIssueButton.background =
                            resources.getDrawable(R.drawable.rounded_button)
                        Toast.makeText(
                            this@ReportIssueActivity,
                            "Error Occurred, Try again Later",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })

            } else {
                Toast.makeText(
                    this@ReportIssueActivity,
                    "Location data not found",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }



    private fun initSpinners() {
        val areaAdapter = SmartCitizenSpinnerAdapter(this, AppData.getAreas().map { it.name })
        areaSpinner?.let {
            it.adapter = areaAdapter
        }

        val categoryAdapter =
            SmartCitizenSpinnerAdapter(this, AppData.getCategory().map { it.name })
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
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //called when image was captured from camera intent
        if (resultCode == Activity.RESULT_OK) {
            //set image captured to image view
            issueImage?.setImageURI(imageUri)
            imageUri?.let {
                val imageStream: InputStream? = contentResolver.openInputStream(it)
                imageStream?.let {
                    encodedImage = encodeToBase64(imageStream)
                }
            }
        }
        initSpinners()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient!!.lastLocation
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    mLastLocation = task.result
                    mLastLocation?.let {
                        latitude = it.latitude
                        longitude = it.longitude
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

    private fun encodeToBase64(inputStream: InputStream): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val width = bitmap.width
        val height = bitmap.height
        val newWidth = 1000
        val newHeight = height*newWidth/width
        val resized = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        resized.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
        return "data:image/png;base64,${Base64.encodeToString(imageBytes, Base64.DEFAULT)}"
    }
}