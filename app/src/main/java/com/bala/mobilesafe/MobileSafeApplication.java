package com.bala.mobilesafe;

/*import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;*/


import android.app.Application;

/*@ReportsCrashes(
		//10.0.2.2  官方模拟器
		//10.0.3.2 genymotion
        formUri = "http://10.0.3.2:8080/LogServer/LogServlet",  
        mode = ReportingInteractionMode.DIALOG,
        resToastText = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. When defined, adds a user text field input with this text resource as a label
        resDialogEmailPrompt = R.string.crash_user_email_label, // optional. When defined, adds a user email text entry with this text resource as label. The email address will be populated from SharedPreferences and will be provided as an ACRA field if configured.
        resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
    )*/
public class MobileSafeApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		//init ShareSDK
//		ShareSDK.initSDK(this);
		
//		ACRA.init(this);
	}

}
