package com.joinflatshare.ui.preferences.flat

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.R
import com.joinflatshare.FlatshareCentral.databinding.ActivityPrefFlatBinding
import com.joinflatshare.constants.AppData
import com.joinflatshare.customviews.GridSpacingItemDecoration
import com.joinflatshare.customviews.deal_breakers.DealBreakerCallback
import com.joinflatshare.customviews.deal_breakers.DealBreakerView
import com.joinflatshare.customviews.interests.InterestsView
import com.joinflatshare.interfaces.OnUiEventClick
import com.joinflatshare.pojo.amenities.AmenitiesItem
import com.joinflatshare.pojo.flat.DealBreakers
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.ui.flat.edit.FlatEditAmenityAdapter
import com.joinflatshare.ui.flat.flatoptions.view_furnishing.FurnishingAdapter
import com.joinflatshare.utils.deeplink.FlatShareMessageGenerator
import com.joinflatshare.utils.deeplink.UserShareMessageGenerator
import com.joinflatshare.utils.helper.DateUtils

class PrefFlatActivity : BaseActivity(), OnUiEventClick {
    lateinit var viewBind: ActivityPrefFlatBinding
    lateinit var listener: PrefFlatListener
    var amenityAdapter: FlatEditAmenityAdapter? = null
    var flatsizeAdapter: FurnishingAdapter? = null
    var furnishingAdapter: FurnishingAdapter? = null
    val user = FlatShareApplication.getDbInstance().userDao().getUser()
    var showLessAmenities = true
    var isFhtView = false
    var dealBreakerView: DealBreakerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityPrefFlatBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        isFhtView = intent.getBooleanExtra("isFhtView", false)
        listener = PrefFlatListener(this)
        bind()
    }

    private fun bind() {
        setHeader()
        setVerified()
        setLocation()
        setMoveInDate()
        setRentRange()
        setFlatSize()
        setRoomType()
        setAmenities()
        setFurnishing()
        setGender()
        setInterests()
        setLanguages()
        setDealBreakers()
    }

    private fun setHeader() {
        if (isFhtView) {
            viewBind.header.text = "FLATHUNT TOGETHER"
            viewBind.btnPrefFlatSearch.text = "Flathunt Together"
            viewBind.txtPrefFlatCloseSearch.text = "Close Flathunt Together"
            viewBind.txtPrefFlatCloseSearch.visibility = View.GONE
            viewBind.imgPrefFlatHeader.visibility = View.GONE
        } else {
            viewBind.header.text = "SHARED FLAT"
            viewBind.btnPrefFlatSearch.text = "Shared Flat Search"
            viewBind.txtPrefFlatCloseSearch.text = "Close Shared Flat Search"
        }
        if (isFhtView) {
            val myFlat = FlatShareApplication.getDbInstance().userDao().getFlatData()
            if (!FlatShareMessageGenerator.isFlatDataAvailableToShare(myFlat))
                viewBind.txtPrefFlatCopyLink.visibility = View.GONE
        } else {
            if (!UserShareMessageGenerator.isUserDataAvailableToShare(user))
                viewBind.txtPrefFlatCopyLink.visibility = View.GONE
        }

    }

    private fun setVerified() {
        if (isFhtView) {
            viewBind.includePrefFlatmate.switchVerifiedMember.isChecked =
                user?.flatProperties?.isVerifiedOnly == true
            viewBind.includePrefFlat.cardSwitchFlats.visibility = View.GONE
        } else {
            viewBind.includePrefFlat.switchVerifiedFlat.isChecked =
                user?.flatProperties?.isVerifiedOnly == true
            viewBind.includePrefFlatmate.cardSwitchMember.visibility = View.GONE
        }

    }

    private fun setLocation() {
        if (!user?.flatProperties?.preferredLocation.isNullOrEmpty() &&
            user?.flatProperties?.preferredLocation!!.size > 0
        )
            viewBind.includePrefFlat.txtPrefFlatLocation.text =
                user.flatProperties.preferredLocation[0].name
    }

    private fun setMoveInDate() {
        if (!user?.flatProperties?.moveinDate.isNullOrEmpty())
            viewBind.includePrefFlat.txtPrefFlatMovein.text =
                DateUtils.convertToAppFormat(user?.flatProperties?.moveinDate)
    }

    private fun setRentRange() {
        if (AppData.flatData?.rentRange?.startRange != null && AppData.flatData?.rentRange?.endRange != null) {
            if (AppData.flatData?.rentRange?.startRange!! > 0 && AppData.flatData?.rentRange?.endRange!! > 0) {
                viewBind.includePrefFlat.seekbarRent.setMinValue(AppData.flatData?.rentRange?.startRange!!.toFloat())
                viewBind.includePrefFlat.seekbarRent.setMaxValue(AppData.flatData?.rentRange?.endRange!!.toFloat())
                viewBind.includePrefFlat.txtRentMin.text =
                    "" + AppData.flatData?.rentRange?.startRange
                viewBind.includePrefFlat.txtRentMax.text =
                    "" + AppData.flatData?.rentRange?.endRange
            }
        }
        if (user?.flatProperties?.rentRange?.startRange != null && user.flatProperties.rentRange?.endRange != null) {
            if (user.flatProperties.rentRange?.endRange!! > 0) {
                viewBind.includePrefFlat.txtPrefFlatRentrange.text =
                    resources.getString(R.string.currency) +
                            "${user.flatProperties.rentRange?.startRange} - ${
                                resources.getString(
                                    R.string.currency
                                )
                            }${user.flatProperties.rentRange?.endRange}"
                viewBind.includePrefFlat.seekbarRent.setMinStartValue(user.flatProperties.rentRange?.startRange!!.toFloat())
                viewBind.includePrefFlat.seekbarRent.setMaxStartValue(user.flatProperties.rentRange?.endRange!!.toFloat())
            }
        }
        viewBind.includePrefFlat.seekbarRent.apply()
        listener.seekListener()
    }

    private fun setFlatSize() {
        if (isFhtView)
            viewBind.includePrefFlat.llPrefFlattypeHolder.visibility = View.GONE
        else {
            val items = ArrayList<AmenitiesItem>()
            if (AppData.flatData?.flatSize != null && AppData.flatData?.flatSize!!.isNotEmpty()) {
                for (size in AppData.flatData?.flatSize!!) {
                    val temp = AmenitiesItem()
                    temp.isSelected = user?.flatProperties?.flatSize?.contains(size) == true
                    temp.id = 1
                    temp.name = size
                    items.add(temp)
                }
            }
            viewBind.includePrefFlat.rvPrefFlatSize.layoutManager = GridLayoutManager(this, 2)
            viewBind.includePrefFlat.rvPrefFlatSize.addItemDecoration(
                GridSpacingItemDecoration(2, 35, false)
            )
            flatsizeAdapter = FurnishingAdapter(
                this, viewBind.includePrefFlat.rvPrefFlatSize, false, items
            )
            viewBind.includePrefFlat.rvPrefFlatSize.adapter = flatsizeAdapter
        }
    }


    fun setRoomType() {
        viewBind.includePrefFlat.txtPrefFlatPrivateRoom.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.color_text_primary
            )
        )
        viewBind.includePrefFlat.cardPrefFlatPrivateRoom.background =
            ContextCompat.getDrawable(this, android.R.color.transparent)
        viewBind.includePrefFlat.cardPrefFlatPrivateRoom.strokeColor =
            ContextCompat.getColor(this, R.color.color_icon)

        viewBind.includePrefFlat.txtPrefFlatSharedRoom.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.color_text_primary
            )
        )
        viewBind.includePrefFlat.cardPrefFlatSharedRoom.background =
            ContextCompat.getDrawable(this, android.R.color.transparent)
        viewBind.includePrefFlat.cardPrefFlatSharedRoom.strokeColor =
            ContextCompat.getColor(this, R.color.color_icon)


        if (!user?.flatProperties?.roomType.isNullOrEmpty()) {
            if (user?.flatProperties?.roomType.equals("Private Room")
                || user?.flatProperties?.roomType.equals("Both")
            ) {
                viewBind.includePrefFlat.txtPrefFlatPrivateRoom.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.color_text_secondary
                    )
                )
                viewBind.includePrefFlat.cardPrefFlatPrivateRoom.background =
                    ContextCompat.getDrawable(this, R.drawable.drawable_button_black)
                viewBind.includePrefFlat.cardPrefFlatPrivateRoom.strokeColor =
                    ContextCompat.getColor(this, R.color.button_bg_black)
            }
            if (user?.flatProperties?.roomType.equals("Shared Room")
                || user?.flatProperties?.roomType.equals("Both")
            ) {
                viewBind.includePrefFlat.txtPrefFlatSharedRoom.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.color_text_secondary
                    )
                )
                viewBind.includePrefFlat.cardPrefFlatSharedRoom.background =
                    ContextCompat.getDrawable(this, R.drawable.drawable_button_black)

                viewBind.includePrefFlat.cardPrefFlatSharedRoom.strokeColor =
                    ContextCompat.getColor(this, R.color.button_bg_black)
            }
        }
    }

    fun setAmenities() {
        if (isFhtView) {
            viewBind.includePrefFlat.llPrefFurnishingHolder.visibility = View.GONE
        } else {
            if (showLessAmenities) {
                viewBind.includePrefFlat.txtShowless.text = "Show More"
                viewBind.includePrefFlat.imgArrowAmenities.setImageResource(R.drawable.arrow_down_black)
            } else {
                viewBind.includePrefFlat.txtShowless.text = "Show Less"
                viewBind.includePrefFlat.imgArrowAmenities.setImageResource(R.drawable.ic_arrow_up_black)
            }
            val selectedAmenityList = ArrayList<String>()
            // Get selected value
            if (amenityAdapter != null) {
                user?.flatProperties?.amenities = amenityAdapter!!.selectedAmenityList
            }
            if (user?.flatProperties?.amenities != null && user.flatProperties.amenities.isNotEmpty()) {
                selectedAmenityList.addAll(user.flatProperties.amenities)
            }
            if (AppData.flatData?.amenities != null && AppData.flatData?.amenities!!.isNotEmpty()) {
                viewBind.includePrefFlat.rvFlatAmenities.layoutManager = LinearLayoutManager(this)
                val allAmenities = ArrayList<String>()
                if (showLessAmenities) {
                    for (amen in AppData.flatData!!.amenities) {
                        if (allAmenities.size == 5) break
                        allAmenities.add(amen)
                    }
                } else allAmenities.addAll(AppData.flatData!!.amenities)
                amenityAdapter = FlatEditAmenityAdapter(allAmenities, selectedAmenityList)
                viewBind.includePrefFlat.rvFlatAmenities.adapter = amenityAdapter
            }
        }
    }

    private fun setFurnishing() {
        if (isFhtView) {
            viewBind.includePrefFlat.llPrefFurnishingHolder.visibility = View.GONE
        } else {
            val items = ArrayList<AmenitiesItem>()
            if (AppData.flatData?.furnishing != null && AppData.flatData?.furnishing!!.isNotEmpty()) {
                for (size in AppData.flatData?.furnishing!!) {
                    val temp = AmenitiesItem()
                    temp.isSelected = user?.flatProperties?.furnishing?.contains(size) == true
                    temp.id = 1
                    temp.name = size
                    items.add(temp)
                }
            }
            viewBind.includePrefFlat.rvPrefFurnishing.layoutManager = GridLayoutManager(this, 1)
            viewBind.includePrefFlat.rvPrefFurnishing.addItemDecoration(
                GridSpacingItemDecoration(1, 35, false)
            )
            furnishingAdapter = FurnishingAdapter(
                this, viewBind.includePrefFlat.rvPrefFurnishing, false, items
            )
            viewBind.includePrefFlat.rvPrefFurnishing.adapter = furnishingAdapter
        }
    }


    private fun setGender() {
        listener.isMaleSelected = false
        listener.isFemaleSelected = false
        if (user?.flatProperties?.gender?.equals("Male") == true) {
            listener.isMaleSelected = true
            listener.isFemaleSelected = false
        } else if (user?.flatProperties?.gender?.equals("Female") == true) {
            listener.isMaleSelected = false
            listener.isFemaleSelected = true
        } else if (user?.flatProperties?.gender?.equals("Both") == true) {
            listener.isMaleSelected = true
            listener.isFemaleSelected = true
        }
        setGenderButton()
    }

    fun setGenderButton() {
        // Male
        viewBind.includePrefFlatmate.cardProfileMale.setCardBackgroundColor(
            ContextCompat.getColor(this, android.R.color.transparent)
        )
        viewBind.includePrefFlatmate.txtMale.setTextColor(
            ContextCompat.getColor(this, R.color.color_text_primary)
        )
        // Female
        viewBind.includePrefFlatmate.cardProfileFemale.setCardBackgroundColor(
            ContextCompat.getColor(this, android.R.color.transparent)
        )
        viewBind.includePrefFlatmate.txtFemale.setTextColor(
            ContextCompat.getColor(this, R.color.color_text_primary)
        )
        user?.flatProperties?.gender = ""
        if (listener.isMaleSelected && listener.isFemaleSelected) {
            viewBind.includePrefFlatmate.cardProfileMale.setCardBackgroundColor(
                ContextCompat.getColor(this, R.color.button_bg_black)
            )
            viewBind.includePrefFlatmate.txtMale.setTextColor(
                ContextCompat.getColor(this, R.color.color_text_secondary)
            )
            viewBind.includePrefFlatmate.cardProfileFemale.setCardBackgroundColor(
                ContextCompat.getColor(this, R.color.button_bg_black)
            )
            viewBind.includePrefFlatmate.txtFemale.setTextColor(
                ContextCompat.getColor(this, R.color.color_text_secondary)
            )
            user?.flatProperties?.gender = "Both"
        } else {
            if (listener.isMaleSelected) {
                viewBind.includePrefFlatmate.cardProfileMale.setCardBackgroundColor(
                    ContextCompat.getColor(this, R.color.button_bg_black)
                )
                viewBind.includePrefFlatmate.txtMale.setTextColor(
                    ContextCompat.getColor(this, R.color.color_text_secondary)
                )
                user?.flatProperties?.gender = "Male"
            } else if (listener.isFemaleSelected) {
                viewBind.includePrefFlatmate.cardProfileFemale.setCardBackgroundColor(
                    ContextCompat.getColor(this, R.color.button_bg_black)
                )
                viewBind.includePrefFlatmate.txtFemale.setTextColor(
                    ContextCompat.getColor(this, R.color.color_text_secondary)
                )
                user?.flatProperties?.gender = "Female"
            }
        }
        viewBind.includePrefFlatmate.cardProfileMale.invalidate()
        viewBind.includePrefFlatmate.cardProfileFemale.invalidate()
    }

    private fun setInterests() {
        if (!user?.flatProperties?.interests.isNullOrEmpty()) {
            viewBind.includePrefFlatmate.txtProfileInterest.text =
                TextUtils.join(", ", user?.flatProperties?.interests!!)
        }
    }

    private fun setLanguages() {
        if (!user?.flatProperties?.languages.isNullOrEmpty()) {
            viewBind.includePrefFlatmate.txtProfileLanguages.text =
                TextUtils.join(", ", user?.flatProperties?.languages!!)
        }
    }

    private fun setDealBreakers() {
        if (dealBreakerView == null)
            dealBreakerView = DealBreakerView(this, viewBind.includePrefFlatmate.rvFlatDeals)
        val callback = object : DealBreakerCallback {
            override fun onSmokingSelected(constant: Int) {
                if (user?.flatProperties?.dealBreakers == null)
                    user?.flatProperties?.dealBreakers = DealBreakers()
                user?.flatProperties?.dealBreakers?.smoking = constant
            }

            override fun onNonVegSelected(constant: Int) {
                if (user?.flatProperties?.dealBreakers == null)
                    user?.flatProperties?.dealBreakers = DealBreakers()
                user?.flatProperties?.dealBreakers?.nonveg = constant
            }

            override fun onPartySelected(constant: Int) {
                if (user?.flatProperties?.dealBreakers == null)
                    user?.flatProperties?.dealBreakers = DealBreakers()
                user?.flatProperties?.dealBreakers?.flatparty = constant
            }

            override fun onEggsSelected(constant: Int) {
                if (user?.flatProperties?.dealBreakers == null)
                    user?.flatProperties?.dealBreakers = DealBreakers()
                user?.flatProperties?.dealBreakers?.eggs = constant
            }

            override fun onWorkoutSelected(constant: Int) {
                if (user?.flatProperties?.dealBreakers == null)
                    user?.flatProperties?.dealBreakers = DealBreakers()
                user?.flatProperties?.dealBreakers?.workout = constant
            }

            override fun onPetsSelected(constant: Int) {
                if (user?.flatProperties?.dealBreakers == null)
                    user?.flatProperties?.dealBreakers = DealBreakers()
                user?.flatProperties?.dealBreakers?.pets = constant
            }
        }

        dealBreakerView?.setDealValues(user?.flatProperties, null)
        dealBreakerView?.assignCallback(callback)
        dealBreakerView?.show()
    }

    override fun onClick(intent: Intent?, requestCode: Int) {
        if (requestCode == 1) {
            // Interests or languages
            val type = intent?.getStringExtra("type")
            if (type == InterestsView.VIEW_TYPE_INTERESTS) {
                viewBind.includePrefFlatmate.txtProfileInterest.text =
                    intent.getStringExtra("interest")
                if (viewBind.includePrefFlatmate.txtProfileInterest.text.isEmpty())
                    user?.flatProperties?.interests = ArrayList()
                else {
                    val items = ArrayList<String>()
                    val text = viewBind.includePrefFlatmate.txtProfileInterest.text.toString()
                    val split = TextUtils.split(text, ", ")
                    if (split.isNotEmpty()) {
                        for (txt in split) {
                            items.add(txt)
                        }
                    }
                    user?.flatProperties?.interests = items
                }
            } else if (type == InterestsView.VIEW_TYPE_LANGUAGES) {
                viewBind.includePrefFlatmate.txtProfileLanguages.text =
                    intent.getStringExtra("interest")
                if (viewBind.includePrefFlatmate.txtProfileLanguages.text.isEmpty())
                    user?.flatProperties?.languages = ArrayList()
                else {
                    val items = ArrayList<String>()
                    val text = viewBind.includePrefFlatmate.txtProfileLanguages.text.toString()
                    val split = TextUtils.split(text, ", ")
                    if (split.isNotEmpty()) {
                        for (txt in split) {
                            items.add(txt)
                        }
                    }
                    user?.flatProperties?.languages = items
                }
            }
        }
    }

    override fun onBackPressed() {
        viewBind.btnBack.performClick()
    }
}