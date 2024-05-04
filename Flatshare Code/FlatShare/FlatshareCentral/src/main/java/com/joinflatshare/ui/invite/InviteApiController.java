package com.joinflatshare.ui.invite;

import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.pojo.invite.InvitedItem;
import com.joinflatshare.pojo.invite.InvitedRequest;
import com.joinflatshare.pojo.invite.InvitedResponse;
import com.joinflatshare.pojo.user.User;
import com.joinflatshare.pojo.user.UserResponse;
import com.joinflatshare.ui.dialogs.DialogLottieViewer;
import com.joinflatshare.utils.helper.CommonMethods;
import com.joinflatshare.utils.mixpanel.MixpanelUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class InviteApiController {
    private final InviteActivity activity;

    protected InviteApiController(InviteActivity activity) {
        this.activity = activity;
    }

    protected void invitedUsers(ArrayList<String> phones) {
        InvitedRequest request = new InvitedRequest();
        request.getIds().addAll(phones);
        request.setType("0");
        activity.apiManager.getInvitedStatus(activity.getSelectedType(), request, response -> {
            InvitedResponse resp = (InvitedResponse) response;
            activity.getDisplayUsers().clear();
            if (resp != null && resp.getData().size() > 0) {
                switch (activity.getSelectedType()) {
                    case InviteActivity.INVITE_TYPE_APP:
                        sortAppRegistered(resp);
                        break;
                    case InviteActivity.INVITE_TYPE_FRIEND:
                        sortFriendRequest(resp);
                        break;
                    case InviteActivity.INVITE_TYPE_FLAT:
                        sortFlatRequest(resp);
                        break;
                }
            }
        });
    }

    private void sortAppRegistered(@NotNull InvitedResponse response) {
        ArrayList<User> registeredUsers = new ArrayList<>();
        ArrayList<User> unregusterdUsers = new ArrayList<>();
        for (InvitedItem item : response.getData()) {
            User user = new User();
            user.setId(item.getId());
            user.setDp(item.getDp());
            if (activity.getNameHashMap().get(item.getId()) != null)
                user.setName(Objects.requireNonNull(activity.getNameHashMap().get(item.getId())));
            else user.setName(item.getName());
            if (Boolean.TRUE.equals(item.isRegistered())) {
                user.setStatus(InviteActivity.INVITE_STATUS_REGISTERED);
                registeredUsers.add(user);
            } else if (item.isInvited()) {
                user.setStatus(InviteActivity.INVITE_STATUS_APP_INVITED);
                registeredUsers.add(user);
            } else {
                user.setStatus(InviteActivity.INVITE_STATUS_UNREGISTERED);
                unregusterdUsers.add(user);
            }
        }
        sort(registeredUsers);
        sort(unregusterdUsers);
        activity.getDisplayUsers().clear();
        activity.getDisplayUsers().addAll(registeredUsers);
        activity.getDisplayUsers().addAll(unregusterdUsers);
        activity.adapter.reload();
    }

    private void sortFriendRequest(InvitedResponse response) {
        ArrayList<User> registeredUsers = new ArrayList<>();
        ArrayList<User> unregusterdUsers = new ArrayList<>();
        ArrayList<User> friends = new ArrayList<>();
        ArrayList<User> friendInvited = new ArrayList<>();
        for (InvitedItem item : response.getData()) {
            User user = new User();
            user.setId(item.getId());
            user.setDp(item.getDp());
            if (activity.getNameHashMap().get(item.getId()) != null)
                user.setName(Objects.requireNonNull(activity.getNameHashMap().get(item.getId())));
            else user.setName(item.getName());
            if (item.isFriend()) {
                user.setStatus(InviteActivity.INVITE_STATUS_FRIENDS);
                friends.add(user);
            } else if (item.isRequested()) {
                user.setStatus(InviteActivity.INVITE_STATUS_FRIEND_INVITED);
                friendInvited.add(user);
            } else if (item.isRegistered()) {
                user.setStatus(InviteActivity.INVITE_STATUS_REGISTERED);
                registeredUsers.add(user);
            } else if (item.isInvited()) {
                user.setStatus(InviteActivity.INVITE_STATUS_APP_INVITED);
                friendInvited.add(user);
            } else {
                user.setStatus(InviteActivity.INVITE_STATUS_UNREGISTERED);
                unregusterdUsers.add(user);
            }
        }
        sort(registeredUsers);
        sort(unregusterdUsers);
        sort(friends);
        sort(friendInvited);
        activity.getDisplayUsers().clear();
        activity.getDisplayUsers().addAll(friends);
        activity.getDisplayUsers().addAll(friendInvited);
        activity.getDisplayUsers().addAll(registeredUsers);
        activity.getDisplayUsers().addAll(unregusterdUsers);
        activity.adapter.reload();
    }

    private void sortFlatRequest(InvitedResponse response) {
        ArrayList<User> registeredUsers = new ArrayList<>();
        ArrayList<User> unregusterdUsers = new ArrayList<>();
        ArrayList<User> flatmates = new ArrayList<>();
        ArrayList<User> flatInvited = new ArrayList<>();
        for (InvitedItem item : response.getData()) {
            User user = new User();
            user.setId(item.getId());
            user.setDp(item.getDp());
            if (activity.getNameHashMap().get(item.getId()) != null)
                user.setName(Objects.requireNonNull(activity.getNameHashMap().get(item.getId())));
            else user.setName(item.getName());
            if (item.isFlatmate()) {
                user.setStatus(InviteActivity.INVITE_STATUS_FLATMATE);
                flatmates.add(user);
            } else if (item.isRequested()) {
                user.setStatus(InviteActivity.INVITE_STATUS_FLAT_INVITED);
                flatInvited.add(user);
            } else if (item.isRegistered()) {
                user.setStatus(InviteActivity.INVITE_STATUS_REGISTERED);
                registeredUsers.add(user);
            } else if (item.isInvited()) {
                user.setStatus(InviteActivity.INVITE_STATUS_APP_INVITED);
                flatInvited.add(user);
            } else {
                user.setStatus(InviteActivity.INVITE_STATUS_UNREGISTERED);
                unregusterdUsers.add(user);
            }
        }
        sort(registeredUsers);
        sort(unregusterdUsers);
        sort(flatmates);
        sort(flatInvited);
        activity.getDisplayUsers().clear();
        activity.getDisplayUsers().addAll(flatmates);
        activity.getDisplayUsers().addAll(flatInvited);
        activity.getDisplayUsers().addAll(registeredUsers);
        activity.getDisplayUsers().addAll(unregusterdUsers);
        activity.adapter.reload();
    }


    protected void inviteUser(User invitedUser, int position) {
        final UserResponse resp = FlatShareApplication.Companion.getDbInstance().userDao().getUserResponse();
        final User user = resp.getData();
        if (user.getInvites() > 0) {
            activity.apiManager.inviteToApp(invitedUser.getId(), response -> {
                user.setInvites(user.getInvites() - 1);
                activity.viewBind.txtInviteCount.setText("You have " + user.getInvites() + " invites left");
                resp.setData(user);
                FlatShareApplication.Companion.getDbInstance().userDao().updateUserResponse(resp);
                invitedUser.setStatus(InviteActivity.INVITE_STATUS_APP_INVITED);
                activity.getDisplayUsers().set(position, invitedUser);
                activity.adapter.reload();
                new DialogLottieViewer(activity, R.raw.lottie_invite,null);
                MixpanelUtils.INSTANCE.onUserInvited(invitedUser);
            });
        } else CommonMethods.makeToast( "You do not have sufficient invites left");
    }

    protected void addFriends(User invitedUser, int position) {
        activity.apiManager.addFriends(invitedUser.getId(), response -> {
            invitedUser.setStatus(InviteActivity.INVITE_STATUS_FRIEND_INVITED);
            activity.getDisplayUsers().set(position, invitedUser);
            activity.adapter.reload();
            new DialogLottieViewer(activity, R.raw.lottie_invite,null);
            MixpanelUtils.INSTANCE.onFriendRequested(invitedUser);
        });
    }

    protected void addFlatmate(User invitedUser, int position) {
        activity.apiManager.addFlatmate(invitedUser.getId(), response -> {
            invitedUser.setStatus(InviteActivity.INVITE_STATUS_FLAT_INVITED);
            activity.getDisplayUsers().set(position, invitedUser);
            activity.adapter.reload();
            new DialogLottieViewer(activity, R.raw.lottie_invite,null);
            MixpanelUtils.INSTANCE.onFlatmateInvited(invitedUser);
        });
    }

    private void sort(ArrayList<User> array) {
        array.sort(Comparator.comparing(lhs -> lhs.getName().getFirstName()));
    }
}
