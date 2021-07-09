package de.do1eh.AlcoTank.data;

/**
 * Klasse um die Daten einer Tankstelle aufzunehmen.
 * @author root
 *
 */
public class Tankstelle
{
 private String name;
 private String strasse;
 private String plz;
 private String ort;
 private String oeffnungszeiten;
 private String preis;
 private String entfernung;
public String getName()
{
	return name;
}
public void setName(String name)
{
	this.name = name;
}
public String getStrasse()
{
	return strasse;
}
public void setStrasse(String strasse)
{
	this.strasse = strasse;
}
public String getPlz()
{
	return plz;
}
public void setPlz(String plz)
{
	this.plz = plz;
}
public String getOrt()
{
	return ort;
}
public void setOrt(String ort)
{
	this.ort = ort;
}
public String getOeffnungszeiten()
{
	return oeffnungszeiten;
}
public void setOeffnungszeiten(String oeffnungszeiten)
{
	this.oeffnungszeiten = oeffnungszeiten;
}
public String getPreis()
{
	return preis.replace('.', ',');
}
public void setPreis(String preis)
{
	this.preis = preis;
}
public String getEntfernung()
{
	return entfernung;
}
public void setEntfernung(String entfernung)
{
	this.entfernung = entfernung;
}

//Text zusammensetzen, der in der Liste angezeigt werden soll.
@Override
public String toString()
{
	return  name + ", " + preis + "€, "+ entfernung;
}
 
 
 
 
 
}
