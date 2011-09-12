package org.m1cha.android.configurableupdater;

import java.io.BufferedReader;
import android.view.View.OnClickListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.m1cha.android.configurableupdater.customexceptions.Long2IntegerException;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
    	android.app.AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(text);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.lang_alert_buttonOk, listener);
        builder.setNegativeButton(R.string.lang_alert_buttonCancel, listener);
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
}
