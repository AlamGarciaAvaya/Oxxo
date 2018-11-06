package com.example.alam.hdi

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.budiyev.android.codescanner.*
import kotlinx.android.synthetic.main.activity_qr.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody


class QR : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        button3.setOnClickListener {
            finish()
        }
        codeScanner = CodeScanner(this, scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false
        codeScanner.decodeCallback = DecodeCallback {

                var myPreferences = "myPrefs"
                var sharedPreferences = getSharedPreferences(myPreferences, Context.MODE_PRIVATE)
                var numeroqr = sharedPreferences.getString("numeroqr", "17863930499")
                var endpoint = sharedPreferences.getString("endpointqr", "http://breeze2-213.collaboratory.avaya.com/services/EventingConnector/events")
                var lang = sharedPreferences.getString("lang", "es-MX")

            val client = OkHttpClient()

            val mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
            val body = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"family\"\r\n\r\nAAAMIAETIQUETAS\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"type\"\r\n\r\nAAAMIAETIQUETASDESCRIPCION\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"version\"\r\n\r\n1.0\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"eventBody\"\r\n\r\n{\"mobileNumber\":\"$numeroqr\",\"productcode\":\"$it\",\"language\":\"$lang\"}\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--")
            val request = Request.Builder()
                    .url(endpoint)
                    .post(body)
                    .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                    .addHeader("Content-Type", "multipart/form-data")
                    .addHeader("cache-control", "no-cache")
                    .build()

            val response = client.newCall(request).execute()
            runOnUiThread {
                Toast.makeText(this, "Se ha detectado un producto: ${it}",
                        Toast.LENGTH_LONG).show()
            }

        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "Error al iniciar la cámara: ${it.message}",
                        Toast.LENGTH_LONG).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}
