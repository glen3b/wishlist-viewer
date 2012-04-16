/*  Wishlist Viewer - View online wishlists from your android phone or tablet
    Copyright (C) 2012  Glen Husman

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.glen_h.teacherwishlist.client_plus;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
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
import android.content.SharedPreferences;
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
	
	
	void makeSimpleConfirmDialog(String title, String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if(message != null) builder.setMessage(message);
		if(title != null) builder.setTitle(title);
		builder.setCancelable(true);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		builder.show();
	}
	
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
	
	/**
	 * Post data to a URL.
	 * @author Glen Husman
	 * @param url The URL to post data to.
	 * @param ids The array of IDs of POST variables.
	 * @param values The array of values of POST variables.
	 * @return The HTTP status code of the request.
	 */
	public static int postData(String url, String[] ids, String[] values) {
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
	        HttpResponse result = httpclient.execute(httppost);
	        return result.getStatusLine().getStatusCode();
	        }else{
	        	return -1;
	        }
	    } catch (ClientProtocolException e) {
	    	return -1;
	    } catch (IOException e) {
	    	return -1;
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
        Button chdefault = (Button) findViewById(R.id.changedefault);
        final SharedPreferences data = getSharedPreferences("Wishlist_Cloud_Editor", 0);
        if(data.getString("instance_url", null) == null){
        final EditText input = new EditText(this);
        // input.setText("http://192.168.1.101/teacher-wishlist/wishlist-edit.php");
        input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_URI);
        new AlertDialog.Builder(this)
        .setTitle("Instance URL")
        .setMessage("Please input the URL to the wishlist-edit.php file you want to use")
        .setView(input)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable value = input.getText();
                instance_url = value.toString();
                SharedPreferences.Editor dataedit = data.edit();
                dataedit.putString("instance_url", instance_url);
                dataedit.commit();
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
        }else{
        	instance_url = data.getString("instance_url", null);
            String[] wishlists = downloadFile(makeURL(instance_url+"?listwishlist=ok"));
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(WishlistViewerActivity.this, 
            		android.R.layout.simple_spinner_item, wishlists);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            wishlist_choose.setAdapter(spinnerArrayAdapter);
        }
        chdefault.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	final EditText input = new EditText(WishlistViewerActivity.this);
                input.setText(data.getString("instance_url", null));
                input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_URI);
                new AlertDialog.Builder(WishlistViewerActivity.this)
                .setTitle("Instance URL")
                .setMessage("Please input the URL to the wishlist-edit.php file you want to use")
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        instance_url = value.toString();
                        SharedPreferences.Editor dataedit = data.edit();
                        dataedit.putString("instance_url", instance_url);
                        dataedit.commit();
                        String[] wishlists = downloadFile(makeURL(instance_url+"?listwishlist=ok"));
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(WishlistViewerActivity.this, 
                        		android.R.layout.simple_spinner_item, wishlists);
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        wishlist_choose.setAdapter(spinnerArrayAdapter);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();
              }
        });
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
            	    	        	   int status = postData(instance_url.replace("wishlist-edit.php", "wishlist-extension.php"), post_vars, post_data);
           	    					   if(status == -1){
           	    						// Function error
           	    						makeSimpleConfirmDialog("ERROR", "An error occurred sending the request.");
           	    					   }else if(status == 500){
           	    						// Web script error
           	    						makeSimpleConfirmDialog("ERROR", "An error occurred in the script. Please contact the server administrator.");
           	    					}else if(status < 400){
           	    						makeSimpleConfirmDialog("Success", "Marking "+wlist[itemid_dialog]+" as bought was successful.\nHTTP Status Code: "+status);
           	    					}else if(status >= 400){
           	    						makeSimpleConfirmDialog("ERROR", "An error occured while marking "+wlist[itemid_dialog]+" as bought.\nHTTP Status Code: "+status);
           	    					}
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