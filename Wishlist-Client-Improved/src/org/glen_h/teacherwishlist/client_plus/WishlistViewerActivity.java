package org.glen_h.teacherwishlist.client_plus;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class WishlistViewerActivity extends Activity {
    
	private Spinner wishlist_choose;
	private Button showlist;
	
	/**
	 * Downloads a text file and returns its contents as an array.
	 * @author Glen Husman
	 * @throws IOException 
	 */
	public static String[] downloadFile(URL website) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(
			          new InputStreamReader(
			          website.openStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	      String input;
	      ArrayList<String> stringList = new ArrayList<String>();
	      	                
	      try {
			while ((input = in.readLine()) != null) {
			      stringList.add(input);
			  }
		} catch (IOException e) {
			stringList = new ArrayList<String>();
			}
	      
	    String[] itemArray = new String[stringList.size()];
		String[] returnedArray = stringList.toArray(itemArray);
		return returnedArray;
		}
	
	/**
	 * Makes a URL from a string without the need for a try/catch.
	 * @see java.net.URL URL
	 * @author Glen Husman
	 * @return URL
	 */
	public static URL makeURL(String webaddress) {
		
		/*
		 * Makes a URL from a string
		 */
		
		URL website;
		try {
			website = new URL(webaddress);
		} catch (MalformedURLException e) {
			website = null;
			Log.e("URL", "Malformed URL Exception was thrown on string to URL conversion");
		}
	return website;
	}
	
	public static void postData(String url, String[] ids, String[] values) {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(url);

	    try {
	        if(ids.length-1 == values.length-1){
	    	// Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(ids.length-1);
	        for(int ii = 0; ii <= ids.length-1; ii++){
	        nameValuePairs.add(new BasicNameValuePair(ids[ii], values[ii]));
	        }
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        httpclient.execute(httppost);
	        }else{
	        	return;
	        }
	    } catch (ClientProtocolException e) {
	    	return;
	    } catch (IOException e) {
	    	return;
	    }
	} 
	
	protected String instance_url;
	private int itemid_dialog;
	private String[] wlist;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        wishlist_choose = (Spinner) findViewById(R.id.wishlist);
        showlist = (Button) findViewById(R.id.showlist);
        final EditText input = new EditText(WishlistViewerActivity.this);
        input.setText("http://192.168.1.101/teacher-wishlist/wishlist-edit.php");
        input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_URI);
        new AlertDialog.Builder(WishlistViewerActivity.this)
        .setTitle("Instance URL")
        .setMessage("Please input the URL to the wishlist-edit.php file")
        .setView(input)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable value = input.getText();
                instance_url = value.toString();
                String[] wishlists = downloadFile(makeURL(instance_url+"?listwishlist=ok"));
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(WishlistViewerActivity.this, 
                		android.R.layout.simple_spinner_item, wishlists);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                wishlist_choose.setAdapter(spinnerArrayAdapter);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        }).show();
        showlist.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	wlist = downloadFile(makeURL(instance_url.replace("wishlist-edit.php", "wishlist-extension.php?extension=view&wishlist="+(String) wishlist_choose.getSelectedItem()+"&plain=yes")));
            	/**
            	AlertDialog.Builder builder = new AlertDialog.Builder(WishlistViewerActivity.this);
                builder.setTitle("Wishlist for: "+(String) wishlist_choose.getSelectedItem());

                ListView modeList = new ListView(WishlistViewerActivity.this);
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(WishlistViewerActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, wlist);
                modeList.setAdapter(modeAdapter);

                builder.setView(modeList);
                final Dialog dialog = builder.create();

                dialog.show();
                */
            	/**
                AlertDialog.Builder ab=new AlertDialog.Builder(WishlistViewerActivity.this);
                ab.setTitle("Title");
                ab.setSingleChoiceItems(wlist, 0,new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }})
                .setPositiveButton("I bought this!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(WishlistViewerActivity.this, "", Toast.LENGTH_SHORT).show();
                }
                })
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
                });
                ab.show();
                */
            	AlertDialog.Builder builder = new AlertDialog.Builder(WishlistViewerActivity.this);
            	builder.setTitle("Wishlist for "+(String) wishlist_choose.getSelectedItem());
            	builder.setItems(wlist, new DialogInterface.OnClickListener() {
            	    public void onClick(DialogInterface dialog, int item) {
            	    	itemid_dialog = item;
            	    	AlertDialog.Builder builder = new AlertDialog.Builder(WishlistViewerActivity.this);
            	    	builder.setMessage("Did you buy "+wlist[item]+" for "+(String) wishlist_choose.getSelectedItem()+"?")
            	    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            	    	           public void onClick(DialogInterface dialog, int id) {
            	    	        	   // Mark as bought
            	    	        	   String[] post_vars = {"extension", "wishlist", "item"};
            	    	        	   String[] post_data = {"bought", (String) wishlist_choose.getSelectedItem(), wlist[itemid_dialog]};
            	    	        	   postData(instance_url.replace("wishlist-edit.php", "wishlist-extension.php"), post_vars, post_data);
            	    	           }
            	    	       })
            	    	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
            	    	           public void onClick(DialogInterface dialog, int id) {
            	    	                dialog.cancel();
            	    	           }
            	    	       });
            	    	builder.show();
            	    }
            	});
            	builder.show();
              }
        });
    }
}