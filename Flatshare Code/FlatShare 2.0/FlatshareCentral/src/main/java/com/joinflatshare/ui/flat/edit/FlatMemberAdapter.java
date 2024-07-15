package com.joinflatshare.ui.flat.edit;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.debopam.progressdialog.DialogCustomProgress;
import com.google.android.material.card.MaterialCardView;
import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.chat.SendBirdUserChannel;
import com.joinflatshare.constants.AppConstants;
import com.joinflatshare.constants.RouteConstants;
import com.joinflatshare.constants.SendBirdConstants;
import com.joinflatshare.customviews.bottomsheet.BottomSheetView;
import com.joinflatshare.customviews.bottomsheet.ModelBottomSheet;
import com.joinflatshare.db.daos.UserDao;
import com.joinflatshare.interfaces.OnitemClick;
import com.joinflatshare.pojo.flat.MyFlatData;
import com.joinflatshare.pojo.invite.InvitedItem;
import com.joinflatshare.pojo.invite.InvitedRequest;
import com.joinflatshare.pojo.invite.InvitedResponse;
import com.joinflatshare.pojo.user.User;
import com.joinflatshare.ui.base.BaseActivity;
import com.joinflatshare.ui.chat.details.ChatDetailsActivity;
import com.joinflatshare.ui.explore.ExploreActivity;
import com.joinflatshare.ui.flat.details.FlatDetailsActivity;
import com.joinflatshare.ui.flat.flat_profile.MyFlatActivity;
import com.joinflatshare.ui.invite.InviteActivity;
import com.joinflatshare.ui.profile.details.ProfileDetailsActivity;
import com.joinflatshare.utils.helper.CommonMethod;
import com.joinflatshare.utils.helper.ImageHelper;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class FlatMemberAdapter extends RecyclerView.Adapter<FlatMemberAdapter.ViewHolder> {
    private final ArrayList<User> items;
    private final BaseActivity activity;
    private final MyFlatData flatData;
    private final MyFlatData myOwnFlat;
    private final String loggedInUserId;

    public FlatMemberAdapter(BaseActivity activity, MyFlatData flatData, ArrayList<User> items) {
        this.activity = activity;
        this.items = items;
        this.flatData = flatData;
        myOwnFlat = FlatShareApplication.Companion.getDbInstance().userDao().getFlatData();
        loggedInUserId = FlatShareApplication.Companion.getDbInstance().userDao().get(UserDao.USER_CONSTANT_USERID);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flatmember, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (activity instanceof MyFlatActivity) {
            if (position < items.size()) {
                holder.ll_member.setVisibility(View.VISIBLE);
                holder.img_flatmate_add.setVisibility(View.GONE);
                final User item = items.get(position);
                if (item.getId().equals(AppConstants.loggedInUser.getId())) {
                    ImageHelper.loadImage(activity, R.drawable.ic_user, holder.img_flatmember,
                            ImageHelper.getProfileImageWithAwsFromPath(item.getDp()));
                } else
                    ImageHelper.loadImage(activity, R.drawable.ic_user, holder.img_flatmember,
                            ImageHelper.getProfileImageWithAws(item));
                if (item.getName() != null)
                    holder.txt_flatmember.setText(item.getName().getFirstName());

                // Admin card
                if (flatData == null)
                    holder.card_admin.setVisibility(View.VISIBLE);
                else {
                    if (item.getId().equals(flatData.getId()))
                        holder.card_admin.setVisibility(View.VISIBLE);
                    else
                        holder.card_admin.setVisibility(View.GONE);
                }

                // Click work
                holder.ll_member.setOnClickListener(v -> {
                    if (item.getId().equals(loggedInUserId)) {
                        return;
                    }
                    ArrayList<ModelBottomSheet> options = new ArrayList<>();

                    InvitedRequest request = new InvitedRequest();
                    request.getIds().add(item.getId());
                    request.setType("0");
                    activity.apiManager.getInvitedStatus(InviteActivity.INVITE_TYPE_FRIEND, request, response -> {
                        InvitedResponse resp = (InvitedResponse) response;
                        if (resp.getData().size() > 0) {
                            InvitedItem invitedItem = resp.getData().get(0);
                            if (!invitedItem.isFriend() && !invitedItem.isRequested()) {
                                options.add(new ModelBottomSheet(R.drawable.ic_member_add, "Add Friend"));
                            } else
                                options.add(new ModelBottomSheet(R.drawable.ic_message_black, "Message " + item.getName().getFirstName()));
                        }
                    });
                    if (myOwnFlat.getId().equals(loggedInUserId)) {
                        if (!item.getId().equals(loggedInUserId)) {
//                                options.add(new ModelBottomSheet(R.drawable.ic_user_favorite, "Make Admin"));
                            options.add(new ModelBottomSheet(R.drawable.ic_cross_red, "Remove " + item.getName().getFirstName()));
                        }
                    }
                    if (options.size() > 0) {
                        final MyFlatActivity act = (MyFlatActivity) activity;
                        new BottomSheetView(activity, options, new OnitemClick() {
                            @Override
                            public void onitemclick(View view, int position1) {
                                if (options.get(position1).getName().equals("Add Friend")) {
                                    activity.apiManager.addFriends(item.getId(), res -> {
                                        CommonMethod.INSTANCE.makeToast("Friend request sent");
                                    });
                                } else if (options.get(position1).getName().equals("Message " + item.getName().getFirstName())) {
                                    if (AppConstants.isSendbirdLive) {
                                        activity.apiManager.showProgress();
                                        SendBirdUserChannel channel = new SendBirdUserChannel(activity);
                                        channel.joinUserChannel(item.getId(), SendBirdConstants.CHANNEL_TYPE_FRIEND, text -> {
                                            DialogCustomProgress.INSTANCE.hideProgress(activity);
                                            if (!text.equals("0")) {
                                                Intent intent1 = new Intent(activity, ChatDetailsActivity.class);
                                                intent1.putExtra("channel", text);
                                                CommonMethod.INSTANCE.switchActivity(activity, intent1, false);
                                            }
                                        });
                                    }
                                } else if (options.get(position1).getName().equals("Remove " + item.getName().getFirstName())) {
                                    act.apiManager.removeFlatmate(item.getId(), respon -> {
                                        act.getMyFlat();
                                    });
                                } else if (options.get(position1).getName().equals("Make Admin")) {
                                        /*act.apiManager.makeAdmin(item.getId(),
                                            (OnResponseCallback<BaseResponse>) response -> {
                                                activity.myFlat.getData().setOwner(item.getId());
                                                CommonMethods.setFlatData(activity, activity.myFlat);
                                                act.viewBind.setData(act.myFlat,act.loggedInUser);
                                                notifyDataSetChanged();
                                            });*/
                                }
                            }
                        });
                    }

                });
                holder.card_vacant_beds.setVisibility(View.GONE);
            } else {
                holder.ll_member.setVisibility(View.GONE);
                holder.img_flatmate_add.setVisibility(View.VISIBLE);
                holder.img_flatmate_add.setOnClickListener(view -> {
                    if (myOwnFlat == null) {
                        ((MyFlatActivity) activity).viewBind.llFlatmateSearch.performClick();
                    } else {
                        Intent intent = new Intent(activity, InviteActivity.class);
                        intent.putExtra(RouteConstants.ROUTE_CONSTANT_FROM, InviteActivity.INVITE_TYPE_FLAT);
                        CommonMethod.INSTANCE.switchActivity(activity, intent, false);
                    }
                });
            }
        } else if (activity instanceof FlatDetailsActivity || activity instanceof ExploreActivity
                ) {
            holder.img_flatmate_add.setVisibility(View.GONE);
            if (position == items.size()) {
                holder.card_vacant_beds.setVisibility(View.VISIBLE);
                holder.ll_member.setVisibility(View.GONE);
                holder.txt_vacant_beds.setText("" + flatData.getFlatProperties().getBeds().getVacant());
            } else {
                // Admin card
                if (items.get(position).getId().equals(flatData.getId()))
                    holder.card_admin.setVisibility(View.VISIBLE);
                else
                    holder.card_admin.setVisibility(View.GONE);
                holder.card_vacant_beds.setVisibility(View.GONE);
                holder.ll_member.setVisibility(View.VISIBLE);
                final User item = items.get(position);
                ImageHelper.loadImage(activity, R.drawable.ic_user, holder.img_flatmember,
                        ImageHelper.getProfileImageWithAws(item));
                if (item.getName() != null)
                    holder.txt_flatmember.setText(item.getName().getFirstName());
                holder.ll_member.setOnClickListener(view -> {
                    Intent intent = new Intent(activity, ProfileDetailsActivity.class);
                    intent.putExtra("phone", item.getId());
                    CommonMethod.INSTANCE.switchActivity(activity, intent, false);
                });
            }
        } else if (activity instanceof ProfileDetailsActivity) {
            holder.img_flatmate_add.setVisibility(View.GONE);
            holder.card_vacant_beds.setVisibility(View.GONE);
            holder.card_admin.setVisibility(View.GONE);
            holder.ll_member.setVisibility(View.VISIBLE);
            final User item = items.get(position);
            ImageHelper.loadImage(activity, R.drawable.ic_user, holder.img_flatmember,
                    ImageHelper.getProfileImageWithAws(item));
            if (item.getName() != null)
                holder.txt_flatmember.setText(item.getName().getFirstName());
            holder.ll_member.setOnClickListener(view -> {
                Intent intent = new Intent(activity, ProfileDetailsActivity.class);
                intent.putExtra("phone", item.getId());
                CommonMethod.INSTANCE.switchActivity(activity, intent, false);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (activity instanceof ProfileDetailsActivity)
            return items.size();
        else
            return items.size() + 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView img_flatmember;
        TextView txt_flatmember, txt_vacant_beds;
        ImageView img_flatmate_add;
        LinearLayout ll_member;
        FrameLayout card_vacant_beds;
        MaterialCardView card_admin;

        ViewHolder(View itemView) {
            super(itemView);
            txt_vacant_beds = itemView.findViewById(R.id.txt_vacant_beds);
            card_vacant_beds = itemView.findViewById(R.id.card_vacant_beds);
            txt_flatmember = itemView.findViewById(R.id.txt_flatmember);
            img_flatmember = itemView.findViewById(R.id.img_flatmember);
            ll_member = itemView.findViewById(R.id.ll_member);
            img_flatmate_add = itemView.findViewById(R.id.img_flatmate_add);
            card_admin = itemView.findViewById(R.id.card_admin);
        }
    }
}
