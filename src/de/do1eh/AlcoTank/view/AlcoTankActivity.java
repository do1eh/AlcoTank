package de.do1eh.AlcoTank.view;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import de.do1eh.AlcoTank.data.Tankstelle;
import de.do1eh.AlcoTank.model.ListenErsteller;
import de.do1eh.AlcoTank.model.WebUtil;

/**
 * Android App zum Abrufen von Ethanol-Tankstellen in Deutschland. Das Programm
 * bestimmt die POsition, berechnet die Postleitzahl und liest die
 * Ethanoltankstellen in 75km Umkreis von www.ethanol-tankde.com aus und stellt
 * sie in einer Liste dar. Sollte keine Postleitzahl bestimmt werden können,
 * wird der Benutzer aufgefordert selbst eine einzugeben. Bei Klick auf einen
 * Listeneintrag kann die Navigation gestartet werden.
 * 
 * @author do1eh
 * @version 1.0 vom 09.03.2012
 * 
 * 
 */
public class AlcoTankActivity extends ListActivity
{

	private List<Tankstelle>	tankstellenliste;
	private Location			location;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getLoc();
	}

	/**
	 * Zeigt die Liste der Tankstellen auf dem Bildschirm an.
	 * 
	 * @param plz
	 */
	private void showList()
	{
		Toast.makeText(this,
				"Tankstellen werden bei www.ethanol-tanken.com gesucht.",
				Toast.LENGTH_LONG).show();
		setListAdapter(new ArrayAdapter(this, R.layout.tankstellenlistitem,
				tankstellenliste));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener()
		{
			/**
			 * Wenn ein Listeneintrag angeklickt wird: starte Navigation.
			 */
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{

				Tankstelle ts = tankstellenliste.get(position);

				Intent i = new Intent(Intent.ACTION_VIEW, Uri
						.parse("google.navigation:q=" + ts.getStrasse()
								+ ts.getPlz() + ts.getOrt()));
				startActivity(i);

			}
		});
	}

	/**
	 * Liest die Location über das Netzwerk aus.
	 */
	private void getLoc()
	{

		Toast.makeText(this, "Bitte warten, Position wird abgerufen",
				Toast.LENGTH_LONG).show();

		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		LocationListener locationListener = new LocationListener()
		{
			/**
			 * Wird aufgerufen wenn das Handy eine neue Location empfängt. Die
			 * Liste wird jedoch nur einmal generiert.
			 */
			public void onLocationChanged(Location location)
			{

				System.out.println("test");
				if (!gotLocation())
				{
					setLocation(location);
					doDownload(getPlz(location));
				}
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras)
			{
			}

			public void onProviderEnabled(String provider)
			{
			}

			public void onProviderDisabled(String provider)
			{
			}
		};

		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}

	/**
	 * Überprüft ob die Location schon ermittelt wurde.
	 * 
	 * @return
	 */
	private boolean gotLocation()
	{
		if (null == this.location)
			return false;
		return true;
	}

	private void setLocation(Location location)
	{
		this.location = location;
	}

	/**
	 * Bestimmt anhand der Längen und Breitengerade die Postleitzahl.
	 * 
	 * @param location
	 * @return
	 */
	private String getPlz(Location location)
	{
		String plz = "";

		Double lat = location.getLatitude();
		Double lng = location.getLongitude();
		Toast.makeText(this, "Postleitzahl wird abgerufen", Toast.LENGTH_LONG)
				.show();

		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		try
		{
			List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
			int i = 0;

			// Der Geocoder gibt evetuell mehrere Adressen zurück. Da nicht
			// immer eine PLZ dabei ist
			// wird so lange gesucht bis eine Postleitzahl gefunden wurde.
			while (i < addresses.size() && (null == plz || plz.length() == 0))
			{
				Address adresse = addresses.get(i);
				plz = adresse.getPostalCode();
				if ((null == plz || plz.length() == 0))
				{
					// Manchmal wird die PLZ zusammen mit der Stadt angegeben.
					if (adresse.getAddressLine(1).substring(0, 5)
							.matches("[0-9]{5}"))
						plz = adresse.getAddressLine(1).substring(0, 5);
				}
				i++;
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return plz;
	}

	/**
	 * Ruft die Erstellung der Liste aus dem heruntergeladenen HTMl auf und
	 * zeigt sie auf dem Bildschirm an.
	 * 
	 * @param html
	 */
	private void getTankstellenList(String html)
	{
		ListenErsteller listenersteller = new ListenErsteller();

		this.tankstellenliste = listenersteller.getTankstellen(html);
		showList();

	}

	/**
	 * Ab Android 3 dürfen Downloads nur im Hintergrund aufgeführt werden. Als
	 * Parameter muss ein String Array übergeben werden [0]=Entfernung [1]=plz
	 */
	private void doDownload(String plz)
	{
		if (null != plz && plz.length() > 0)
		{
			String[] params =
			{ "75", plz };
			new DatenSauger().execute(params);
		} else
		{
			showDialog(7);
		}
	}

	/**
	 * Diese Klasse lädt asynchron die Daten aus dem Internet.
	 * 
	 * @author root
	 * 
	 */
	public class DatenSauger extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... params)
		{
			HashMap<String, String> parameters = new HashMap<String, String>();
			//POST Parameter für die Webseite um die Suche zu starten 
			parameters.put("entfernung", params[0]);
			parameters.put("suche_ort", params[1]);
			String html = "";
			try
			{
				html = WebUtil
						.getPage(
								"http://www.ethanol-tanken.com/index.php?dat=13&suche=1",
								parameters);

			} catch (MalformedURLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return html;

		}

		/**
		 * Wird aufgerufen, wenn das Handy die Daten von der Webseite heruntergeladen hat.
		 */
		@Override
		protected void onPostExecute(String result)
		{
			getTankstellenList(result);
		}

	}

	/**
	 * Zeigt den Dialog zum manuellen Eingeben der PLZ.
	 * @param id
	 * @return
	 */
	@Override
	protected Dialog onCreateDialog(int id)
	{

		final Intent j = new Intent(this, AlcoTankActivity.class);

		switch (id)
		{
		case 7:

			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("PLZ nicht gefunden!");
			alert.setMessage("Bitte PLZ eingeben:");

			final EditText input = new EditText(this);
			alert.setView(input);
			input.setInputType(InputType.TYPE_CLASS_NUMBER);

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
			{
				//Wenn der OK-Button geklickt wird überprüfe ob es sich um eine PLZ handelt.
				public void onClick(DialogInterface dialog, int whichButton)
				{
					String plz = input.getText().toString();
					if (!plz.matches("[0-9]{5}"))
					{
						plz = null;
					}
					doDownload(plz);

				}
			});

			alert.show();

		} // ende case
		return null;
	}

}