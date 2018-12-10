
@file:Suppress("DEPRECATION")

package org.zubbl.views.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatTextView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_nearby.*
import org.zubbl.R
import org.zubbl.application.AppController
import org.zubbl.configs.Constant
import org.zubbl.configs.SessionManager
import org.zubbl.customviews.ConnectZubble
import org.zubbl.customviews.CustomDialog
import org.zubbl.customviews.JoinZubblDialog
import org.zubbl.interfaces.CallBackListener
import org.zubbl.interfaces.ServiceListener
import org.zubbl.model.Login.MessageModel
import org.zubbl.model.MerchantDetails
import org.zubbl.model.MerchantSummery
import org.zubbl.model.NearBy
import org.zubbl.model.common.JsonResp
import org.zubbl.receivers.LocationUpdateReceiver
import org.zubbl.service.ApiService
import org.zubbl.service.NearByService
import org.zubbl.utils.*
import javax.inject.Inject

/*
Created by lalitha
 */


open class NearByActivity : AppCompatActivity(), ServiceListener, View.OnClickListener,OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener,LocationUpdateReceiver.Receiver,CallBackListener{

    lateinit var mGoogleMap: GoogleMap
    private  var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest?=null
    private  var mFusedLocationClient:FusedLocationProviderClient?=null
    private var locationUpdateReceiver:LocationUpdateReceiver?=null

    @Inject
    lateinit var networkUtils:NetworkUtils
    @Inject
    lateinit var customDialog: CustomDialog
    @Inject
    lateinit var commonMethods: CommonMethods
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var apiService: ApiService
    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var runTimePermission: RunTimePermission
    private   var isFirstTime=true
    private lateinit var progressDialog: ProgressDialog
    private var lat:Double=0.0
    private var lng:Double=0.0
    var bundle=Bundle()
    private var zubblList:List<MerchantDetails>?=null
    private var markerList: MutableList<Marker> ?=ArrayList()
    private lateinit var serviceIntent:Intent
    private var MY_PERMISSIONS_REQUEST_LOCATION=99
    private var firstUpdate=true
    private var TIME_INTERVAL:Long=600*1000 //1 min interval

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppController.appComponent.inject(this)
        setContentView(R.layout.fragment_nearby)
        //intent the backend serrvice
        serviceIntent= Intent(this,NearByService::class.java)

        //check the location permission to user
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission()
        }

        //initialize the map
        initMap()

        initViews()
    }

    override fun onPause() {
        super.onPause()
        // stop the map location updates
        if (mFusedLocationClient != null) {
            mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
        }
        //stop the service
        stopService(serviceIntent)
    }

    //Initialize View Components
    private fun initViews(){
        progressDialog = commonMethods.getProgressDialog(this)
        iv_left_icon.setOnClickListener(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    //doLocation updates for every two minutes
    private fun callLocationUpdates(){
        mLocationRequest =  LocationRequest()
        mLocationRequest?.interval =TIME_INTERVAL// 1 minute interval
        //  mLocationRequest?.fastestInterval =TIME_INTERVAL//1 minute
        mLocationRequest?.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.e("error","mLocationCallback")
            mGoogleMap.isMyLocationEnabled = true
            if( mFusedLocationClient!=null)
                mFusedLocationClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())

        }
    }

    override fun onResume() {
        super.onResume()
        if (!networkUtils.haveNetworkConnection()) {
            customDialog.showDialog(supportFragmentManager, getString(R.string.network_failure), 0, getString(R.string.okay), object : CustomDialog.BtnOkClick {
                override fun clicked() {
                    checkGpsEnable()}
            })
        }
        else{
            checkGpsEnable()
        }
        if(checkLocationPermission() ) {
            if (mGoogleApiClient != null && mFusedLocationClient != null) {
                callLocationUpdates()
            } else {
                buildGoogleApiClient()
            }
        }
    }

    @Synchronized
    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        mGoogleApiClient?.connect()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            iv_left_icon.id->{
                onBackPressed()
            }
        }
    }
    //initialize the map fragment
    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_nearby) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //location permission granted
                buildGoogleApiClient()
                mGoogleMap.isMyLocationEnabled = true
            }
        } else {
            //below 23 versions
            buildGoogleApiClient()
            mGoogleMap.isMyLocationEnabled = true
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onLocationChanged(location: Location?) {
//        try{
//            Toast.makeText(this, "Location cahnged ", Toast.LENGTH_LONG).show()
//            val ll = LatLng(location!!.latitude, location.longitude)
//            val update = CameraUpdateFactory.newLatLngZoom(ll, 15f)
//            mGoogleMap.animateCamera(update)
//        } catch(e:NullPointerException){}
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // This is Case 2 (Permission is now granted)
                    if (ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient()
                            mGoogleMap.isMyLocationEnabled = true
                        }
                    }
                }
                else {
                   Toast.makeText(applicationContext,"Permission Denied",Toast.LENGTH_LONG).show()
                }
//                else {
//                    // This is Case 1 again as Permission is not granted by user
//                    if(!checkLocationPermission()) {
//                        //Now further we check if used denied permanently or not
//                        if (ActivityCompat.shouldShowRequestPermissionRationale(this@NearByActivity,
//                                        Manifest.permission.ACCESS_FINE_LOCATION)) {
//                            // case 4 User has denied permission but not permanently
//
//                        } else {
//                            // case 5. Permission denied permanently.
//                            // You can open Permission setting's page from here now.
//                            callPermissionSettings()
//                        }
//                    }
//
//                }

            }
        }
        return
    }
    private fun checkLocationPermission():Boolean{

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                        .setTitle("Zubbl")
                        .setMessage("Location permission is required")
                        .setPositiveButton(R.string.okay) { _, _ ->
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(this,
                                    Array(10)  {Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION)
                        }
                        .create()
                        .show()

            } else {
                ActivityCompat.requestPermissions(this,
                        Array(10){Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION)
            }
            return false
        } else {
            return true
        }
    }

    private var mLocationCallback: LocationCallback = object:LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            Log.e("error"," LocationResult")
            locationResult?.locations.let {
                val locationList = it
                if (locationList!!.size > 0) {
                    //The last location in the list is the newest
                    val location = locationList[locationList.size - 1]
                    Log.e("chennai", "$location.latitude$location.longitude")
                    //for testing
                    lat = location.latitude
                    lng = location.longitude
                    val ll = LatLng(location.latitude, location.longitude)
                    if(firstUpdate) {
                        firstUpdate=false
                        val update = CameraUpdateFactory.newLatLngZoom(ll, 15f)
                        mGoogleMap.animateCamera(update)
                    }

                    // call backend service when location updated
                    callNearByService()
                }
            }
        }
    }


    override fun onConnected(bundle: Bundle?) {
        callLocationUpdates()
    }

    // call the backend service
    private fun callNearByService(){
        locationUpdateReceiver= LocationUpdateReceiver(Handler())
        locationUpdateReceiver?.setReceiver(this)
        serviceIntent.putExtra(Constant.HOME, locationUpdateReceiver)
        serviceIntent.putExtra("lat",lat)
        serviceIntent.putExtra("lng",lng)
        startService(serviceIntent)
    }

    //call the merchant details api
    private fun callMerchantDetailApi(merchantId:String,offerId:String) {
        //add request values
        val query = HashMap<String, String>()
        Log.e("merchant api", "Mid$merchantId offerID  $offerId"+"userrid"+sessionManager.getPersonID())
        query["merchantId"] =merchantId
        query["offerId"]=offerId
        query["userId"]=sessionManager.getPersonID()

        //pass  the offerid and merchant id to next screen
        bundle.clear()
        bundle.putString("offerId",offerId)
        bundle.putString("merchantId",merchantId)

        //show loader
        commonMethods.showLoader(progressDialog)

        apiService.merchantDetails(query).enqueue(RequestCallback(RequestCode.REQ_MERCHANT_DETAILS, this))
    }

    override fun onSuccess(jsonResp: JsonResp, data: String) {
        commonMethods.dismissLoader(progressDialog)
        when (jsonResp.requestCode) {
            RequestCode.REQ_MERCHANT_DETAILS-> {
                if (jsonResp.responseCode == 200) {
                    jsonResp.headers?.let {
                        if (jsonResp.headers!!["status"]!![0].contains( "200")) {
                            try {
                                val merchantSummery: MerchantSummery = gson.fromJson(jsonResp.strResponse, MerchantSummery::class.java)
                                merchantSummery.merchantDetails.let {
                                    merchantSummery.merchantDetails?.joinedStatus?.let{
                                        // call the respective popup based on zubbl joinedstatus
                                        callRelaventPopup(it,jsonResp.strResponse)
                                    }

                                }
                            }catch (e:Exception){}
                        } else if(jsonResp.headers!!["status"]!![0].contains( "403")){
                            val messageModel: MessageModel = gson.fromJson(jsonResp.strResponse, MessageModel::class.java)
                            messageModel.message?.let { displayAuthorizationPopup(messageModel.message!!)
                            }
                        }
                        else {
                            //cast json response to model object
                            //show the  error  dialog box
                            val messageModel: MessageModel = gson.fromJson(jsonResp.strResponse, MessageModel::class.java)
                            messageModel.message?.let { customDialog.showDialog(supportFragmentManager, it, true)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onFailure(jsonResp: JsonResp, data: String) {
        commonMethods.dismissLoader(progressDialog)
        if (!data.isEmpty()) {
            customDialog.showDialog(supportFragmentManager, data, true)
        } else {
            customDialog.showDialog(supportFragmentManager, getString(R.string.server_error), true)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    @SuppressLint("SetTextI18n")
    // get the backend service result and update the ui
    override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
        Log.e("receive", "Service result $resultData")
        when(resultCode)
        {
            Constant.SUCCESS->{
                resultData.let {
                    val details = resultData.getString("Details")
                    //update the ui and adda the markers
                    val nearBy: NearBy = gson.fromJson(details, NearBy::class.java)
                    nearBy.merchantDetail?.let {
                        if(nearBy.merchantDetail!!.isNotEmpty()) {
                            zubblList = it
                            addMarkers(zubblList!!)
                        }else{// if zubble list is empty
                            // Removes all markers, overlays, and polylines from the map.
                            mGoogleMap.clear();
                        }
                    }
                }
            }
            Constant.FAILURE-> {
                if (isFirstTime) {
                    isFirstTime = false
                    resultData.let {
                        customDialog.showDialog(supportFragmentManager, resultData.getString("Details")!!, true)
                    }
                }
            }
            Constant.AUTHORIZATION_FAILURE->{
                resultData.let {
                    customDialog.showDialog(supportFragmentManager, resultData.getString("Details")!!, 0, "OK", object : CustomDialog.BtnOkClick {
                        override fun clicked() {
                            val intent = Intent(this@NearByActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    })
                }
            }
        }
    }

    //add markers in map
    private fun addMarkers(zubblList:List<MerchantDetails>){
        var lat1=0.0
        var lng1=0.0
        var anchorValue=0.5f
        var marker1: Marker
        // Removes all markers
        mGoogleMap.clear()
        markerList?.clear()
        //  add markers into list
        for (i in 0 until zubblList.size) {
            anchorValue += 0.5f
            val  merchantDetails=zubblList[i]
            merchantDetails.lat?.let{ lat1=it.toDouble()}
            merchantDetails.lng?.let{lng1=it.toDouble()}
            val markerOptions =  MarkerOptions()
                    .position(LatLng(lat1, lng1))
                    .anchor(anchorValue, anchorValue)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.offer))
            marker1 = mGoogleMap.addMarker(markerOptions)
            markerList?.add(marker1)

        }
        //set the custom markers to google map
        mGoogleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            @SuppressLint("InflateParams")
            override fun getInfoWindow(marker: Marker): View? {

                val v = layoutInflater.inflate(R.layout.custom_marker, null)
                val remainingUsers=v.findViewById<AppCompatTextView>(R.id.image1_remaining_users)
                var markerId =0
                for (i in 0 until markerList!!.size){
                    markerId = i
                    val m = markerList?.get(i)
                    if (m?.id == marker.id)
                        break
                }
                Log.e("onInfoWindowClick", "Marker id: $markerId")
                if( zubblList.size>=markerId) {
                    val merchantDetails = zubblList[markerId]
                    merchantDetails.joinedMembers.let {
                        remainingUsers.text = it.toString()
                    }
                    for (i in 0 until 3) {
                        val url = ""//Constant.IMAGE_URL +"/images/useravatar.png"
                        when (i) {
                            0 -> {
                                displayProfile(v.findViewById(R.id.image4), url)
                            }
                            1 -> {
                                displayProfile(v.findViewById(R.id.image3), url)
                            }
                            2 -> {
                                displayProfile(v.findViewById(R.id.image2), url)
                            }
                        }
                    }
                    merchantDetails.userProfile?.let {
                        val size=merchantDetails.userProfile!!.size
                        for(i in 0 until  merchantDetails.userProfile!!.size){
                            val userProfile = merchantDetails.userProfile!![i]
                            val url = Constant.IMAGE_URL + userProfile.userProfile
                            when(i){
                                0->{displayProfile(v.findViewById(R.id.image4),url)}
                                1->{displayProfile(v.findViewById(R.id.image3),url)}
                                2->{displayProfile(v.findViewById(R.id.image2),url)}
                            }
                        }
                    }
                }
                return v
            }

            override fun getInfoContents(marker: Marker): View? {
                return null
            }
        })
        mGoogleMap.setOnInfoWindowClickListener { p0 ->
            var markerId =0
            p0?.let{
                for (i in 0 until markerList!!.size){
                    markerId = i
                    val m = markerList?.get(i)
                    if (m?.id == p0.id)
                        break
                }
            }
            Log.e("onInfoWindowClick", "Marker id: $markerId")
            if( zubblList.size>=markerId){
                val merchantDetails= zubblList[markerId]
                merchantDetails.merchantId?.let{
                    merchantDetails.offerId?.let{
                        callMerchantDetailApi(merchantDetails.merchantId!!,merchantDetails.offerId!!)
                    }
                }
            }
        }
    }
    private fun displayProfile(imageView:de.hdodenhof.circleimageview.CircleImageView,url:String){
        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.useravatar)
        requestOptions.error(R.drawable.useravatar)
        Glide.with(applicationContext!!)
                .load(url)
                .apply(requestOptions)
                .into(imageView)

    }

    //check the gps  and location is enable or not
    private fun checkGpsEnable() {
        var gpsEnabled=false
        var networkEnabled=false
        val lm: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }
        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }
        if(!gpsEnabled && !networkEnabled){

            customDialog.showDialog(supportFragmentManager,getString(R.string.gps_enabled),0,getString(R.string.okay),object :CustomDialog.BtnOkClick{
                override fun clicked() {
                    startActivityForResult( Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),101)
                }
            })
        }
    }

    // handle the authorization failures  and it will navigate to login page
    private fun displayAuthorizationPopup(message:String){
        stopService(serviceIntent)
        customDialog.showDialog(supportFragmentManager,  message, 0, getString(R.string.okay), object : CustomDialog.BtnOkClick {
            override fun clicked() {
                val intent = Intent(this@NearByActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        })
    }

  //display  the mobile location settings
  private fun callPermissionSettings() {
      val intent = Intent()
      intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      val uri = Uri.fromParts("package", this@NearByActivity.applicationContext.packageName, null)
      intent.data = uri
      startActivityForResult(intent, 300)
  }


    private fun callRelaventPopup(status:Boolean, jsonRes:String){
        if(status){
            //call the merchant detail dialog box with join button
            bundle.putString("Details",jsonRes)
            val connectZubblDialog = ConnectZubble()
            Log.e("joinbundle",""+bundle)
            connectZubblDialog.arguments = bundle
            connectZubblDialog.show(supportFragmentManager, "Custom Bottom Sheet")
        }
        else{
            //call the zubbl detail popup with connect button
            bundle.putString("Details", jsonRes)
            val joinZubblDialog = JoinZubblDialog()
            joinZubblDialog.arguments = bundle
            joinZubblDialog.show(supportFragmentManager, "Custom Bottom Sheet")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) return
        when (requestCode) {
            101 -> checkGpsEnable()
            300 -> {}//checkLocationPermission()
            else -> {
            }
        }
    }

    override fun onDismiss() {
        Log.e("near","dismiss")
        callNearByService()
    }


}


