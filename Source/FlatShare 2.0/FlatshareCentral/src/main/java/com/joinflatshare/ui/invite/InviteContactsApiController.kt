package com.joinflatshare.ui.invite

import com.debopam.flatshareprogress.DialogCustomProgress
import com.google.gson.Gson
import com.joinflatshare.FlatshareCentral.databinding.ActivityInviteBinding
import com.joinflatshare.constants.AppConstants
import com.joinflatshare.constants.ContactConstants
import com.joinflatshare.pojo.invite.MutualContactsResponse
import com.joinflatshare.pojo.invite.RequestSavedContacts
import com.joinflatshare.pojo.user.Name
import com.joinflatshare.pojo.user.User
import com.joinflatshare.ui.invite.InviteActivity.Companion.INVITE_STATUS_SUGGESTED_USERS
import com.joinflatshare.utils.helper.CommonMethod
import com.joinflatshare.webservice.api.WebserviceManager
import com.joinflatshare.webservice.api.interfaces.OnFlatshareResponseCallBack
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Created by debopam on 25/04/23
 */
class InviteContactsApiController(
    private val activity: InviteActivity,
    private val viewBind: ActivityInviteBinding
) {
    private var TAG = InviteActivity::class.java.simpleName

    fun sendContacts() {
        if (ContactConstants.allContacts.isNotEmpty()) {
            activity.apiManager.showProgress()
            val contacts = RequestSavedContacts()
            if (!AppConstants.isAppLive) {
                contacts.ids.add("6303546278")
                contacts.ids.add("8169533929")
                contacts.ids.add("9403556548")
            } else {
                for (user in ContactConstants.allContacts) {
                    contacts.ids.add(user.id)
                    if (contacts.ids.size == 1000)
                        break
                }
            }
            activity.apiManager.sendContacts(
                contacts
            ) { response ->
                val resp = response as com.joinflatshare.pojo.BaseResponse
                if (resp.status == 200) {
                    CommonMethod.makeLog(TAG, "${contacts.ids.size} Contacts uploaded")
//                    getMutualContacts()
                    DialogCustomProgress.hideProgress(activity);
                    activity.adapter.reload()
                }
            }
        }
    }

    private fun getMutualContacts() {
        WebserviceManager().getMutualContacts(
            activity,
            activity.selectedType,
            object : OnFlatshareResponseCallBack<Response<ResponseBody>> {
                override fun onResponseCallBack(response: String) {
                    val resp: MutualContactsResponse? =
                        Gson().fromJson(response, MutualContactsResponse::class.java)
                    if (!resp?.mutualContacts.isNullOrEmpty()) {
                        ContactConstants.mutualContacts.clear()
                        ContactConstants.mutualContacts.addAll(resp?.mutualContacts!!)
                    } else if (!AppConstants.isAppLive) {
                        ContactConstants.mutualContacts.add("6303546278")
                        ContactConstants.mutualContacts.add("8169533929")
                        ContactConstants.mutualContacts.add("9403556548")
                    }
                    createMutualUserList()
                }
            })
    }

    fun displayMutualList() {
        if (ContactConstants.mutualContacts.isNotEmpty()) {
            activity.displayUsers.clear()
            createMutualUserList()
        }
    }

    fun createMutualUserList() {
        val contactMap = HashMap<String, String>()
        for (user in ContactConstants.allContacts)
            contactMap[user.id] = user.name!!.firstName
        activity.displayUsers.clear()
        for (phone in ContactConstants.mutualContacts) {
            val user = User()
            if (contactMap.containsKey(phone)) {
                user.id = phone
                user.name = Name()
                user.name!!.firstName = contactMap[phone]!!
                user.status = INVITE_STATUS_SUGGESTED_USERS
                activity.displayUsers.add(user)
            }
        }
        DialogCustomProgress.hideProgress(activity);
        activity.adapter.reload()
    }
}