/***

The MIT License (MIT)
Copyright � 2014 Rutvijkumar Shah
 
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the �Software�), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
 
The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.
 
THE SOFTWARE IS PROVIDED �AS IS�, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

***/

package codepath.watsiapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.widget.TextView;
import codepath.watsiapp.R;
import codepath.watsiapp.activities.ParseDispatchActivity;
import codepath.watsiapp.models.MedicalPartner;
import codepath.watsiapp.models.Patient;

public class Util {

	private static final String DATE_FORMAT = "MMM dd yyyy";
	private static String PRIMARY_FONT="Roboto-Regular.ttf";

	
	public static void startFundTreatmentIntent(Activity activity,Patient patient) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(patient.getProfileUrl()));
		activity.startActivity(browserIntent);
	}
	
	/**
	  * Sets a hyperlink style to the textview.
	  */
	public static void makeTextViewHyperlink(TextView tv) {
	  SpannableStringBuilder ssb = new SpannableStringBuilder();
	  ssb.append(tv.getText());
	  ssb.setSpan(new URLSpan("#"), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	  tv.setText(ssb, TextView.BufferType.SPANNABLE);
	} 
	
	public static void starShowMedicalPartnerIntent(Activity activity,MedicalPartner medicalPartner) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(medicalPartner.getWebsiteUrl()));
		activity.startActivity(browserIntent);
	}
	public static void showMyProfileActivity(FragmentActivity activity) {
		
		activity.startActivity(new Intent(activity,ParseDispatchActivity.class));
	}
	public static void startShareIntent(Activity activity,Patient patient) {
		Intent shareIntent = new Intent();
	    shareIntent.setAction(Intent.ACTION_SEND);
	    shareIntent.putExtra(Intent.EXTRA_TEXT, patient.getProfileUrl());
	    shareIntent.setType("text/plain");
	    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Fund Treatment");
	    activity.startActivity(Intent.createChooser(shareIntent, "Share Story"));		
	}
	
	public static Boolean isNetworkAvailable(Context activity) {
		ConnectivityManager connectivityManager = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		boolean isNetworkAvailable = true;
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		isNetworkAvailable = (activeNetworkInfo != null && activeNetworkInfo
				.isConnectedOrConnecting());
		return isNetworkAvailable;
	}
	
	public static String getFormatedDate(Date date) {
		
		SimpleDateFormat sf = new SimpleDateFormat(DATE_FORMAT);
		sf.setLenient(true);
		return sf.format(date);
		
	}
	
	public static void applyPrimaryFont(Context ctx,TextView textView) {	
		Typeface typeface = Typeface.createFromAsset(ctx.getAssets(),"fonts/"+PRIMARY_FONT);
		textView.setTypeface(typeface);
	}
}
	