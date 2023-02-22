import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
class Acteur{
	private int numero;
	private String nom;
	private String prenom;
	
	Acteur(int num, String n, String p){
		numero = num;
		nom = n;
		prenom = p;
	}
	
	String getNom() {
		return nom;
	}
	
	String getPrenom() {
		return prenom;
	}
}

class Film{
	private int numero;
	private String titre;
	
	Film(int num, String t){
		this.numero = num;
		this.titre = t;
	}
	
	String getTitre() {
		return titre;
	}
}
public class OutilsJDBC {

public static Connection openConnection (String url) {
Connection co=null;
try {
Class.forName("oracle.jdbc.driver.OracleDriver");
co= DriverManager.getConnection(url);
}
catch (ClassNotFoundException e){
System.out.println("il manque le driver oracle");
System.exit(1);
}
catch (SQLException e) {
System.out.println("impossible de se connecter à l'url : "+url);
System.exit(1);
}
return co;
}
public static ResultSet exec1Requete (String requete, Connection co, int type){
ResultSet res=null;
try {
Statement st;
if (type==0){
st=co.createStatement();}
else {
st=co.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
ResultSet.CONCUR_READ_ONLY);
};
res= st.executeQuery(requete);
}
catch (SQLException e){
System.out.println("Problème lors de l'exécution de la requete : "+requete);
};
return res;
}
public static void closeConnection(Connection co){
try {
co.close();
System.out.println("Connexion fermée!");
}
catch (SQLException e) {
System.out.println("Impossible de fermer la connexion");
}
}
public static void main(String[] args){
	
	
	String url = "jdbc:oracle:thin:identifiant/mdp@oracle.iut-orsay.fr:1521:etudom";
	Connection co = OutilsJDBC.openConnection(url);
	
	ResultSet resultats = OutilsJDBC.exec1Requete("SELECT * FROM ens2004.Film FETCH FIRST 10 ROWS only",co,0);
	try {
		while(resultats.next()) {
			int numFilm = resultats.getInt(1);
			String nomF = resultats.getString(2);
			int real = resultats.getInt(3);
			System.out.println("Numero du film : " + numFilm);
			System.out.println("Nom du film : " + nomF);
			System.out.println("Réalisateur : " + real);	
			System.out.println("/");
		}
	}
	catch(SQLException e) {
		e.printStackTrace();
	}
	System.out.println( "Exercice 2 : ");
	ResultSet resultats2 = OutilsJDBC.exec1Requete("SELECT * FROM ens2004.individu WHERE nomIndividu = 'FONDA'",co,0);
	try {
		while(resultats2.next()) {
			int id_I = resultats2.getInt(1);
			String nom = resultats2.getString(2);
			String prenom = resultats2.getString(3);
			System.out.println("Numero de l'individu : " + id_I);
			System.out.println("Nom de l'individu : " + nom);
			System.out.println("Prénom de l'individu : " + prenom);	
			System.out.println("/");
		}
	}
	catch(SQLException e) {
		e.printStackTrace();
	}
	System.out.println( "Exercice 3 : ");
	//Ecrire le code java permettant de récupérer les films qui ont été renvoyés aujourd'hui et de les
	//afficher en indiquant leur titre et la date à laquelle ils ont été demandés.
	ResultSet resultat3 = OutilsJDBC.exec1Requete("SELECT titre, dateEmprunt FROM mdaghou.FILM NATURAL JOIN mdaghou.LOCATIONJDBC WHERE substr(dateRetour,1,10) = substr(sysdate,1,10)",co,0);
	try {
		while(resultat3.next()) {
			String titre = resultat3.getString(1);
			Date dateE = resultat3.getDate(2);
			System.out.println("titre du film : " + titre);
			System.out.println("date d'emprunt : " + dateE);	
			System.out.println("/");
		}
	}
	catch(SQLException e) {
		e.printStackTrace();
	}
	System.out.println( "Exercice 4 : ");
	System.out.println("a)");
	ArrayList<Acteur> listeActeur = new ArrayList<Acteur>();
	Scanner sc = new Scanner(System.in);
	System.out.println("Ecrire un nom d'acteur: ");
	String prenom = sc.next();
	ResultSet resultat4 = OutilsJDBC.exec1Requete("SELECT nomIndividu, prenomIndividu FROM ens2004.Individu WHERE nomIndividu = '"+ prenom+"'",co,0);
	int i = 0;
	try {
		System.out.println("Choissisez parmi les acteurs suivants :");	
		while(resultat4.next()) {
			i++;
			String nomIndividu = resultat4.getString(1);
			String prenomIndividu = resultat4.getString(2);
			listeActeur.add(new Acteur(i, nomIndividu,prenomIndividu));
			System.out.println("individu "+ i + " : " + nomIndividu + " " +prenomIndividu);	
		}
	}
	catch(SQLException e) {
		e.printStackTrace();
	}
	System.out.println("b)");
	ArrayList<Film> listeFilm = new ArrayList<Film>();
	System.out.println("Votre choix :");
	Scanner sc2 = new Scanner(System.in);
	int nb = sc.nextInt();
	System.out.println("Vous avez choisi : "+ listeActeur.get(nb-1).getPrenom()+" "+ listeActeur.get(nb-1).getNom());
	System.out.println("Il/elle a joué dans :");
	ResultSet resultat5 = OutilsJDBC.exec1Requete("SELECT titre FROM ens2004.individu NATURAL JOIN ens2004.Acteur NATURAL JOIN ens2004.Film WHERE nomIndividu = '"+listeActeur.get(nb-1).getNom()+"' AND prenomIndividu = '"+listeActeur.get(nb-1).getPrenom()+"'",co,0);
	i = 0;
	try {
		System.out.println("Choissisez parmi les acteurs suivants :");	
		while(resultat5.next()) {
			i++;
			String titre = resultat5.getString(1);
			listeFilm.add(new Film(i, titre));
			System.out.println( i + " " + titre);	
		}
	}
	catch(SQLException e) {
		e.printStackTrace();
	}
	System.out.println("c)");
	//ArrayList<Film> listeFilm = new ArrayList<Film>();
	System.out.println("Votre choix :");
	Scanner sc3 = new Scanner(System.in);
	int nb2 = sc3.nextInt();
	System.out.println("Vous avez choisi : "+ listeFilm.get(nb2-1).getTitre());
	System.out.println("Voici le nombre d'exemplaires de ce film:");
	ResultSet resultat6 = OutilsJDBC.exec1Requete("SELECT numExemplaire FROM ens2004.Film NATURAL JOIN ens2004.Exemplaire WHERE titre = '"+listeFilm.get(nb-1).getTitre()+"'",co,0);
	try {
		while(resultat6.next()) {
			int num = resultat6.getInt(1);
			System.out.println(num);	
		}
	}
	catch(SQLException e) {
		e.printStackTrace();
	}
	
}; }