package com.def.team2.screen.map

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.def.team2.R
import com.def.team2.network.model.IdolGroup
import com.def.team2.network.model.School
import com.google.gson.Gson
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.plugins.localization.LocalizationPlugin
import com.mapbox.mapboxsdk.plugins.localization.MapLocale
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment: Fragment(), MapContract.View {

    override lateinit var lifeCycleOwner: LifecycleOwner
    override lateinit var presenter: MapContract.Presenter

    override val isActive: Boolean
        get() = isAdded

    private var mapboxMap: MapboxMap? = null
    private var symbolManager: SymbolManager? = null

    private val tempImgUrl = "https://img-s-msn-com.akamaized.net/tenant/amp/entityid/BBLqut9.img?h=0&w=720&m=6&q=60&u=t&o=f&l=f&x=265&y=329"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Mapbox.getInstance(context!!, getString(R.string.mapbox_access_token))
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        presenter = MapPresenter(this, MapInteractor(context!!))

        initMap(savedInstanceState)
        initIdolRankView()
        initMapOptionClickListener()
        initFilterClickListener()

    }

    private fun initMap(savedInstanceState: Bundle?) {
        map_content.onCreate(savedInstanceState)
        map_content.getMapAsync { mapboxMap ->
            this@MapFragment.mapboxMap = mapboxMap
            mapboxMap.setStyle(Style.LIGHT) { mapStyle ->
                //                mapboxMap.setStyle(Style.Builder().fromUri("mapbox://styles/kyungseok-cory/ck5thafyb29hy1ioxtwdpnsmi")) { mapStyle ->
                val localizationPlugin = LocalizationPlugin(map_content, mapboxMap, mapStyle)
                localizationPlugin.setMapLanguage(MapLocale.KOREAN)

                symbolManager = SymbolManager(map_content, mapboxMap, mapStyle).apply {
                    iconAllowOverlap = true
                    textAllowOverlap = true
                }.apply {
                    addClickListener {symbol ->
                        symbol.data?.let {jsonElement ->
                            val school = Gson().fromJson(jsonElement, School::class.java)
                            presenter.loadIdolRankInSchool(school)
                        }
                    }
                }

                mapboxMap.addOnCameraIdleListener {
                    val position = mapboxMap.cameraPosition.target
                    val boundBox = mapboxMap.projection.visibleRegion.latLngBounds
                    presenter.updateMapPosition(position.latitude, position.longitude, boundBox, true)
                }
            }

            presenter.loadMySchool()
        }
    }

    private fun initIdolRankView() {
        vp_map_idol.adapter = IdolMapAdapter {
            presenter.removeIdolRankInSchool()
        }.apply {
            setItems(listOf(tempImgUrl, tempImgUrl, tempImgUrl))
        }
        vp_map_idol.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        fl_map_idol_background.setOnClickListener {
            presenter.removeIdolRankInSchool()
        }
    }

    private fun initMapOptionClickListener() {
        map_option_tab.setOnClickListener {
            map_option_tab.toggle()
        }

        iv_map_search.setOnClickListener {  }

        iv_map_filter.setOnClickListener { presenter.openFilterView() }

        iv_map_my_school.setOnClickListener { presenter.loadMySchool() }

        iv_map_my_location.setOnClickListener { presenter.loadMyLocation() }
    }

    private fun initFilterClickListener() {
        iv_map_filter_check_element.setOnClickListener { presenter.changeSchoolLevel(School.Level.ELEMENT) }
        tv_map_filter_element.setOnClickListener { presenter.changeSchoolLevel(School.Level.ELEMENT) }

        iv_map_filter_check_middle.setOnClickListener { presenter.changeSchoolLevel(School.Level.MIDDLE) }
        tv_map_filter_middle.setOnClickListener { presenter.changeSchoolLevel(School.Level.MIDDLE) }
        iv_label_filter_middle.setOnClickListener { presenter.deleteSchoolLevel(School.Level.MIDDLE, true) }

        iv_map_filter_check_high.setOnClickListener { presenter.changeSchoolLevel(School.Level.HIGH) }
        tv_map_filter_high.setOnClickListener { presenter.changeSchoolLevel(School.Level.HIGH) }
        iv_label_filter_high.setOnClickListener { presenter.deleteSchoolLevel(School.Level.HIGH, true) }

        iv_map_filter_check_univ.setOnClickListener { presenter.changeSchoolLevel(School.Level.UNIVERSAL) }
        tv_map_filter_univ.setOnClickListener { presenter.changeSchoolLevel(School.Level.UNIVERSAL) }

        iv_map_filter_close.setOnClickListener { presenter.loadSchoolList() }
    }

    override fun onStart() {
        super.onStart()
        map_content?.onStart()
    }

    override fun onResume() {
        super.onResume()
        map_content?.onResume()
        presenter.start()
    }

    override fun onPause() {
        presenter.clearDisposable()
        map_content?.onPause()
        super.onPause()
    }

    override fun onStop() {
        map_content?.onStop()
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map_content.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        map_content?.onDestroy()
        super.onDestroyView()
    }

    override fun onDestroy() {
        map_content?.onDestroy()
        super.onDestroy()
    }

    override fun showSchoolList(schoolList: List<School>) {
        val imgUrl1 = "https://upload.wikimedia.org/wikipedia/commons/6/60/TWICE_LOGO.png"
        val imgUrl2 = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/ff/BTS_logo_%282017%29.png/200px-BTS_logo_%282017%29.png"
        val imgUrl3 = "https://upload.wikimedia.org/wikipedia/commons/a/a2/Exo-logo-v-neck_design2.jpg"


        // refresh symbol
        symbolManager?.deleteAll()

        // make symbol
        schoolList.map {
            mapboxMap?.getStyle {style ->
                // Todo 나중에 idol 정보를 이용해서 filtering 할 것
                val iconBitmap = style.getImage(it.users.toString())
                iconBitmap?.let {_ ->
                    createSymbol(it, it.users.toString())
                } ?: kotlin.run {
                    val imgUrl = when (it.users.toString()) {
                        "1" -> imgUrl1
                        "2" -> imgUrl2
                        "3" -> imgUrl3
                        else -> imgUrl1
                    }

                    Glide.with(context!!)
                        .asBitmap()
                        .load(imgUrl)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                style.addImage(it.users.toString(), resource)
                                createSymbol(it, it.users.toString())
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }
                        })
                }
            }
        }
    }

    private fun createSymbol(school: School, imgId: String) {
        // Create Symbol
        symbolManager?.create(SymbolOptions()
            .withLatLng(LatLng(school.location.latitude, school.location.longitude))
            .withIconImage(imgId)
            .withIconSize(0.5f)
            .withData(Gson().toJsonTree(school))
        )
    }

    override fun hideMapOption() {
        map_option_tab.close()
    }

    override fun setSchoolFilterUI(active: Boolean) {
        fl_map_filter_background.visibility = if (active) View.VISIBLE  else View.GONE
    }

    override fun setFilterOption(filterType: School.Level, active: Boolean) {
        when (filterType) {
            School.Level.ELEMENT -> {
                setFilterCheckedUI(active, iv_map_filter_check_element, tv_map_filter_element, null)
            }

            School.Level.MIDDLE -> {
                setFilterCheckedUI(active, iv_map_filter_check_middle, tv_map_filter_middle, iv_label_filter_middle)
            }

            School.Level.HIGH -> {
                setFilterCheckedUI(active, iv_map_filter_check_high, tv_map_filter_high, iv_label_filter_high)
            }

            School.Level.UNIVERSAL -> {
                setFilterCheckedUI(active, iv_map_filter_check_univ, tv_map_filter_univ, null)
            }
        }
    }

    private fun setFilterCheckedUI(active: Boolean,
                                   checkImageView: ImageView,
                                   filterTextView: TextView,
                                   labelImageView: ImageView?) {
        if (active) {
            checkImageView.visibility = View.VISIBLE
            filterTextView.setTextColor(Color.BLACK)
            labelImageView?.visibility = View.VISIBLE
        } else {
            checkImageView.visibility = View.INVISIBLE
            filterTextView.setTextColor(ContextCompat.getColor(context!!, R.color.colorGray))
            labelImageView?.visibility = View.GONE
        }
    }

    override fun moveMapPosition(lat: Double, lng: Double) {
        CameraPosition.Builder()
            .target(LatLng(lat, lng))
            .zoom(15.0)
            .tilt(20.0)
            .build().run {
                mapboxMap?.animateCamera(
                    CameraUpdateFactory
                        .newCameraPosition(this), 2000)
            }
    }

    override fun showSchoolIdolRank(school: School, idolGroupList: List<IdolGroup>) {
        fl_map_idol_background.visibility = View.VISIBLE
    }

    override fun hideSchoolIdolRank() {
        fl_map_idol_background.visibility = View.GONE
    }

    override fun showTotalIdolRank() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        const val TAG = "FRAGMENT_MAP"

        fun newInstance() = MapFragment()
    }
}
