

package de.do1eh.AlcoTank.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Diese Klasse handelt die Web-Zugriffe ab
 *
 * Copyright (C) 2009 Jan-Hendrik Kossow (DK7JAN)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Jan-Hendrik Kossow
 */
public class WebUtil {
    
    /**
     * Fragt den Inhalt einer Internetseite ab
     * @param url String der Internetadresse (z.B. https://www.test.de)
     * @return Inhalt der Internetseite als String
     * @throws java.net.MalformedURLException
     * @throws java.io.IOException
     * @throws java.net.UnknownHostException 
     */
    public static String getPage(String url) throws MalformedURLException, IOException, java.net.UnknownHostException {
        URL adress = new URL(url);
        HttpURLConnection con = (HttpURLConnection) adress.openConnection();
        InputStream in = con.getInputStream();
        InputStreamReader isr = new InputStreamReader(in,"UTF-8");
        StringWriter out = new StringWriter();
        int temp = -1;
        while( (temp = isr.read()) != -1) {
            out.append((char)temp);
        }
        con.disconnect();
        return out.toString();
    }
    
    /**
     * Fragt den Inhalt einer Internetseite mit Parametern per POST-Methode ab.
     * @param url String der Internetadresse (z.B. https://www.test.de)
     * @param parameters key&values der Parameter
     * @return Inhalt der Internetseite als String
     * @throws java.net.MalformedURLException
     * @throws java.io.IOException
     */
    public static String getPage(String url, HashMap parameters) throws MalformedURLException, IOException {
        
        URL adress = new URL(url);
        HttpURLConnection con = (HttpURLConnection) adress.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        
        
        String[] keys = new String[parameters.size()];
        String[] values = new String[parameters.size()];
        int keysIndex = 0;
        String data = "";
        Iterator parametersIterator = parameters.keySet().iterator();
        while(parametersIterator.hasNext()) {
            keys[keysIndex] = (String) parametersIterator.next();
            values[keysIndex] = (String) parameters.get(keys[keysIndex]);
            
            try{
                data += URLEncoder.encode(keys[keysIndex], "UTF-8") + "=" + 
                        URLEncoder.encode(values[keysIndex], "UTF-8");
                data += parametersIterator.hasNext() ? "&" : "";
            } catch(NullPointerException e) {
               
            }
            keysIndex++;
        }
        con.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        wr.write(data);
        wr.flush();

        InputStream in = con.getInputStream();
        InputStreamReader isr = new InputStreamReader(in,"UTF-8");
        StringWriter out = new StringWriter();
        int temp = -1;
        while( (temp = isr.read()) != -1) {
            out.append((char)temp);
        }
        con.disconnect();
        return out.toString();
    }
}
