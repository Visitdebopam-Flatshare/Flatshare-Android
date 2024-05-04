package com.joinflatshare.ui.faq;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.joinflatshare.FlatshareCentral.databinding.ActivityFaqBinding;
import com.joinflatshare.pojo.faq.FaqItem;
import com.joinflatshare.ui.base.BaseActivity;
import com.joinflatshare.utils.mixpanel.MixpanelUtils;

import java.util.ArrayList;

public class FaqActivity extends BaseActivity {
    private ActivityFaqBinding viewBind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBind = ActivityFaqBinding.inflate(getLayoutInflater());
        setContentView(viewBind.getRoot());
        showTopBar(this, true, "Settings", 0, 0);
        new FaqViewBind(this, viewBind);
        new FaqListener(this, viewBind);
        getFaqs();
        MixpanelUtils.INSTANCE.onButtonClicked("Settings");
    }

    private void getFaqs() {
        ArrayList<FaqItem> faqItems = new ArrayList<>();
        FaqItem item = new FaqItem();
        item.setQuestion("What is flatshare?");
        item.getAnswers().add("flatshare is a new app for students and young adults living in India where members connect with each other for dating, hunting and sharing a flat together.");
        faqItems.add(item);

        item = new FaqItem();
        item.setQuestion("Why did you create flatshare?");
        item.getAnswers().add("Who you live with = Who you become.");
        item.getAnswers().add("People you surround yourself with can have a huge impact on your life.");
        item.getAnswers().add("That's why we created flatshare - to make it super easy for you to connect with other better and interesting people, people just like you.");
        item.getAnswers().add("People who get you.");
        faqItems.add(item);

        item = new FaqItem();
        item.setQuestion("What is Flathunt Together and how it’s different from Shared Flat Search?");
        item.getAnswers().add("Flathunt together allows you to connect with your future flatmate before you have a flat. Once you’ve found a flatmate you can hunt a private flat and move-in together.");
        item.getAnswers().add("If you are looking for an individual bed/room in an already rented flat.\nShared Flat Search is a better option for you.");
        faqItems.add(item);

        item = new FaqItem();
        item.setQuestion("How do I find a shared flat?");
        item.getAnswers().add("Looking for an individual bed/room in an already rented flat.\nFinding flatshare has never been easier.");
        item.getAnswers().add(getEmojiByUnicode(0x1F4AA) + " Build your profile: Your profile is a snapshot of who you are.");
        item.getAnswers().add(getEmojiByUnicode(0x2705) + " Check Flats: Based on your preferences, we’ll show you flats that you might like to check.");
        item.getAnswers().add(getEmojiByUnicode(0x1F91D) + " Match and Chat: If the checking is mutual, you can chat with the flatmates and decide if it’s the right flat for you.");
        faqItems.add(item);

        item = new FaqItem();
        item.setQuestion("How do I find a flatmate for my already rented flat?");
        item.getAnswers().add("Got a vacant room or bed?\nWith flatshare, you can find flatmates like friends.");
        item.getAnswers().add(getEmojiByUnicode(0x1F3E0) + " Add your flat: Add your flat by entering your flatname.");
        item.getAnswers().add(getEmojiByUnicode(0x1F4AA) + " Build your flat profile: Your flat profile includes you, your flatmates, flat photos and other details.");
        item.getAnswers().add(getEmojiByUnicode(0x2705) + " Check potential flatmates: Based on your details and preferences, we’ll show you potential flatmates who you might like to live with.");
        item.getAnswers().add(getEmojiByUnicode(0x1F91D) + "  Match and Chat: If the checking is mutual, you can chat with them and decide if they are the right flatmate for you.");
        faqItems.add(item);

        item = new FaqItem();
        item.setQuestion("Can I send a chat request directly and skip the mutual checking?");
        item.getAnswers().add("Yes, you can send chat request and skip the mutual checking.\n" +
                "Your chat request still has to be accepted for you to connect.");
        faqItems.add(item);


        item = new FaqItem();
        item.setQuestion("Why are there daily limits on Views, Checks and Chat Requests?");
        item.getAnswers().add("Our algorithm gives you only the Top Recommendations based on your profile and preferences so that you don't need to find a needle in a haystack. We do it for you.");
        item.getAnswers().add("Limits help everyone send and receive the Checks and Chat Requests to the ones they are most interested in.\n" +
                "Everyone gets much better quality of Checks and Chat Requests.");
        faqItems.add(item);


        item = new FaqItem();
        item.setQuestion("What does f - FlatScore mean?");
        item.getAnswers().add("FlatScore is a measure of community trust. \nHigher the community trusts you, the higher your score. \n" +
                "No spammer, scammer, or fraudster can have a high FlatScore.");
        item.getAnswers().add("FlatScore helps you in connecting with people who have FlatScore similar to you and keep spammers, scammers, & fraudsters out of flatshare.");
        faqItems.add(item);


        item = new FaqItem();
        item.setQuestion("How do Invites work?");
        item.getAnswers().add("Everybody who joins flatshare can invite a few of their friends.\n" +
                "If your friends join you get to invite even more friends.");
        item.getAnswers().add("All the invites expire in 48 hours, so make sure your friends know you’ve invited them.");
        item.getAnswers().add("You’ll get credit for the invite on their profile and 1000 flatcoins for each successful invite.");
        faqItems.add(item);


        item = new FaqItem();
        item.setQuestion("When should I report someone?");
        item.getAnswers().add("We care deeply about our users and strive to make our community safe and comfortable. We will not tolerate any form of abuse or harassment.");
        item.getAnswers().add("If you see any member trying to impersonate someone else, asking for brokerage, sending inappropriate messages or photos, go to the bottom of their profile and report.");
        item.getAnswers().add("You can also reach out directly to our team directly at hello@flatshare.club and share your feedback. We would love to hear from you.");
        faqItems.add(item);

        item = new FaqItem();
        item.setQuestion("I signed up with the wrong name/birthdate/gender. What should I do?");
        item.getAnswers().add("If you signed up with the wrong name, birthdate or gender, reach out to our team directly at hello@flatshare.club along with your mobile number and aadhar card and we’ll change it for you.");
        faqItems.add(item);

        item = new FaqItem();
        item.setQuestion("Can I share feedback or a feature request?");
        item.getAnswers().add("Constant evolution is our MO. We're always looking for ways to improve and make flatshare the best it can be. So don't be shy - let us know what you think!");
        item.getAnswers().add("If you have any other questions, feedback or want to report a negative experience, please reach out at hello@flatshare.club");
        faqItems.add(item);
        viewBind.expProfileFaq.setVisibility(View.VISIBLE);
        viewBind.expProfileFaq.setAdapter(new FaqAdapter(this, faqItems));
    }

    protected void collapseGroup(int position) {
        if (viewBind.expProfileFaq.isGroupExpanded(position))
            viewBind.expProfileFaq.collapseGroup(position);
    }
}
