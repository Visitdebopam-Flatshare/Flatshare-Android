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
                "Why did you create Flatshare?",
                true
            )
        )
        faqItems.add(
            FaqItem(
                "Who you live with = Who you'll become.",
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
                "That's why we created flatshare - to make your flat and flatmate search as simple and fun as possible, without you paying any brokerage.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                "So that you can LIVE YOUR BEST LIFE.",
                false
            )
        )


        faqItems.add(
            FaqItem(
                "How does Flatshare help with finding your Perfect Flat & Flatmate?",
                true
            )
        )
        faqItems.add(
            FaqItem(
                "Flatshare uses an intelligent matching algorithm that only gives you the top recommendations based on your profile and preferences.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                "This eliminates the need to search through an overwhelming number of options.\n" +
                        "We do the work for you, making the process more efficient and tailored to your needs.",
                false
            )
        )

        faqItems.add(
            FaqItem(
                "What is FlatScore?",
                true
            )
        )
        faqItems.add(
            FaqItem(
                "Flatscore is a measure of trust within the Flatshare community.\n" +
                        "A higher Flatscore indicates a higher level of trust from the community. ",
                false
            )
        )
        faqItems.add(
            FaqItem(
                "Spammers, scammers, and fraudsters are unlikely to have a high Flatscore.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                "This feature helps you connect with people who have a similar Flatscore, ensuring a safe and trustworthy community.",
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
                "We take the safety and comfort of our members seriously.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                "Any form of abuse, harassment, impersonation, solicitation of brokerage fees, or inappropriate messages or photos should be reported. ",
                false
            )
        )
        faqItems.add(
            FaqItem(
                "Go to the bottom of the person's profile and select 'report'.\n" +
                        "You can also reach out to our team directly at hello@flatshare.club to share your feedback.",
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
                "If you signed up with incorrect personal information, such as name, birth date, or gender, please reach out to our team at hello@flatshare.club with your mobile number and Aadhar card details.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                "We’ll change it for you.",
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
                "Absolutely! We value your feedback and are always striving to improve Flatshare.",
                false
            )
        )
        faqItems.add(
            FaqItem(
                "f you have any suggestions, feature requests, or feedback, please reach out to us at hello@flatshare.club. We'd love to hear from you.",
                false
            )
        )

        return faqItems
    }
}