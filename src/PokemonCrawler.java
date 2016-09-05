/**
 * Description:
 * @author: Philip Yoo (philipsdyoo)
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PokemonCrawler {

    public static void main(String[] args) {
	String startURL = "http://bulbapedia.bulbagarden.net/wiki/List_of_Pok%C3%A9mon_by_National_Pok%C3%A9dex_number";
	PrintWriter pokemonCSV = null;
	try {
	    pokemonCSV = new PrintWriter("pokemon.csv", "UTF-8");
	    pokemonCSV.println("Name, Species, Type(s), HP, Attack, Defense, Sp. Attack, Sp. Defense, Speed, Abilities");
	    pokemonCSV.flush();
	} catch (FileNotFoundException e1) {
	    e1.printStackTrace();
	} catch (UnsupportedEncodingException e1) {
	    e1.printStackTrace();
	}
	try {
	    //Connect to the Pokemon List page and set the timeout to 60 seconds
	    Document doc = Jsoup.connect(startURL).timeout(60*1000).get();
	    //Generations are separated by tables
	    Elements tables = doc.select("table[align=center]");
	    
	    //Go through each table/generation
	    for(Element table: tables) {
		Elements rows = table.select("tr");
		//Go through each row representing a specific species
		for (Element pokemon: rows) {
		    //Skip the first row which contains the header
		    if (pokemon.equals(rows.get(0)))
			continue;
		    String name = pokemon.select("td:eq(3) > a").html();
		    String url = "http://bulbapedia.bulbagarden.net" + pokemon.select("td:eq(3) > a").attr("href");
		    //Get the types
		    String type1 = pokemon.select("td[colspan] > a > span").first().html();
		    String type2 = pokemon.select("td[colspan] > a > span").last().html();
		    //If it has only one type, set the second type to null
		    if (type2.equals(type1))
			type2 = null;
		    //System.out.println(name + ": " + url);
		    
		    //Go to Pokemon's page and set timeout to 60 seconds
		    Document pokemonDoc = Jsoup.connect(url).timeout(60*1000).get();
		    
		    //Species name
		    String species = pokemonDoc.select("a[title=Pokémon category] > span").first().html().replace(" Pokémon", "");
		    //If the species name has an "explain" abbr, take the child span instead
		    if (species.charAt(0) == '<')
			species = pokemonDoc.select("a[title=Pokémon category] > span > span").first().html().replace(" Pokémon", "");
		    
		    /*
		     * Takes the first stat table available even though they may not be the current stats due to stat increases in generation 6
		     * 
		     * These Pokemon received a 10 point stat increase: Butterfree, Beedrill, Pidgeot, Raichu, Nidoqueen, 
		     * Nidoking, Clefable, Wigglytuff, Vileplume, Poliwrath, Alakazam, Victreebel, Golem, Ampharos, 
		     * Bellossom, Azumarill, Jumpluff, Beautifly, Exploud, Staraptor, Roserade, Stoutland, Unfezant, 
		     * Gigalith, Seismitoad, Leavanny, Scolipede, and Krookodile.
		     * 
		     * Pikachu recieved a 20 point stat increase.
		     * 
		     * Will throw an error for the newer Pokemon for which stats are not yet revealed
		     */
		    Element statsTable = pokemonDoc.select("table[align=left]").first();
		    Element statRow = statsTable.select("tr:eq(2)").first();
		    String stats = "";
		    for (int i = 0; i < 6; i++) {
			String stat = statRow.select("td > table > tbody > tr > th:eq(1)").first().html();
			stats += stat + ", ";
			statRow = statRow.nextElementSibling();
		    }
		    
		    //Collects abilities of the Pokemon both regular and hidden
		    Element abilitiesTable = pokemonDoc.select("a[title=Ability]").first().parent().parent().child(1);
		    //Excludes Mega Pokemon Abilities; to include Mega Abilities use the following line instead:
		    //Elements abilities = abilitiesTable.select("tr > td");
		    Elements abilities = abilitiesTable.select("tr").first().select("td");
		    ArrayList<String> abilitiesList = new ArrayList<String>();
		    for (Element ability: abilities) {
			String abilityName = ability.select("a > span").first().html();
			
			//Ignore unimplemented Cacophony ability and empty Strings
			if (!abilityName.equals("Cacophony") && !abilityName.isEmpty())
			    abilitiesList.add(abilityName);
		    }
		    //Join the list of abilities delimited with a pipe
		    String joinedAbilities = "["+String.join(" | ", abilitiesList)+"]";
		    String pokemonLine = name + ", " + species + ", " + type1 + "/" + type2 + ", " + stats + joinedAbilities;
		    System.out.println(pokemonLine);
		    pokemonCSV.println(pokemonLine);
		    pokemonCSV.flush();
		}
	    }
	} catch (IOException e) {
	    System.err.println("Could not connect to provided URL: " + startURL);
	    //e.printStackTrace();
	    pokemonCSV.close();
	}
	pokemonCSV.close();
    }
}
