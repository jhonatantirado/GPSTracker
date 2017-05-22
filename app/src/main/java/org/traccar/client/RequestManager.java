/*
 * Copyright 2015 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.client;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestManager {

    private static final int TIMEOUT = 15 * 1000;

    public interface RequestHandler {
        void onComplete(boolean success);
    }

    private static class RequestAsyncTask extends AsyncTask<String, Void, Boolean> {

        private RequestHandler handler;

        public RequestAsyncTask(RequestHandler handler) {
            this.handler = handler;
        }

        @Override
        protected Boolean doInBackground(String... request) {
            return sendRequest(request[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            handler.onComplete(result);
        }
    }

    public static boolean sendRequest(String request) {
        InputStream inputStream = null;
        try {
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(TIMEOUT);
            connection.setConnectTimeout(TIMEOUT);
            connection.connect();
            inputStream = connection.getInputStream();
            while (inputStream.read() != -1);
            return true;
        } catch (IOException error) {
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException secondError) {
                return false;
            }
        }
    }

    public static String postRequest (String request, CellTowerInformation cellTowerInformation){
        String result = null;
        try{
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(TIMEOUT);
            connection.setConnectTimeout(TIMEOUT);
            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("charset","UTF-8");
            connection.connect();

            JSONObject postData = postData (cellTowerInformation);

            DataOutputStream wr = new DataOutputStream( connection.getOutputStream());
            //wr.write(postData);

        } catch (IOException error) {
            return result;
        }

        return result;
    }

    public static JSONObject postData (CellTowerInformation cellTowerInformation)
    {
        JSONObject jsonParam = new JSONObject();

        try
        {
            jsonParam.put("radioType", cellTowerInformation.getRadioType());
            jsonParam.put("mobileCountryCode",cellTowerInformation.getMobileCountryCode());
            jsonParam.put("mobileNetworkCode",cellTowerInformation.getMobileNetworkCode());
            jsonParam.put("locationAreaCode",cellTowerInformation.getLocationAreaCode());
            jsonParam.put("cellId",cellTowerInformation.getCellId());
        }
        catch (Exception ex)
        {
            Log.d("Exception","Not supported");
        }

        return jsonParam;
    }

    public static void sendRequestAsync(String request, RequestHandler handler) {
        RequestAsyncTask task = new RequestAsyncTask(handler);
        task.execute(request);
    }

}
