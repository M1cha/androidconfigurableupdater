package org.m1cha.android.configurableupdater;

import java.io.BufferedReader;
import android.view.View.OnClickListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.m1cha.android.configurableupdater.activities.MainPreferenceActivity;
import org.m1cha.android.configurableupdater.activities.ManualActivity;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

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
    
    public static String getStringFromUrl(String uri) throws Exception {
    	
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(uri));
			HttpResponse response = client.execute(request);
			InputStream ips  = response.getEntity().getContent();
			BufferedReader buf = new BufferedReader(new InputStreamReader(ips,"UTF-8"));
			
			StringBuilder sb = new StringBuilder();
			String s;
	        while(true )
	        {
	            s = buf.readLine();
	            if(s==null || s.length()==0)
	                break;
	            sb.append(s);

	        }
			buf.close();
			ips.close();
			return sb.toString();
		
		} 
		finally {
		}
	} 
    
    public static int dipToPixel(Context context, float dipValue) {
    	float scale = context.getResources().getDisplayMetrics().density;
    	return (int) (dipValue * scale + 0.5f);
    }
    
    /**
     * @deprecated
     */
    public static LinearLayout getPrefRoot(Window window) {
    	ViewGroup vg = (ViewGroup)window.getDecorView();
		LinearLayout l = (LinearLayout) vg.getChildAt(0);
		return l;
    }
    
    public static LinearLayout patchListView(ListView listView) {
		
		/** get frame-layout */
		FrameLayout frameLayout = (FrameLayout)listView.getParent();
		
		/** remove listView from frameLayout */
		frameLayout.removeView(listView);
		
		/** create main layout and add it to the frameLayout */
		LinearLayout mainLayout = new LinearLayout(listView.getContext());
		mainLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setBackgroundDrawable(listView.getBackground());
		frameLayout.addView(mainLayout);
		
		/** create layoutParams for row1 */
	    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	    layoutParams.weight = 1.0f;
		
		/** create first row and add it to the main-layout */
	    LinearLayout row1 = new LinearLayout(listView.getContext());
	    row1.setLayoutParams(layoutParams);
	    mainLayout.addView(row1);
	    
	    /** add listView to the first row */
	    row1.addView(listView);
	    
	    /** create second row and add it to the main-layout */
	    LinearLayout row2 = new LinearLayout(listView.getContext());
	    row2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	    mainLayout.addView(row2);
	    
	    /** return second row */
	    return row2;
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
    	
    	/** get default text-color */
    	int textColor = new Button(context).getCurrentTextColor();
    	
    	
    	/** set content */
    	WebView webview = (WebView) popup.findViewById(R.id.popup_webView);
    	webview.setBackgroundColor(Color.TRANSPARENT);
    	webview.loadData("<style>*{color:rgb("+Color.red(textColor)+","+Color.green(textColor)+","+Color.blue(textColor)+");}</style>"+content, "texl/html", "utf-8");
    	
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
    	
    		case R.id.menuMain_itemManual:
    			Intent iManual = new Intent(context, ManualActivity.class);
    			context.startActivity(iManual);
    		break;
    	
			case R.id.menuMain_itemSettings:
				Intent iSettings = new Intent(context, MainPreferenceActivity.class);
				context.startActivity(iSettings);
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
    
    public static void rebootPhone() {
    	Process p;
		try {
			/** gain superuser shell */
			p = Runtime.getRuntime().exec("su");
			OutputStream os = p.getOutputStream();
			
			/** run script from customisation-folder */
			// don't know why but it does not work
			/*File rebootScript = getCustomFile("reboot.sh");
			if(rebootScript.canExecute()) {;
				os.write(("sh "+rebootScript.getAbsolutePath()).getBytes());
			}
			
			else */if(android.os.Build.MODEL.equals("MB525")) {
				
				/** NEW reboot-method for defy */
				if(Util.getProperty("ro.build.newrecoveryreboot").equals("1")) {
					os.write("reboot recovery\n".getBytes());
				}
				
				/** OLD reboot-method for defy */
				else {
					/** set bootmode to recovery */
	    			os.write("mkdir -p /cache/recovery/\n".getBytes());
	                os.write("echo 'recovery' >/cache/recovery/bootmode.conf\n".getBytes());
	            	
	            	/** reboot */
	                os.write("reboot\n".getBytes());
				}
            	
            }
			
			/** standard reboot-method for all devices */
            else {
            	os.write("reboot recovery\n".getBytes());
            }
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static ArrayList<MountPoint> getMountPoints() {
    	ArrayList<MountPoint> mountPoints = new ArrayList<MountPoint>();
    	
    	try {
			/** open mounts-file */
			BufferedReader reader = new BufferedReader(new FileReader("/proc/mounts"));
			
			String line;
	        while ((line = reader.readLine()) != null) {
	        	
	        	/** split line into single parts */
	        	String[] parts = line.split(" +");
	        	
	        	/** continue if we don't have enough info */
	        	if(parts.length<3) continue;
	        	
	        	/** get info */
	        	String device = parts[0];
	        	String mountPoint = parts[1];
	        	String fsType = parts[2];
	        	
	        	/** add to arrayList */
	        	mountPoints.add(new MountPoint(device, mountPoint, fsType));
	        }
		} catch (FileNotFoundException e) {
			Logger.debug("Unable to open Mount-File", e);
		} catch (IOException e) {
			Logger.debug("IO-Error in getMountPoints()", e);
		}
    	
    	return mountPoints;
    }
    
    public static String getDefaultRomFolder(Context context) {
    	
    	/** look if a defaultRomFolder was set in advancedSettings */
		try {
			if(DataStore.advancedSettings.has("default_romFolder")) {
				return DataStore.advancedSettings.getString("default_romFolder");
			}
		} catch (JSONException e) {
			Logger.debug("advanced_defaultRomFolder", e);
		}
		
		/** else return internal defaultRomFolder */
    	return context.getString(R.string.default_romFolder);
    }
    
    public static File getExternalStorage(String path) {
    	
    	/** for LG Optimus Speed */
    	if(android.os.Build.MODEL.equals("Optimus 2X")) {
    		
    		/** get mountPoints */
	    	ArrayList<MountPoint> mountPoints = Util.getMountPoints();
	    	
	    	/** iterate over mountPoints */
	        for(int i=0; i<mountPoints.size(); i++) {
	        	
	        	/** get current mointPoint */
	        	MountPoint mountPoint = mountPoints.get(i);
	        	
	        	/** check if current mountPoint is the sdcard and NOT asec-mount */
	        	if(mountPoint.getDevice().equals("/dev/block/vold/179:17") && !mountPoint.getMountPoint().equals("/mnt/secure/asec")) {
	        		return new File(mountPoint.getMountPoint()+"/"+path);
	        	}
	        }
    	}
    	
    	/** for Google Nexus S */
    	if(android.os.Build.MODEL.equals("Nexus S")) {
    		return new File("/"+path);
    	}
    	
    	/** for standard-devices */
    	return new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+path);
    }
    
    public static JSONObject getAdvancedSettingsFile() {

    	/** try to load file */
        try {
        	/** parse and return JSON-Object of file */
			return new JSONObject(Util.getStreamData(new FileInputStream(Util.getCustomFile("advanced.json"))));
		} catch (FileNotFoundException e) {
			Logger.debug("advancedJSON", e);
		} catch (JSONException e) {
			Logger.debug("advancedJSON", e);
		} catch (IOException e) {
			Logger.debug("advancedJSON", e);
		}
        
        /** return new JSON-Object */
        return new JSONObject();
    }
    
    public static String getProperty(String name) { 
    	/** string for result */
    	String result="";
    	
		try 
		{
			/** string for one line */
			String line;
			
			/** execute command */
			java.lang.Process p = Runtime.getRuntime().exec("getprop "+name);
			
			/** get inputstream */
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			/** read all lines */
			while ((line = input.readLine()) != null) 
			{ 
				result+=line;
			}
			
			/** close stream */
			input.close(); 
		} 
		catch (Exception e) 
		{ 
			Logger.debug("failed getting prop: "+name, e);
		}
		
		/** return result */
		return result;
	} 
}
