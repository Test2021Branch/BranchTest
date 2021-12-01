package com.example.branch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;

public class MainActivity extends AppCompatActivity implements Branch.BranchReferralInitListener, View.OnClickListener, Branch.BranchLinkShareListener {

    private static String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Branch.sessionBuilder(this).withCallback(this).withData(getIntent() != null ? getIntent().getData() : null).init();

        findViewById(R.id.share).setOnClickListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null && intent.hasExtra("branch_force_new_session") && intent.getBooleanExtra("branch_force_new_session", false)) {
            Branch.sessionBuilder(this).withCallback(this).reInit();
        }
    }

    // Branch.BranchReferralInitListener
    @Override
    public void onInitFinished(JSONObject linkProperties, BranchError error) {
        if (linkProperties != null) {
            try {
                if (linkProperties.has("deep_link_test")) {
                    String deep_link_test = linkProperties.getString("deep_link_test");
                    Log.d(TAG, "deep_link_test: " + deep_link_test);

                    if (deep_link_test.contains("other")) {
                        Intent intent = new Intent(MainActivity.this, OtherActivity.class);
                        startActivity(intent);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // View.OnClickListener
    @Override
    public void onClick(View view) {
        BranchUniversalObject buo = new BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                .setTitle("My Content Title")
                .setContentDescription("My Content Description")
                .setContentImageUrl("https://lorempixel.com/400/400")
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(new ContentMetadata().addCustomMetadata("key1", "value1"));

        LinkProperties lp = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .setCampaign("content 123 launch")
                .setStage("new user")
                .addControlParameter("$desktop_url", "https://example.com/home")
                .addControlParameter("deep_link_test", "other")
                .addControlParameter("custom_random", Long.toString(Calendar.getInstance().getTimeInMillis()));

        ShareSheetStyle ss = new ShareSheetStyle(MainActivity.this, "Check this out!", "This stuff is awesome: ")
                .setCopyUrlStyle(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
                .setMoreOptionStyle(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_search), "Show more")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.HANGOUT)
                .setAsFullWidthStyle(true)
                .setSharingTitle("Share With");

        buo.showShareSheet(MainActivity.this, lp,  ss,  this);
    }

    @Override
    public void onShareLinkDialogLaunched() {

    }
    @Override
    public void onShareLinkDialogDismissed() {

    }
    @Override
    public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {
        if (error != null) {
            Toast.makeText(this, "LinkShareResponse: " + error.toString(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "LinkShareResponse: " + sharedLink, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onChannelSelected(String channelName) {

    }
}