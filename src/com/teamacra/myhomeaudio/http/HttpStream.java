package com.teamacra.myhomeaudio.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.SharedPreferences;

import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.StreamInterface;

public class HttpStream extends HttpBase implements StreamInterface {
	
	public HttpStream(MHAApplication app) {
		super(app);
	}
	
	public String[] getMediaList() {
		String[] result = new String[0];
		
		try {
			String url = this.host+"/song/list";
			
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			
			if (entity != null)
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				StringBuilder stringBuilder = new StringBuilder();
				
				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
						stringBuilder.append(line + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				try {
					JSONArray array = new JSONArray(stringBuilder.toString());
					result = new String[array.length()];
					for (int i=0;i<array.length();i++) {
						result[i] = array.getString(i);
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			result = null;
		} catch(IllegalArgumentException e){
			e.printStackTrace();
			result = null;
		}
		return result;
		
	}
	
	@Override
	public String getStreamName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getArtist() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean play(String name) {
		HttpPost httpPost = new HttpPost(this.host+"/song/play");
		try {
			//List<NameValuePair> postVars = new ArrayList<NameValuePair>(1);
			//postVars.add(new BasicNameValuePair("media", name));
			
			httpPost.setEntity(new StringEntity("{\"song\":\""+name+"\"}\r\n\r\n"));
			
			HttpResponse response = this.httpClient.execute(httpPost);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean pause() {
		HttpGet httpGet = new HttpGet(this.host+"/song/pause");
		try {
			
			HttpResponse response = this.httpClient.execute(httpGet);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean next() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean prev() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean play() {
		// TODO Auto-generated method stub
		return false;
	}

}
