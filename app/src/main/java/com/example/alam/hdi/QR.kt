package com.example.alam.hdi

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.github.kittinunf.fuel.core.DataPart
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_llamada_video.*
import kotlinx.android.synthetic.main.activity_qr.*

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

                //Definimos las variables de URL que tendra nuestra peticion con sus respectivas llaves
                //Key
                var paramKey1 = "family"
                //Parametro-Variable
                var paramValue1 = "AAAMIAETIQUETAS"
                //Key
                var paramKey2 = "type"
                //Parametro Variable
                var paramValue2 = "AAAMIAETIQUETASDESCRIPCION"
                var paramKey3 = "version"
                //Parametro Variable
                var paramValue3 = "1.0"
                var paramKey4 = "eventbody"
                //Parametro Variable
                var paramValue4 = "{\"mobileNumber\":\"{$numeroqr}\",\"productcode\":\"{$it}\"}"
            val formData = listOf(paramKey1 to paramValue1,paramKey2 to paramValue2,paramKey3 to paramValue3,paramKey4 to paramValue4 )

            //Invocamos FUEL Manager y lo asignamos a una variable para tener un mejor acceso a el
                val manager: FuelManager by lazy { FuelManager() }
                //Usamos el metodo request de FUUEL Manager, junto a la lusta de parametros
            manager.upload("http://breeze2-213.collaboratory.avaya.com/services/EventingConnector/events", param = formData)
                    //Upload normally requires a file, but we can give it an empty list of `DataPart`
                    .dataParts { request, url -> listOf<DataPart>() }
                    .responseString { request, response, result ->
                        runOnUiThread {
                        Toast.makeText(this, "Se ha detectado un producto: ${it.text} . Llamando",
                                Toast.LENGTH_LONG).show()
                        }
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
