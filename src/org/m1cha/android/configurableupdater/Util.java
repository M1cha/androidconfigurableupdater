package org.m1cha.android.configurableupdater;

import java.io.BufferedReader;
import android.view.View.OnClickListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.m1cha.android.configurableupdater.customexceptions.Long2IntegerException;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class Util {

	/**
	 * Function for creating an alert-dialog with message and OK-button
	 * @param context
	 * @param text
	 */
    public static void alert(Context context, String text) {
    	android.app.AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(text);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.lang_alert_buttonOk, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    
    public static void alertOkCancel(Context context, String text, DialogInterface.OnClickListener listener) {
        Util.alertCustom(context, text, R.string.lang_alert_buttonOk, R.string.lang_alert_buttonCancel, listener);
    }
    
    public static void alertCustom(Context context, String text, int posButtonTextId, int negButtonTextId, DialogInterface.OnClickListener listener) {
    	android.app.AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(text);
        builder.setCancelable(true);
        builder.setPositiveButton(posButtonTextId, listener);
        builder.setNegativeButton(negButtonTextId, listener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    
    /**
     * Function for loading a complete InputStream-Resource into String
     * @param InputStream is
     * @return
     * @throws IOException
     */
    public static String getStreamData(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[is.available()];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
    
    public static int dipToPixel(Context context, float dipValue) {
    	float scale = context.getResources().getDisplayMetrics().density;
    	return (int) (dipValue * scale + 0.5f);
    }
    
    public static LinearLayout getPrefRoot(Window window) {
    	ViewGroup vg = (ViewGroup)window.getDecorView();
		LinearLayout l = (LinearLayout) vg.getChildAt(0);
		return l;
    }
    
    public static String getLayoutNameById(int id) {
    	/** get all layout-ids */
    	Field[] fields = R.layout.class.getDeclaredFields();
    	
    	/** iterate over all layout-ids */
		for (Field field : fields) {
			try {
				/** if current field-id is the wanted one */
				if(field.getInt(null)==id) {
					/** return the name of the layout */
					return field.getName();
				}
			}
			catch (IllegalArgumentException e) {} 
			catch (IllegalAccessException e) {}
		}
		
		return null;
    }
    
    public static String getTitleByLayoutId(Context context, int id) {
    	/** get all string-ids */
    	Field[] fields = R.string.class.getDeclaredFields();
    	
    	/** get layout-name */
    	String layoutName = Util.getLayoutNameById(id);
    	
    	/** iterate over all string-ids */
    	for (Field field : fields) {
    		/** if current field is the title for our layout */
    	    if(field.getName().equals("lang_"+layoutName+"_title")) {
	    	    try {
	    	    	/** get title via the string-id */
	    	    	return context.getString(field.getInt(null));
				} 
				catch (IllegalArgumentException e) {return null;} 
	    	    catch (IllegalAccessException e) {return null;}
    	    }
    	}
    	return null;
    }
    
    public static String getEntryContentAsString(ZipFile zipFile, ZipEntry entry) throws IOException, Long2IntegerException {
    	/** open info-file */
		InputStream is = zipFile.getInputStream(entry);
		InputStreamReader isr = new InputStreamReader(is);
		
		/** get file-size (as integer) */
		if(entry.getSize()>Integer.MAX_VALUE) {
			throw(new Long2IntegerException());
		}
		int size = Integer.parseInt(Long.toString(entry.getSize()));
		
		/** read version-File */
		String result = "";
		char[] buffer = new char[size];
        while (isr.read(buffer, 0, buffer.length) != -1)
        {
            String s = new String(buffer);
            result+=s;
        }
        
        /** close streams */
        isr.close();
        is.close();
        
        /** return */
        return result;
    }
    
    private static Dialog popup;
    public static void showPopup(Context context, CharSequence title, String content) {
    	
    	/** create dialog */
    	popup = new Dialog(context);
    	popup.setTitle(title);
    	popup.setCancelable(true);
    	popup.getContext().getTheme().applyStyle(R.style.popup_title, false);
    	popup.setContentView(R.layout.popup);
    	
    	/** set content */
    	WebView webview = (WebView) popup.findViewById(R.id.popup_webView);
    	webview.setBackgroundColor(Color.TRANSPARENT);
    	webview.loadData(content, "texl/html", "utf-8");
    	
    	/** set onclick-Listener */
    	popup.findViewById(R.id.popup_buttonClose).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popup.dismiss();
			}
		});
    	
    	/** show dialog */
    	popup.show();
    }
    
    private static Context contextFeedback;
    public static void menuHandler(Context context, MenuItem item) {
    	switch(item.getItemId()) {
			case R.id.menuMain_itemSettings:
				Intent i = new Intent(context, MainPreferenceActivity.class);
				context.startActivity(i);
			break;
			
			case R.id.menuMain_itemFeedback:
				
				/** save context for onclickhandler */
				contextFeedback = context;
				
				/** show message */
				Util.alertCustom(context, context.getString(R.string.lang_alert_FeedbackText), R.string.lang_alert_buttonIssue, R.string.lang_alert_buttonFeedback, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which) {

							/** issue */
							case DialogInterface.BUTTON_POSITIVE:
								Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://code.google.com/p/androidconfigurableupdater/issues/list"));
								contextFeedback.startActivity(browserIntent);
							break;
							
							/** feedback */
							case DialogInterface.BUTTON_NEGATIVE:
								/** receiver */
								String[] mailto = { "m1cha-dev@web.de" };
								
							    /** create intent */
							    Intent sendIntent = new Intent(Intent.ACTION_SEND);
							    
							    /** set attributes */
							    sendIntent.putExtra(Intent.EXTRA_EMAIL, mailto);
							    sendIntent.putExtra(Intent.EXTRA_SUBJECT, contextFeedback.getString(R.string.lang_menuMain_itemFeedSubject));
							    sendIntent.putExtra(Intent.EXTRA_TEXT, "");
							    sendIntent.setType("text/plain");
							    
							    /** start */
							    contextFeedback.startActivity(Intent.createChooser(sendIntent, contextFeedback.getString(R.string.lang_menuMain_itemFeedSubject)));
							break;
						}
					}
				});
			break;
			
			case R.id.menuMain_itemAbout:
				/** load about-text */
		    	String text = "";
		    	try {
		    		InputStream stream = context.getResources().openRawResource(R.raw.about);
					text = Util.getStreamData(stream);
				} catch (IOException e) {
					Util.alert(context, context.getString(R.string.lang_error_uncaughtException));
				}
		    	
		    	/** show popup */
				Util.showPopup(context, context.getString(R.string.lang_menuMain_itemAbout), text);
				
			break;
		}
    }
    
    private static String CUSTOMFILESPATH = "androidconfigurableupdater/";
    public static File getCustomFile(String filepath) {
    	
    	/** get language-code */
    	String langCode = Locale.getDefault().getLanguage();
    	if(langCode.length()>0) {
    		langCode = "-"+langCode;
    	}
    	
    	/** get root-directory */
		File root = Environment.getRootDirectory();
    	
    	/** get object for new file */
    	File customFile = new File(root.getAbsolutePath()+"/"+CUSTOMFILESPATH+"raw"+langCode+"/"+filepath);
    	
    	/** load default-folder if the localized cannot be loaded */
    	if(!customFile.canRead()) {
    		customFile = new File(root.getAbsolutePath()+"/"+CUSTOMFILESPATH+"raw/"+filepath);
    	}
    	
    	/** return */
    	return customFile;
    }
}
