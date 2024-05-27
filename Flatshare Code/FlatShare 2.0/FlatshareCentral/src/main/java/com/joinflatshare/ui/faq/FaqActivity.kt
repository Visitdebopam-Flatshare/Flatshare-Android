package com.joinflatshare.ui.faq

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.joinflatshare.FlatshareCentral.databinding.ActivityFaqBinding
import com.joinflatshare.pojo.faq.FaqItem
import com.joinflatshare.ui.base.BaseActivity
import com.joinflatshare.utils.mixpanel.MixpanelUtils

/**
 * Created by debopam on 21/05/24
 */
class FaqActivity : BaseActivity() {
    private lateinit var viewBind: ActivityFaqBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind = ActivityFaqBinding.inflate(layoutInflater)
        setContentView(viewBind.root)
        showTopBar(this, true, "FAQ", 0, 0)
        MixpanelUtils.onButtonClicked("FAQ")
        init()
    }

    private fun init() {
        viewBind.rvFaq.layoutManager = LinearLayoutManager(this)
        viewBind.rvFaq.adapter = FaqAdapter(getFaqs())
    }

    private fun getFaqs(): ArrayList<FaqItem> {
        val faqItems = ArrayList<FaqItem>()
        faqItems.add(
            FaqItem(
                "What is flatshare?",
                true
            )
        )
        faqItems.add(
            FaqItem(
                "flatshare is a new app for students and young adults living in India where members connect with each other for dating, hunting and sharing a flat together.",
                false
            )
        )


        faqItems.add(
            FaqItem(
                "Why did you create flatshare?",
                true
            )
        )
        faqItems.add(
            FaqItem(
                "Who you live with = Who you become.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                "People you surround yourself with can have a huge impact on your life.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                "That's why we created flatshare - to make it super easy for you to connect with other better and interesting people, people just like you.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                "People who get you.",
                false
            )
        )


        faqItems.add(
            FaqItem(
                "What is Flathunt Together and how it’s different from Shared Flat Search?",
                true
            )
        )
        faqItems.add(
            FaqItem(
                "Flathunt together allows you to connect with your future flatmate before you have a flat. Once you’ve found a flatmate you can hunt a private flat and move-in together.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                "If you are looking for an individual bed/room in an already rented flat.\nShared Flat Search is a better option for you.",
                false
            )
        )



        faqItems.add(
            FaqItem(
                "How do I find a shared flat?",
                true
            )
        )
        faqItems.add(
            FaqItem(
                "Looking for an individual bed/room in an already rented flat.\nFinding flatshare has never been easier.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                getEmojiByUnicode(0x1F4AA) + " Build your profile: Your profile is a snapshot of who you are.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                getEmojiByUnicode(0x2705) + " Check Flats: Based on your preferences, we’ll show you flats that you might like to check.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                getEmojiByUnicode(0x1F91D) + " Match and Chat: If the checking is mutual, you can chat with the flatmates and decide if it’s the right flat for you.",
                false
            )
        )



        faqItems.add(
            FaqItem(
                "How do I find a flatmate for my already rented flat?",
                true
            )
        )
        faqItems.add(
            FaqItem(
                "Got a vacant room or bed?\nWith flatshare, you can find flatmates like friends.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                getEmojiByUnicode(0x1F3E0) + " Add your flat: Add your flat by entering your flatname.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                getEmojiByUnicode(0x1F4AA) + " Build your flat profile: Your flat profile includes you, your flatmates, flat photos and other details.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                getEmojiByUnicode(0x2705) + " Check potential flatmates: Based on your details and preferences, we’ll show you potential flatmates who you might like to live with.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                getEmojiByUnicode(0x1F91D) + "  Match and Chat: If the checking is mutual, you can chat with them and decide if they are the right flatmate for you.",
                false
            )
        )



        faqItems.add(
            FaqItem(
                "Can I send a chat request directly and skip the mutual checking?",
                true
            )
        )
        faqItems.add(
            FaqItem(
                "Yes, you can send chat request and skip the mutual checking. Your chat request still has to be accepted for you to connect.",
                false
            )
        )




        faqItems.add(
            FaqItem(
                "Why are there daily limits on Views, Checks and Chat Requests?",
                true
            )
        )
        faqItems.add(
            FaqItem(
                "Our algorithm gives you only the Top Recommendations based on your profile and preferences so that you don't need to find a needle in a haystack. We do it for you.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                "Limits help everyone send and receive the Checks and Chat Requests to the ones they are most interested in. Everyone gets much better quality of Checks and Chat Requests.",
                false
            )
        )




        faqItems.add(
            FaqItem(
                "What does f - FlatScore mean?",
                true
            )
        )
        faqItems.add(
            FaqItem(
                "FlatScore is a measure of community trust. Higher the community trusts you, the higher your score. No spammer, scammer, or fraudster can have a high FlatScore.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                "FlatScore helps you in connecting with people who have FlatScore similar to you and keep spammers, scammers, & fraudsters out of flatshare.",
                false
            )
        )




        faqItems.add(
            FaqItem(
                "How do Invites work?",
                true
            )
        )
        faqItems.add(
            FaqItem(
                "Everybody who joins flatshare can invite a few of their friends. If your friends join you get to invite even more friends.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                "All the invites expire in 48 hours, so make sure your friends know you’ve invited them.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                "You’ll get credit for the invite on their profile and 1000 flatcoins for each successful invite.",
                false
            )
        )




        faqItems.add(
            FaqItem(
                "When should I report someone?",
                true
            )
        )
        faqItems.add(
            FaqItem(
                "We care deeply about our users and strive to make our community safe and comfortable. We will not tolerate any form of abuse or harassment.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                "If you see any member trying to impersonate someone else, asking for brokerage, sending inappropriate messages or photos, go to the bottom of their profile and report.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                "You can also reach out directly to our team directly at hello@flatshare.club and share your feedback. We would love to hear from you.",
                false
            )
        )



        faqItems.add(
            FaqItem(
                "I signed up with the wrong name/birthdate/gender. What should I do?",
                true
            )
        )
        faqItems.add(
            FaqItem(
                "If you signed up with the wrong name, birthdate or gender, reach out to our team directly at hello@flatshare.club along with your mobile number and aadhar card and we’ll change it for you.",
                false
            )
        )



        faqItems.add(
            FaqItem(
                "Can I share feedback or a feature request?",
                true
            )
        )
        faqItems.add(
            FaqItem(
                "Constant evolution is our MO. We're always looking for ways to improve and make flatshare the best it can be. So don't be shy - let us know what you think!",
                false
            )
        )
        faqItems.add(
            FaqItem(
                "If you have any other questions, feedback or want to report a negative experience, please reach out at hello@flatshare.club",
                false
            )
        )

        return faqItems
    }
}