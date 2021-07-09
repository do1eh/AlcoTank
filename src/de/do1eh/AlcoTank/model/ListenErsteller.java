package de.do1eh.AlcoTank.model;



import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.os.AsyncTask;

import de.do1eh.AlcoTank.data.Tankstelle;

/**
 * Klasse zum extrahieren von Tankstellenobjekten aus HTML-Code von www.ethanol-tanken.com
 * @author root
 *
 */
public class ListenErsteller 
{
	

	public List<Tankstelle> getTankstellen(String html)
	{
		List<Tankstelle> tankstellenliste=new LinkedList();
		
		
		
		//Als erstes die Tabelle aus dem HTML ausschneiden, die die Tankstellen enthält
		String tabelle=html.substring(html.indexOf("<td>Entfernung</td></tr>")+25, html.indexOf("var geocoder;")-75);
		
		
		
		tabelle=tabelle.trim();
		Tankstelle tankstelle;
		while(tabelle.length()>0)
		{
			
			//Bei jeder neuen Tabellenzeile neues Tankstellenobjekt erzeugen, Daten aus den
			//Spalten zuweisen und das Objekt in der Liste speichern.
			if(tabelle.startsWith("<tr><td>"))
			{
				
				tankstelle=new Tankstelle();
				
				//name
				tankstelle.setName(tabelle.substring(8, tabelle.indexOf("</td><td>")));
				tabelle=tabelle.substring(tabelle.indexOf("</td><td>")+9);
				//plz
				tankstelle.setPlz(tabelle.substring(0, tabelle.indexOf("</td><td>")));
				tabelle=tabelle.substring(tabelle.indexOf("</td><td>")+9);
				//ort
				tankstelle.setOrt(tabelle.substring(0, tabelle.indexOf("</td><td>")));
				tabelle=tabelle.substring(tabelle.indexOf("</td><td>")+9);
				//strasse
				tankstelle.setStrasse(tabelle.substring(0, tabelle.indexOf("</td><td>")));
				tabelle=tabelle.substring(tabelle.indexOf("</td><td>")+9);
				//öffnungszeiten
				tankstelle.setOeffnungszeiten(tabelle.substring(0, tabelle.indexOf("</td><td>")));
				tabelle=tabelle.substring(tabelle.indexOf("</td><td>")+9);
				//preis
				tankstelle.setPreis(tabelle.substring(0, tabelle.indexOf("</td><td>")));
				tabelle=tabelle.substring(tabelle.indexOf("</td><td>")+9);
				//entfernung
				tankstelle.setEntfernung(tabelle.substring(0, tabelle.indexOf("</td><td>")));
				if (tabelle.indexOf("<tr><td>")>0)
					{
					  tabelle=tabelle.substring(tabelle.indexOf("<tr><td>"));
					}
				else
				{
					tabelle="";
				}
			tankstellenliste.add(tankstelle);	
			}
			
		}
		
		
		return tankstellenliste;
	}

	
	
	
	

	
}
